#include "jcl.h"
#include "metadata.h"
#include "utils.h"
#include "sensor.h"
#include "message.h"
#include "constants.h"
#include <Arduino.h>
#include <EEPROM.h>
#include <avr/pgmspace.h>
#include <Ethernet.h>
#include <SPI.h>

JCL::JCL(char* hostIP, int hostPort, char* mac){
/*  char mac[18];
  int pos = 0;
  for (int i=0; i<6;i++){
    mac[pos++] = random(10) + 48;
    mac[pos++] = random(10) + 48;
    if (i != 5)
      mac[pos++] = '-';
  }
  mac[pos] = '\0';*/
  numSensors = 0;
  metadata = new Metadata();
  metadata->setHostIP(hostIP);
  metadata->setMAC(mac);
  char port[8];
  itoa(hostPort, port, 10);
  metadata->setHostPort(port);
  this->setEncryption(false);
  this->useEEPROM(true);
  char defaultBoardName[] = "arduino";
  this->changeBoardNickname(defaultBoardName);

  for (int i=0; i<TOTAL_SENSORS; i++)
    this->getSensors()[i] = NULL;
}

void JCL::startHost(){
  if ( isUseEEPROM() )
    readEprom();
  beginEthernet();
  connectToServer();
  // conectToBroker();
  listSensors();
  run();
}

void JCL::beginEthernet(){
  int* ip = Utils::getIPAsArray(metadata->getHostIP());
  Ethernet.begin(Utils::macAsByteArray(metadata->getMAC()), IPAddress(ip[0], ip[1], ip[2], ip[3]));
}

void JCL::setBrokerData(char* brokerIP, int brokerPort){
  metadata->setBrokerIP(brokerIP);
  metadata->setBrokerPort(brokerPort);
}

void JCL::sendBroadcastMessage(){
  int UDP_PORT = 9696;
  char packetBuffer[18];

  EthernetUDP udp;
  udp.begin(UDP_PORT);
  IPAddress broadcastIp(255,255,255,255);
  while(true) {
    udp.beginPacket(broadcastIp, UDP_PORT);
    udp.write("SERVERMAINPORT\n");
    udp.endPacket();
    int pos = 0;
    int packetSize = udp.parsePacket();
    if (packetSize) {
      //Serial.print("Received packet of size ");
      //Serial.println(packetSize);
      IPAddress remote = udp.remoteIP();
      char ip[18], port[8];
      for (int i = 0; i < 4; i++) {
        char part[4];
        sprintf(part, "%d", remote[i]);
        for (int k = 0; k < strlen(part); k++)
          ip[pos++] = part[k];
        if (i < 3)
          ip[pos++] = '.';
        else
          ip[pos] = '\0';
        //Serial.println(meta.serverIp);
      }
      udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
      //Serial.println(packetBuffer);
      pos = 0;
      for (int k=0; k<packetSize; k++)
        port[pos++] = packetBuffer[k];
      port[pos] = '\0';
      metadata->setServerIP(ip);
      metadata->setServerPort(port);
      //Serial.println(meta.serverPort);
      break;
    }
    delay(5000);
  }
  udp.stop();
}


void JCL::connectToServer(){
  int* ip = Utils::getIPAsArray(metadata->getServerIP());
  while (!client.connected()){
    client.connect(IPAddress(ip[0], ip[1], ip[2], ip[3]), atoi(metadata->getServerPort()));
    if ( millis() >= 8000 && !client.connected() ){
      sendBroadcastMessage();
      ip = Utils::getIPAsArray(metadata->getServerIP());
      client.connect(IPAddress(ip[0], ip[1], ip[2], ip[3]), atoi(metadata->getServerPort()));
      //Serial.println("Not Connected");
      // Message::printMessagePROGMEM(Constants::connectionErrorMessage);
      //delay(1000);
    }else if (client.connected()){
      //Serial.println("Connected");
      // Message::printMessagePROGMEM(Constants::connectedMessage);
    }
  }
  Message msg(this);
  msg.sendMetadata(-1);
}

void JCL::conectToBroker(){
  //PubSubClient mqttClient(mqttEthClient);
  mqtt = new PubSubClient(mqttEthClient);
  //mqtt = &mqttClient;
  mqtt->setServer(getMetadata()->getBrokerIP(), metadata->getBrokerPort());
  Serial.println(getMetadata()->getBrokerIP());
  Serial.println(getMetadata()->getBrokerPort());
  // Serial.print("Attempting MQTT connection...");
  for (int i=0;i<4;i++){
    if (mqtt->connect(getMetadata()->getBoardName())) {
      Serial.println("connected");
      break;
    }
    delay(1000);
  }
}

void JCL::run(){
  EthernetServer s(atoi(getMetadata()->getHostPort()));
  s.begin();
  while (true){
    if (!this->getMetadata()->isStandBy())
      this->makeSensing();

    int currentPosition = 0;
    this->requestListener = s.available();
    if (requestListener) {
    // Serial.println("Bytes received");
      while (this->requestListener.available()){
        message[currentPosition++] =  (char) this->requestListener.read();
   // Serial.print( (int) message[currentPosition -1] );
   // Serial.print(" // ");
   // Serial.print( message[currentPosition -1] );
   // Serial.print(" // ");
   // Serial.println(currentPosition);
      }
      Message msg(this, currentPosition);
      msg.treatMessage();
    }
  }
}

void JCL::makeSensing(){
  //  for( int i=0; i < TOTAL_SENSORS; i++){
    //  Sensor* s = this->getSensors()[i];
       for( int i=1; i < numSensors; i++){
          Sensor* s = this->getSensors()[availableSensors[i]];

      if (s!= NULL)
        checkContext(i);

      if( s != NULL && millis() - s->getLastExecuted() >= atoi(s->getDelay())){
//Serial.println("antes sensing");
unsigned long currentMillis = millis();
Serial.println(s->getPin());
        Message m(this);
        m.sensing(atoi(s->getPin()), false);
//Serial.println("depois sensing");
/*unsigned long finalMillis = millis();
if (s->count < 800){
  Serial.print(finalMillis - currentMillis);
  Serial.print("|");
  Serial.println(s->getPin());
}*/
      }
    }
  //}
  }

void JCL::checkContext(int pin){
  if( sensors[pin] != NULL ){
       for( uint16_t x=0; x < sensors[pin]->getNumContexts(); x++){
        int sensorValue;
        if ( pin >= TOTAL_DIGITAL_SENSORS ){
          sensorValue = analogRead(pin - TOTAL_DIGITAL_SENSORS);
        }
        else
          sensorValue = digitalRead(pin);
        Context *c = sensors[pin]->getEnabledContexts()[x];
        if (checkCondition(sensorValue, c->getOperators()[0], c->getThreshold()[0], sensors[pin]->getValue())){
          if (c->isMQTTContext()){
            if (mqtt->connected()){
              char mqttMessage[6];
              sprintf(mqttMessage, "%d", sensorValue);
              mqtt->publish(c->getNickname(), mqttMessage);
            }
          }else if (!c->isTriggered()){
            Serial.println("** Context reached **");
            for (uint8_t l=0; l < c->getNumActions(); l++){
              Message m(this);
              m.sendContextActionMessage(c->getEnabledActions()[l]);
            }
          }
          c->setTriggered(true);
        }else{
          if (c->isMQTTContext() && c->isTriggered()){
            char m[] = "done";
            mqtt->publish(c->getNickname(), m);
          }
          c->setTriggered(false);
        }
      }
    }
  //delay(10);
}

boolean JCL::checkCondition(int sensorValue, char *operation, char *threshold, float lastValue){
  if ( strcmp(operation, ">") == 0 ){
    if ( sensorValue > atoi(threshold) )
      return true;
  }
  else if ( strcmp(operation, "<") == 0 ){
    if ( sensorValue < atoi(threshold) )
      return true;
  }
  else if ( strcmp(operation, "=") == 0 ){
    if ( sensorValue == atoi(threshold) )
      return true;
  }
  else if ( strcmp(operation, "<=") == 0 ){
    if ( sensorValue <= atoi(threshold) )
      return true;
  }
  else if ( strcmp(operation, ">=") == 0 ){
    if ( sensorValue >= atoi(threshold) )
      return true;
  }
  else if ( strcmp(operation, "~") == 0 ){
    if ( abs(sensorValue - lastValue) >= atoi(threshold) )
      return true;
  }
  return false;
}

void JCL::configureJCLServer(char *serverIP, int serverPort){
  metadata->setServerIP(serverIP);
  char port[8];
  itoa(serverPort, port, 10);
  metadata->setServerPort(port);
}

void JCL::listSensors(){
  // Message::printMessagePROGMEM(Constants::configuredSensorsMessage);
/*  Serial.print("free: "); Serial.println(freeRam())  ;
  for (int i=0; i<TOTAL_SENSORS; i++){
    if (sensors[i] != NULL){
      Serial.print(sensors[i]->getPin());
      Serial.print("  ");
      Serial.print(sensors[i]->getSensorNickname());
      Serial.print("  ");
      Serial.print(sensors[i]->getDelay());
      Serial.print("  ");
      Serial.print(sensors[i]->getSensorSize());
      Serial.print("  ");
      Serial.print(sensors[i]->getTypeIO());
      Serial.print("  ");
      Serial.println(sensors[i]->getType());
      Serial.print(sensors[i]->getNumContexts());
      Serial.println(" contexts");
      for (int k=0; k<sensors[i]->getNumContexts();k++){
        Serial.print(sensors[i]->getEnabledContexts()[k]->getNickname());
        Serial.print("  ");
        Serial.print(sensors[i]->getEnabledContexts()[k]->getExpression());
        Serial.print("  ");
        Serial.print(sensors[i]->getEnabledContexts()[k]->getNumActions());
        Serial.print("  ");
        Serial.println(sensors[i]->getEnabledContexts()[k]->getNumExpressions());
        for (int y=0; y < sensors[i]->getEnabledContexts()[k]->getNumExpressions(); y++){
          Serial.print("   ||");
          Serial.print(sensors[i]->getEnabledContexts()[k]->getOperators()[y]);
          Serial.println(sensors[i]->getEnabledContexts()[k]->getThreshold()[y]);
        }
      }
      Serial.println();
    }
  }*/
}

int JCL::freeRam (){
  extern int __heap_start, *__brkval;
  int v;
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}

void JCL::writeEprom(){
  uint16_t addr = 0;
  unsigned int i;
  EEPROM.write(addr++, 1);  // Indicates that there's information on EEPROM

  // At the end these two bytes indicates the size of the info stored on EEPROM
  EEPROM.write(addr++, Constants::SEPARATOR);
  EEPROM.write(addr++, Constants::SEPARATOR);

  EEPROM.write(addr++, strlen(getMetadata()->getBoardName()));
  for ( i=0; i < strlen(getMetadata()->getBoardName()); i++ )
    EEPROM.write(addr++, getMetadata()->getBoardName()[i]);

  EEPROM.write(addr++, strlen(getMetadata()->getHostIP()));
  for ( i=0; i < strlen(getMetadata()->getHostIP()); i++ )
    EEPROM.write(addr++, getMetadata()->getHostIP()[i]);

  EEPROM.write(addr++, strlen(getMetadata()->getHostPort()));
  for ( i=0; i < strlen(getMetadata()->getHostPort()); i++ )
    EEPROM.write(addr++, getMetadata()->getHostPort()[i]);

  EEPROM.write(addr++, strlen(getMetadata()->getServerIP()));
  for ( i=0; i < strlen(getMetadata()->getServerIP()); i++ )
    EEPROM.write(addr++, getMetadata()->getServerIP()[i]);

  EEPROM.write(addr++, strlen(getMetadata()->getServerPort()));
  for ( i=0; i < strlen(getMetadata()->getServerPort()); i++ )
    EEPROM.write(addr++, getMetadata()->getServerPort()[i]);

  for ( int k=0; k < TOTAL_SENSORS; k++ )
    if ( sensors[k] != NULL ){
      Sensor* s = getSensors()[k];
      EEPROM.write(addr++, k);

      EEPROM.write(addr++, strlen(s->getSensorNickname()));
      for ( i=0; i < strlen(s->getSensorNickname()); i++ )
        EEPROM.write(addr++, s->getSensorNickname()[i]);

      EEPROM.write(addr++, s->getTypeIO());
      EEPROM.write(addr++, (char) s->getType());

      EEPROM.write(addr++, strlen(s->getDelay()));
      for ( i=0; i < strlen(s->getDelay()); i++ )
        EEPROM.write(addr++, s->getDelay()[i]);

      EEPROM.write(addr++, strlen(s->getSensorSize()));
      for ( i=0; i < strlen(s->getSensorSize()); i++ )
        EEPROM.write(addr++, s->getSensorSize()[i]);

      EEPROM.write(addr++, s->getNumContexts());

      for (int numCtx=0; numCtx<s->getNumContexts(); numCtx++){
        Context* ctx = s->getEnabledContexts()[numCtx];

        EEPROM.write(addr++, strlen(ctx->getNickname()));
        for ( i=0; i < strlen(ctx->getNickname()); i++ )
          EEPROM.write(addr++, ctx->getNickname()[i]);

        EEPROM.write(addr++, strlen(ctx->getExpression()));
        for ( i=0; i < strlen(ctx->getExpression()); i++ )
            EEPROM.write(addr++, ctx->getExpression()[i]);

        EEPROM.write(addr++, ctx->getNumExpressions());

        for (int numExp=0; numExp < ctx->getNumExpressions(); numExp++){
          EEPROM.write(addr++, strlen(ctx->getOperators()[numExp]));
          for ( i=0; i < strlen(ctx->getOperators()[numExp]); i++ )
            EEPROM.write(addr++, ctx->getOperators()[numExp][i]);

          EEPROM.write(addr++, strlen(ctx->getThreshold()[numExp]));
          for ( i=0; i < strlen(ctx->getThreshold()[numExp]); i++ )
              EEPROM.write(addr++, ctx->getThreshold()[numExp][i]);
        }

        EEPROM.write(addr++, ctx->getNumActions());

        for (int numActions=0; numActions<ctx->getNumActions(); numActions++){
          Action* act = ctx->getEnabledActions()[numActions];

          EEPROM.write(addr++, act->isActing());

          EEPROM.write(addr++, strlen(act->getHostIP()));
          for ( i=0; i < strlen(act->getHostIP()); i++ )
              EEPROM.write(addr++, act->getHostIP()[i]);

          EEPROM.write(addr++, strlen(act->getHostPort()));
          for ( i=0; i < strlen(act->getHostPort()); i++ )
              EEPROM.write(addr++, act->getHostPort()[i]);

          if (!act->isActing()){
            EEPROM.write(addr++, act->isUseSensorValue());

            EEPROM.write(addr++, strlen(act->getHostMAC()));
            for ( i=0; i < strlen(act->getHostMAC()); i++ )
                EEPROM.write(addr++, act->getHostMAC()[i]);

            EEPROM.write(addr++, strlen(act->getTicket()));
            for ( i=0; i < strlen(act->getTicket()); i++ )
                EEPROM.write(addr++, act->getTicket()[i]);

            EEPROM.write(addr++, act->getClassNameSize());
            for ( i=0; i < act->getClassNameSize(); i++ )
                EEPROM.write(addr++, act->getClassName()[i]);

            EEPROM.write(addr++, act->getMethodNameSize());
            for ( i=0; i < act->getMethodNameSize(); i++ )
                EEPROM.write(addr++, act->getMethodName()[i]);
          }

          EEPROM.write(addr++, act->getParamSize());
          for ( i=0; i < strlen(act->getParam()); i++ )
              EEPROM.write(addr++, act->getParam()[i]);
        }
      }
    }

  EEPROM.write(1, (addr >> 8) & 0xFF);
  EEPROM.write(2, addr & 0xFF);
}

void JCL::readEprom(){
  int addr = 0;
  int valueA = EEPROM.read(addr++);
  int numBytes;
  int numSensors = 0;
  if ( valueA != 1 )
    return;

  char c1, c2;
  c1 = EEPROM.read(addr++);
  c2 = EEPROM.read(addr++);
  numBytes = ((c1 & 0xff) << 8) | ((c2 & 0xff));

  int nChars = EEPROM.read(addr++);
  char* nameBoard = new char[ nChars + 1 ];
  for (int i=0; i<nChars; i++)
    nameBoard[i] = EEPROM.read(addr++);
  nameBoard[nChars] = '\0';
  this->getMetadata()->setBoardName(nameBoard);

  nChars = EEPROM.read(addr++);
  char* hostIP = new char[nChars + 1];
  for (int i=0; i<nChars; i++)
    hostIP[i] = EEPROM.read(addr++);
  hostIP[nChars] = '\0';
  this->getMetadata()->setHostIP(hostIP);

  nChars = EEPROM.read(addr++);
  char* hostPort = new char[nChars + 1];
  for (int i=0; i<nChars; i++)
    hostPort[i] = EEPROM.read(addr++);
  hostPort[nChars] = '\0';
  this->getMetadata()->setHostPort(hostPort);

  nChars = EEPROM.read(addr++);
  char* serverIP = new char[nChars + 1];
  for (int i=0; i<nChars; i++)
    serverIP[i] = EEPROM.read(addr++);
  serverIP[nChars] = '\0';
  this->getMetadata()->setServerIP(serverIP);

  nChars = EEPROM.read(addr++);
  char* serverPort = new char[nChars + 1];
  for (int i=0; i<nChars; i++)
    serverPort[i] = EEPROM.read(addr++);
  serverPort[nChars] = '\0';
  this->getMetadata()->setServerPort(serverPort);

  while ( addr < numBytes ){
    int pin = EEPROM.read(addr++);
    Sensor* s = new Sensor;

    this->getSensors()[pin] = s;

    char* pinStr = new char[4];
    sprintf(pinStr, "%d", pin);
    s->setPin(pinStr);

    numSensors++;

    nChars = EEPROM.read(addr++);
    char* nickname = new char[nChars + 1];
    for (int i=0; i<nChars; i++)
      nickname[i] = EEPROM.read(addr++);
    nickname[nChars] = '\0';
    s->setSensorNickname(nickname);

    s->setTypeIO(EEPROM.read(addr++));
    s->setType(EEPROM.read(addr++));

    nChars = EEPROM.read(addr++);
    char* delay = new char[nChars + 1];
    for (int i=0; i<nChars; i++)
      delay[i] = EEPROM.read(addr++);
    delay[nChars] = '\0';
    s->setDelay(delay);

    nChars = EEPROM.read(addr++);
    char* sensorSize = new char[nChars + 1];
    for (int i=0; i<nChars; i++)
      sensorSize[i] = EEPROM.read(addr++);
    sensorSize[nChars] = '\0';
    s->setSensorSize(sensorSize);

    s->setNumContexts(EEPROM.read(addr++));
    s->configurePinMode();

    for (int numCtx=0; numCtx < s->getNumContexts();numCtx++){
      Context* ctx = new Context();

      s->getEnabledContexts()[numCtx] = ctx;
      nChars = EEPROM.read(addr++);
      char* ctxName = new char[nChars + 1];
      for (int i=0; i<nChars; i++)
        ctxName[i] = EEPROM.read(addr++);
      ctxName[nChars] = '\0';
      ctx->setNickname(ctxName);

      nChars = EEPROM.read(addr++);
      char* expression = new char[nChars + 1];
      for (int i=0; i<nChars; i++)
        expression[i] = EEPROM.read(addr++);
      expression[nChars] = '\0';
      ctx->setExpression(expression);

      ctx->setNumExpressions(EEPROM.read(addr++));

      for (int numExp=0; numExp<ctx->getNumExpressions(); numExp++){
        nChars = EEPROM.read(addr++);
        char* op = new char[nChars + 1];
        for (int i=0; i<nChars; i++)
          op[i] = EEPROM.read(addr++);
        op[nChars] = '\0';
        ctx->getOperators()[numExp] = op;

        nChars = EEPROM.read(addr++);
        char* threshold = new char[nChars + 1];
        for (int i=0; i<nChars; i++)
          threshold[i] = EEPROM.read(addr++);
        threshold[nChars] = '\0';
        ctx->getThreshold()[numExp] = threshold;
      }

      ctx->setNumActions(EEPROM.read(addr++));
      for (int numActions=0; numActions<ctx->getNumActions(); numActions++){
        Action* act = new Action();
        ctx->getEnabledActions()[numActions] = act;

        act->setActing(EEPROM.read(addr++));

        nChars = EEPROM.read(addr++);
        char* hostIPAct = new char[nChars + 1];
        for (int i=0; i<nChars; i++)
          hostIPAct[i] = EEPROM.read(addr++);
        hostIPAct[nChars] = '\0';
        act->setHostIP(hostIPAct);

        nChars = EEPROM.read(addr++);
        char* hostPortAct = new char[nChars + 1];
        for (int i=0; i<nChars; i++)
          hostPortAct[i] = EEPROM.read(addr++);
        hostPortAct[nChars] = '\0';
        act->setHostPort(hostPortAct);

        if (!act->isActing()){
          act->setUseSensorValue(EEPROM.read(addr++));

          nChars = EEPROM.read(addr++);
          char* hostMACAct = new char[nChars + 1];
          for (int i=0; i<nChars; i++)
            hostMACAct[i] = EEPROM.read(addr++);
          hostMACAct[nChars] = '\0';
          act->setHostMAC(hostMACAct);

          nChars = EEPROM.read(addr++);
          char* ticket = new char[nChars + 1];
          for (int i=0; i<nChars; i++)
            ticket[i] = EEPROM.read(addr++);
          ticket[nChars] = '\0';
          act->setTicket(ticket);

          nChars = EEPROM.read(addr++);
          act->setClassNameSize(nChars);
          char* className = new char[nChars + 1];
          for (int i=0; i<nChars; i++)
            className[i] = EEPROM.read(addr++);
          className[nChars] = '\0';
          act->setClassName(className);

          nChars = EEPROM.read(addr++);
          act->setMethodNameSize(nChars);
          char* methodName = new char[nChars + 1];
          for (int i=0; i<nChars; i++)
            methodName[i] = EEPROM.read(addr++);
          methodName[nChars] = '\0';
          act->setMethodName(methodName);
        }
        nChars = EEPROM.read(addr++);
        act->setParamSize(nChars);
        char* param = new char[nChars];

        for (int i=0; i<nChars-1; i++)
          param[i] = EEPROM.read(addr++);
        param[nChars] = '\0';
        act->setParam(param);
      }
    }

  }
  char* nSensors = new char[4];
  sprintf(nSensors, "%d", numSensors);
  this->getMetadata()->setNumConfiguredSensors(nSensors);
  Serial.println(numSensors);
}

void JCL::changeBoardNickname(char *boardName){
  metadata->setBoardName(boardName);
}

void JCL::setEncryption(bool encryption){
  this->encryption = encryption;
}

bool JCL::isEncryption(){
  return this->encryption;
}

EthernetClient JCL::getClient(){
  return this->client;
}

EthernetClient JCL::getRequestListener(){
  return this->requestListener;
}

Sensor** JCL::getSensors(){
  return this->sensors;
}

Metadata* JCL::getMetadata(){
  return this->metadata;
}

char* JCL::getKey(){
  return this->key;
}

void JCL::setKey(char *key){
  this->key = key;
}

int JCL::getTotalSensors(){
    return TOTAL_SENSORS;
}

int JCL::getTotalDigitalSensors(){
  return TOTAL_DIGITAL_SENSORS;
}

void JCL::useEEPROM(bool useEEPROM){
    eeprom = useEEPROM;
}

bool JCL::isUseEEPROM(){
  return this->eeprom;
}

PubSubClient* JCL::getMQTTClient(){
  return mqtt;
}
