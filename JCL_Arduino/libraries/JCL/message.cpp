#include "jcl.h"
#include "message.h"
#include "sensor.h"
#include "crypt.h"
#include "constants.h"
#include <Regexp.h>
#include "context.h"
#include "utils.h"

 void(* resetFunc1) (void) = 0;

Message::Message( JCL* jcl, int messageSize){
  this->jcl = jcl;
  this->messageSize = messageSize;
}

Message::Message(JCL* jcl){
  this->jcl = jcl;
}

void Message::treatMessage(){
  int key = jcl->message[4] & 0x3F;
  int crypt = ((jcl->message[4] >> 6) & 0x03);
  int typePosition = 14;
  Crypt c;
  if ( crypt == 1 ){
    this->messageSize = c.decryptMessage(this->messageSize, jcl);
    if ( messageSize == -1 ){
      sendResultBool(false);
      return;
    }
    jcl->message[4] = key;
  }

  switch(jcl->message[typePosition]){
    case 44:{ // SensorNow
      Serial.println("SensorNow");
      int msgSize = jcl->message[3];
      int pos = msgSize - 1;
      while (jcl->message[pos]!=74)
      pos--;
      int nChars = jcl->message[++pos];
      char pinC[nChars+1];
      pos++;
      for (uint8_t x=0; x<nChars; x++)
      pinC[x] = jcl->message[x+pos];
      pinC[nChars] = '\0';
      sensing( atoi(pinC), true );
      break;
    }
    case 45:{   // Message TurnOn
      // printMessagePROGMEM(Constants::turnOnMessage);
      Serial.println("TurnOn");
      jcl->getMetadata()->setStandBy(false);

      sendMetadata(40);
      sendResult(101);
      break;
    }
    case 46:{  // Message StandBy
      // printMessagePROGMEM(Constants::standByMessageListen);
      Serial.println("StandBy");
      jcl->getMetadata()->setStandBy(true);
      sendMetadata(40);
      sendResult(102);
      break;
    }
    case 47:{   //SetMetadata
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::setMetadataMessage);
        Serial.println("SetMetadata");
        bool b = setMetadata();
        if ( b ){
          sendMetadata(40);
          sendResultBool(true);
          jcl->writeEprom();
        }else{
          jcl->readEprom();
          sendResultBool(false);
        }
      }
      break;
    }
    case 49:{  // Message SetSensor
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::setSensorMessage);
        // Serial.println("SetSensor");
        bool b = messageSetSensor();
        if (b){
          sendMetadata(40);
          jcl->writeEprom();
        }
        else
          jcl->readEprom();
        sendResultBool(b);
      }
      break;
    }
    case 50:{ // RemoveSensor
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::removeSensorMessage);
        Serial.println("RemoveSensor");
        int msgSize = jcl->message[3];
        int pos = msgSize - 1;
        while (jcl->message[pos] != 74)
        pos--;
        pos++;
        int nChars = jcl->message[pos];
        char pinC[nChars+1];
        pos++;
        for (uint8_t x=0; x<nChars; x++)
        pinC[x] = jcl->message[x+pos];
        pinC[nChars] = '\0';

        Sensor* s = jcl->getSensors()[atoi(pinC)];
        if ( s == NULL)
        sendResultBool(false);
        else{
          s->deleteSensor();
          char nSensors[4];
          sprintf( nSensors, "%d", atoi(jcl->getMetadata()->getNumConfiguredSensors()) - 1 );
          jcl->getMetadata()->setNumConfiguredSensors(nSensors);
          jcl->getSensors()[atoi(pinC)] = NULL;
          sendMetadata(40);
          jcl->writeEprom();
          sendResultBool(true);
        }
      }
      break;
    }
    case 51:{   // Acting
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::actingMessage);
        Serial.println("Acting");
        int msgSize = jcl->message[3];
        int pos = msgSize - 1;
        int value = 0;
        int pin = 0;
        int k, j;
        char command[12];
        pos = 0;
        while ( jcl->message[pos] != 74 )
        pos++;
        k = jcl->message[++pos];

        if (k == 1)
        pin = jcl->message[pos+1] - 48;
        else{
          pin = 10 * (jcl->message[pos+1]- 48) ;
          pin += jcl->message[pos+2] - 48 ;
        }

        if (!Sensor::validPin(pin))
          sendResultBool(false);
        if (jcl->getSensors()[pin] == NULL || jcl->getSensors()[pin]->getTypeIO() == Constants::CHAR_INPUT )
          sendResultBool(false);

        while ( jcl->message[pos] != 16 )
        pos++;
        pos++;
        pos++;
        uint8_t numCmd = (int) jcl->message[pos];
        pos++;
        pos++;
        boolean b = true;
        Sensor* s = jcl->getSensors()[pin];
        for (k=0; k<numCmd; k++){
          uint8_t nChars = jcl->message[pos++];
          for (j=0; j<nChars && j <12; j++)
          command[j] = jcl->message[pos++];
          command[j] = '\0';
          pos++;
          float f = atof(command);
          value = (int) f;
          if ( !s->acting(value) ){
            b = false;
            break;
          }
        }
        sendResultBool(b);
      }
      break;
    }
    case 52:{   // Restart
      if (jcl->getMetadata()->isStandBy()){
        sendResult(101);
      }else{
        sendResult(100);
        unregister();
        resetFunc1();
      }
      break;
    }
    case 53:{   // setEncryption
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::setEncryptionMessage);
        Serial.println("setEncryption");
        if (jcl->message[21] == 'f')
          jcl->setEncryption(false);
        else
          jcl->setEncryption(true);
        sendResultBool(true);
      }
      break;
    }
    case 54:{   // RegisterContext
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::registerContextMessage);
        Serial.println("RegisterContext");
        bool b = registerContext(false);
        if (b)
          jcl->writeEprom();
        sendResultBool(b);
        jcl->listSensors();
      }
      break;
    }
    case 55:{   //  AddContextAction
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::addContextActionMessage);
        Serial.println("AddContextAction1");
        uint16_t pos = 17;
        while (jcl->message[pos] != 74)
          pos++;
        pos++;
        uint8_t nChars=jcl->message[pos++];
        char contextName[nChars+1];
        for (uint8_t x=0; x < nChars; x++)
          contextName[x] = jcl->message[pos + x];
        contextName[nChars] = '\0';

        Context *ctx = NULL;
        for (uint16_t x=0; x<TOTAL_SENSORS;x++){
          Sensor* s = jcl->getSensors()[x];
          if (s->getNumContexts() > 0){
            for (uint8_t ctxNumber=0; ctxNumber<s->getNumContexts(); ctxNumber++)
              if ( strcmp(s->getEnabledContexts()[ctxNumber]->getNickname(), contextName) == 0 ){
                ctx = s->getEnabledContexts()[ctxNumber];
                break;
              }
          }
        }

        if ( ctx == NULL )
          sendResultBool(false);
        else{
          Action *act = new Action();
          pos += nChars;
          while (jcl->message[pos] != 74)
          pos++;
          pos++;
          nChars = jcl->message[pos++];
          char* hostIP = (char*)malloc(sizeof(char)*nChars+1);
          for (uint8_t x=0; x < nChars; x++)
          hostIP[x] = jcl->message[pos + x];
          hostIP[nChars] = '\0';
          act->setHostIP(hostIP);

          pos += nChars;
          char* hostPort = new char[nChars+1];
          while (jcl->message[pos] != 74)
            pos++;
          pos++;
          nChars = jcl->message[pos++];
          for (uint8_t x=0; x < nChars; x++)
            hostPort[x] = jcl->message[pos + x];
          hostPort[nChars] = '\0';
          act->setHostPort(hostPort);

          pos += nChars;
          while (jcl->message[pos] != 74)
          pos++;
          pos++;
          nChars = jcl->message[pos++];
          char* hostMAC = new char[nChars+1];
          for (uint8_t x=0; x < nChars; x++)
              hostMAC[x] = jcl->message[pos + x];
          hostMAC[nChars] = '\0';
          act->setHostMAC(hostMAC);

          pos += nChars;
          while (jcl->message[pos] != 74)
          pos++;
          pos++;
          nChars = jcl->message[pos++];
          char* superPeerPort = new char[nChars+1];
          for (uint8_t x=0; x < nChars; x++)
              superPeerPort[x] = jcl->message[pos + x];
          superPeerPort[nChars] = '\0';
          act->setSuperPeerPort(superPeerPort);

          pos += nChars;
          while (jcl->message[pos] != 74)
            pos++;
          pos++;
          nChars = jcl->message[pos++];
          char* ticket = new char[nChars+1];
          for (uint8_t x=0; x < nChars; x++)
            ticket[x] = jcl->message[pos + x];
          ticket[nChars] = '\0';
          act->setTicket(ticket);

          pos += nChars;
          while (jcl->message[pos] != 74)
            pos++;
          pos++;
          nChars = jcl->message[pos++];
          if (jcl->message[pos] == 't')
            act->setUseSensorValue(true);
          else
            act->setUseSensorValue(false);

          pos += nChars;
          while (jcl->message[pos] != 74)
            pos++;
          pos++;
          nChars = jcl->message[pos++];
          char* className = new char[nChars+1];
          for (uint8_t x=0; x < nChars; x++)
            className[x] = jcl->message[pos + x];
          className[nChars] = '\0';
          act->setClassName(className);
          act->setClassNameSize(nChars);

          pos += nChars;
          while (jcl->message[pos] != 74)
            pos++;
          nChars = jcl->message[++pos];
          char* methodName = new char[nChars+1];
          for (uint8_t x=0; x < nChars; x++)
            methodName[x] = jcl->message[pos + x + 1];
          methodName[nChars] = '\0';
          act->setMethodName(methodName);
          act->setMethodNameSize(nChars);

          while (jcl->message[pos] != 122)
            pos++;
          pos--;
          char* param = new char[messageSize - pos + 1];
          for (int x=pos; x < messageSize; x++)
            param[x-pos] = jcl->message[x];
          param[messageSize - pos] = '\0';
          act->setParam(param);
          act->setParamSize(messageSize - pos);
          act->setActing(false);
          ctx->getEnabledActions()[ctx->getNumActions()] = act;
          ctx->setNumActions(ctx->getNumActions() + 1);
          jcl->writeEprom();
          sendResultBool(true);
        }
      }
      break;
    }
    case 56:{   // AddContextAction
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::addContextActionMessage);
        Serial.println("AddContextAction");
        /*uint8_t nChars=jcl->message[43];
        char contextName[nChars+1];
        for (uint8_t x=0; x < nChars; x++)
        contextName[x] = jcl->message[44 + x];
        contextName[nChars] = '\0';*/
        Context *ctx = NULL;
        uint16_t pos = 17;
        while (jcl->message[pos] != 74)
          pos++;
        pos++;
        uint8_t nChars=jcl->message[pos++];
        char contextName[nChars+1];
        for (uint8_t x=0; x < nChars; x++)
          contextName[x] = jcl->message[pos + x];
        contextName[nChars] = '\0';
        for (uint16_t x=0; x<TOTAL_SENSORS;x++)
        if (jcl->getSensors()[x]->getNumContexts() > 0){
          for (uint8_t ctxNumber=0; ctxNumber<jcl->getSensors()[x]->getNumContexts(); ctxNumber++)
          if ( strcmp(jcl->getSensors()[x]->getEnabledContexts()[ctxNumber]->getNickname(), contextName) == 0 ){
            ctx = jcl->getSensors()[x]->getEnabledContexts()[ctxNumber];
            break;
          }
        }

        if ( ctx == NULL )
        sendResultBool(false);
        else{
          Action* act = new Action();
          uint16_t pos = 47 + nChars;
          nChars = jcl->message[pos++];
          char* hostIP = (char*)malloc(sizeof(char)*nChars+1);
          for (uint8_t x=0; x<nChars; x++)
          hostIP[x] = jcl->message[pos + x];
          hostIP[nChars] = '\0';
          act->setHostIP(hostIP);

          pos +=nChars;
          while (jcl->message[pos] != 74)
            pos++;
          nChars = jcl->message[++pos];
          pos++;
          char* hostPort = (char*)malloc(sizeof(char)*nChars+1);
          for (uint8_t x=0; x<nChars; x++)
            hostPort[x] = jcl->message[pos + x];
          hostPort[nChars] = '\0';
          act->setHostPort(hostPort);

          pos +=nChars;
          while (jcl->message[pos] != 74)
            pos++;
          nChars = jcl->message[++pos];
          pos++;
          char* portSuperPeer = (char*)malloc(sizeof(char)*nChars+1);
          for (uint8_t x=0; x<nChars; x++)
            portSuperPeer[x] = jcl->message[pos + x];
          portSuperPeer[nChars] = '\0';
          act->setSuperPeerPort(portSuperPeer);

          pos += nChars+95;
          while (jcl->message[pos] != 3)
            pos++;
        /*  pos++;
          while (jcl->message[pos] != 3)
            pos++;*/

          nChars = messageSize - pos;
          char* param = (char*)malloc(sizeof(char)*nChars+1);
          for (uint16_t x=0; x<nChars; x++)
            param[x] = jcl->message[pos + x];
          param[nChars] = '\0';
          act->setParam(param);
          act->setParamSize(nChars);
/*for (int jj=0; jj<nChars; jj++){
  Serial.print((int) act->getParam()[jj]);
  Serial.print("  ");
  Serial.print((char) act->getParam()[jj]);
  Serial.println();
}

Serial.println();*/
          act->setActing(true);
          ctx->getEnabledActions()[ctx->getNumActions()] = act;
          ctx->setNumActions(ctx->getNumActions() + 1);
          jcl->writeEprom();
          sendResultBool(true);
        }
      }
      break;
    }
    case 61:{
          if (jcl->getMetadata()->isStandBy()){
            sendResultBool(false);
          }else{
            // printMessagePROGMEM(Constants::registerContextMessage);
            Serial.println("RegisterContext");
            bool b = registerContext(true);
            if (b)
              jcl->writeEprom();
            sendResultBool(b);
            jcl->listSensors();
          }
          break;
    }
    case 62:{
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::registerContextMessage);
        Serial.println("UnregisterContext");
        bool b = unregisterContext(false);
        if (b)
          jcl->writeEprom();
        sendResultBool(b);
        jcl->listSensors();
      }
      break;
    }
    case 63:{
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::registerContextMessage);
        Serial.println("UnregisterMQTTContext");
        bool b = unregisterContext(true);
        if (b)
          jcl->writeEprom();
        sendResultBool(b);
        jcl->listSensors();
      }
      break;
    }
    case 64:{
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::registerContextMessage);
        Serial.println("remove Context action");
        bool b = removeContextAction(true);
        if (b)
          jcl->writeEprom();
        sendResultBool(b);
        jcl->listSensors();
      }
      break;
    }
    case 65:{
      if (jcl->getMetadata()->isStandBy()){
        sendResultBool(false);
      }else{
        // printMessagePROGMEM(Constants::registerContextMessage);
        Serial.println("remove Context action");
        bool b = removeContextAction(false);
        if (b)
          jcl->writeEprom();
        sendResultBool(b);
        jcl->listSensors();
      }
      break;
    }
    default:{
      // printMessagePROGMEM(Constants::commandUnkwownMessage);
      Serial.println("Command unkwnown");
      Serial.println((int) jcl->message[typePosition]);
      break;
    }
  }
}

int Message::completeHeader(int messageSize, int messageType, boolean sendMAC, char* hostMAC, int superPeerPort){
  int addBytes;
  char* regKey;
  Crypt c;
  char* iv;;
  if (jcl->isEncryption()){
    iv = c.generateIV();
    messageSize = c.cryptMessage(messageSize, jcl, iv, Crypt::hash1);
    regKey = c.generateRegistrationKey(jcl->message, iv, messageSize, Crypt::hash2);
  }

  if (sendMAC && jcl->isEncryption())
    addBytes = 61;
  else if (!sendMAC && jcl->isEncryption())
    addBytes = 53;
  else if (sendMAC && !jcl->isEncryption())
    addBytes = 13;
  else
    addBytes = 5;

  for (int k=messageSize-1; k>=0; k--)
    jcl->message[k+addBytes] = jcl->message[k];

  uint8_t currentPosition = 4;
  if (jcl->isEncryption())
    jcl->message[currentPosition++] =  messageType + 64;
  else
    jcl->message[currentPosition++] =  messageType;

  if (sendMAC){
    if (superPeerPort == 0 ){
      for (int i=0; i<8; i++)
        jcl->message[currentPosition++] = 0;
    }else{
      char* a = (char*)&superPeerPort;
      jcl->message[currentPosition++] = a[1];
      jcl->message[currentPosition++] = a[0];

/*      char mmac[17];
      for (int i=0; i<17; i++)
        mmac[i] = hostMAC[i];
Serial.println(mmac);*/
      unsigned char mac[6];
      sscanf(hostMAC, "%hhx-%hhx-%hhx-%hhx-%hhx-%hhx", &mac[0], &mac[1], &mac[2], &mac[3], &mac[4], &mac[5]);
      for (int i=0; i<6; i++)
        jcl->message[currentPosition++] = mac[i];
    }
  }

  if (jcl->isEncryption()){
    for (uint8_t it=0; it<16; it++)
      jcl->message[currentPosition++] = iv[it];
    for (uint8_t it=0; it<32; it++)
      jcl->message[currentPosition++] = regKey[it];
  }

  int totalSize = messageSize +addBytes - 4;
  char *value = (char*)&totalSize;

  if ( totalSize <= 255 ){  // If the size is less then 256, we only need one byte to store the size
    jcl->message[0] = 0;
    jcl->message[1] = 0;
    jcl->message[2] = 0;
    jcl->message[3] = value[0];
  }else{       // Otherwise we use two bytes to store the size (up to 32000 bytes)
    jcl->message[0] = 0;
    jcl->message[1] = 0;
    jcl->message[2] = value[1];
    jcl->message[3] = value[0];
  }
  return messageSize + addBytes;
}

void Message::receiveServerAnswer(){
  while (!jcl->getClient().available());
  while (jcl->getClient().available())
    jcl->getClient().read();
}

void Message::receiveRegisterServerAnswer(){
  char *key;
  if (jcl->getClient()) {
    int16_t pos = 0;
    while (!jcl->getClient().available());
    while (jcl->getClient().available())
      jcl->message[pos++] = (char) jcl->getClient().read();

    key = (char *) malloc(sizeof(char)*18);
    for (uint16_t x = 0; x < 17; x++)
      key[x] = jcl->message[pos - 17 + x];
    key[17] = '\0';
  }
  jcl->setKey(key);
  Crypt::update(jcl);
}

/*void Message::printMessagePROGMEM(const char *m){
  int len = strlen_P(m);
  char c;
  for (int k = 0; k < len; k++)
  {
    c =  pgm_read_byte_near(m + k);
    Serial.print(c);
  }
  Serial.println();
}*/

bool Message::messageSetSensor(){
  int j;
  char values[8];
  Sensor* s;
  s = new Sensor();
  int position = 20;

  int nChars = jcl->message[position++];
  char* nameSensor = (char*)malloc(sizeof(char)*nChars+1);
  for ( j = 0; j < nChars; j++ ){
    nameSensor[j] = jcl->message[position++];
  }
  nameSensor[nChars] = '\0';
  s->setSensorNickname(nameSensor);

  position++;
  nChars = jcl->message[position++];
  char* pin = (char*)malloc(sizeof(char)*nChars+1);
  for ( j = 0; j < nChars; j++ ){
    pin[j] = jcl->message[position++];
  }
  pin[nChars] = '\0';
  s->setPin(pin);

  if ( !Sensor::validPin(atoi(s->getPin()))) {
    s->deleteSensor();
    return false;
  }

  /* Sensor Size */
  position++;
  nChars = jcl->message[position++];
  char* sensorSize = (char*)malloc(sizeof(char)*nChars+1);
  for ( j = 0; j < nChars; j++ )
  sensorSize[j] = jcl->message[position++];
  sensorSize[nChars] = '\0';
  s->setSensorSize(sensorSize);

  position++;
  nChars = jcl->message[position++];
  char* delay = (char*)malloc(sizeof(char)*nChars+1);
  for ( j = 0; j < nChars; j++ )
  delay[j] = jcl->message[position++];
  delay[nChars] = '\0';
  s->setDelay(delay);

  if ( position != jcl->message[3] )  {
    position++;
    nChars = jcl->message[position++];
    for ( j = 0; j < nChars; j++ )
    values[j] = jcl->message[position++];
    values[nChars] = '\0';

    s->setTypeIO(toUpperCase(values[0]));
    if ( s->getTypeIO() != Constants::CHAR_OUTPUT && s->getTypeIO() != Constants::CHAR_INPUT)
    s->setTypeIO(Constants::CHAR_INPUT);
  }else
  s->setTypeIO(Constants::CHAR_INPUT);

  position++;
  nChars = jcl->message[position++];
  for ( j = 0; j < nChars; j++ )
  values[j] = jcl->message[position++];
  values[nChars] = '\0';
  s->setType(atoi(values));

  s->setLastExecuted(0);

  if (jcl->getSensors()[atoi(s->getPin())] != NULL){
    jcl->getSensors()[atoi(s->getPin())]->deleteSensor();
    jcl->getSensors()[atoi(s->getPin())] = NULL;
  }else{
    char nSensors[4];
    sprintf( nSensors, "%d", atoi(jcl->getMetadata()->getNumConfiguredSensors()) + 1 );
    jcl->getMetadata()->setNumConfiguredSensors(nSensors);
  }

  jcl->getSensors()[atoi(s->getPin())] = s;
  s->configurePinMode();
  jcl->listSensors();
jcl->availableSensors[jcl->numSensors++] = atoi((s->getPin()));
  return true;
}

void Message::sendResultBool(boolean result){
  int size;
  int currentPosition = 0;

  jcl->message[currentPosition++] = 18;
  jcl->message[currentPosition++] = 4;
  jcl->message[currentPosition++] = 8;
  jcl->message[currentPosition++] =  1;
  jcl->message[currentPosition++] =  16;
  if ( result )
    jcl->message[currentPosition++] = 1;
  else
    jcl->message[currentPosition++] = 0;

  size = completeHeader(currentPosition, 11, false, jcl->getMetadata()->getMAC(), 0);

  jcl->getRequestListener().write(jcl->message, size);
  jcl->getRequestListener().flush();
}

void Message::sendResult(int result){
  int size;
  int currentPosition = 0;

  jcl->message[currentPosition++] = 8;
  jcl->message[currentPosition++] = result;

  size = completeHeader(currentPosition, 0, false, jcl->getMetadata()->getMAC(), 0);
  jcl->getRequestListener().write(jcl->message, size);
  jcl->getRequestListener().flush();
}

int Message::encode_unsigned_varint(uint8_t *const buffer, uint64_t value)
{
  int encoded = 0;
  do{
    uint8_t next_byte = value & 0x7F;
    value >>= 7;
    if (value)
    next_byte |= 0x80;
    buffer[encoded++] = next_byte;
  } while (value);
  return encoded;
}

int Message::encode_signed_varint(uint8_t *const buffer, int64_t value)
{
  uint64_t uvalue;
  uvalue = uint64_t( value < 0 ? ~(value << 1) : (value << 1) );
  return encode_unsigned_varint( buffer, uvalue );
}

void Message::sendMetadata(int messageType){
  int totalSize;
  int size;
  int i;

  /* To calculate the size of the metadata ENABLE_SENSOR*/
  int enableSensorSize = 0;
  for (i=0; i < JCL::getTotalSensors(); i++)
    if ( jcl->getSensors()[i] != NULL )
      enableSensorSize += strlen(jcl->getSensors()[i]->getPin());

  enableSensorSize += atoi(jcl->getMetadata()->getNumConfiguredSensors());
enableSensorSize++;

  int totalSizePins = 0, totalSizeNameSensor = 0, totalTimeSensor = 0, totalDirSensor = 0, totalSensorSize = 0;
  for (i=0; i < JCL::getTotalSensors(); i++){
    if ( jcl->getSensors()[i] != NULL ){
      totalSizePins += strlen(jcl->getSensors()[i]->getPin());
      totalSizeNameSensor += strlen(jcl->getSensors()[i]->getSensorNickname());
      totalTimeSensor += strlen(jcl->getSensors()[i]->getDelay());
      totalSensorSize += strlen(jcl->getSensors()[i]->getSensorSize());
      totalDirSensor++;
    }
  }

  int currentPosition = 0;
  jcl->message[currentPosition++] =  8; // Indicates first field

  /* The next fields indicates the value -1. It takes multiple bytes to indicate negative field because
  Protocol Buffer uses base 128 varints */
  if (messageType == -1){
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  -1;
    jcl->message[currentPosition++] =  1;
  }
  else
    jcl->message[currentPosition++] =  messageType;

  jcl->message[currentPosition++] =  18; // Indicates second field

  int sensorsSize = 0;
  if ( atoi(jcl->getMetadata()->getNumConfiguredSensors()) != 0 ){
    sensorsSize += strlen(Constants::enableSensorMessage) + enableSensorSize + 4 + 2  +
    (strlen(Constants::sensorAliasMessage) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) + totalSizePins + totalSizeNameSensor + ((4 + 2) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) +
    (strlen(Constants::sensorDirMessage) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) + totalSizePins + totalDirSensor + ((4 + 2) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) +
    (strlen(Constants::sensorSizeMessage) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) + totalSizePins + totalSensorSize + ((4 + 2) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) +
    (strlen(Constants::sensorSamplingMessage) * atoi(jcl->getMetadata()->getNumConfiguredSensors())) + totalSizePins + totalTimeSensor + ((4 + 2) * atoi(jcl->getMetadata()->getNumConfiguredSensors()));
  }
else
sensorsSize += strlen(Constants::enableSensorMessage) + enableSensorSize + 4 + 2;

  int standBySize = jcl->getMetadata()->isStandBy()?strlen(Constants::trueMessage):strlen(Constants::falseMessage);
  // Calculate size to append to the message
  totalSize =
              strlen(Constants::portMessage) + strlen(jcl->getMetadata()->getHostPort()) + 4 + 2 +
              strlen(Constants::coreMessage) + strlen(Constants::coreValue) + 4 + 2 +
              strlen(Constants::deviceTypeMessage) + strlen(Constants::deviceTypeValue) + 4 + 2 +
              strlen(Constants::deviceIDMessage) + strlen(jcl->getMetadata()->getBoardName()) + 4 + 2 +
              strlen(Constants::macMessage) + strlen(jcl->getMetadata()->getMAC()) + 4 + 2 +
              strlen(Constants::Constants::devicePlatformMessage) + strlen(Constants::devicePlatformValue) + 4 + 2 +
              strlen(Constants::numberSensorsMessage) + strlen(jcl->getMetadata()->getNumConfiguredSensors()) + 4 + 2 +
              strlen(Constants::standByMessage) + standBySize + 4 + 2 +
              sensorsSize;

  if (messageType == -1){
    totalSize += strlen(Constants::ipMessage) +strlen(jcl->getMetadata()->getHostIP()) + 4 + 2;
  }
  /* If total size is less then 128 we only need one byte to store it */
  if ( totalSize < 128 )
    jcl->message[currentPosition++] = totalSize;
  else{
    int lastByte = (int) totalSize / 128;
    int rest = 128 - ( (int) totalSize % 128 );
    rest *= -1;
    jcl->message[currentPosition++] = rest;
    jcl->message[currentPosition++] = lastByte;
  }

  /* The following values are the Metadata in the Map format (Key, Value) */

  /* Setting the value of the DEVICE_ID */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::deviceIDMessage) + strlen(jcl->getMetadata()->getBoardName()) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::deviceIDMessage);
  for ( unsigned int i=0; i < strlen(Constants::deviceIDMessage); i++ )
    jcl->message[currentPosition++] = Constants::deviceIDMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getBoardName());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getBoardName()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getBoardName()[i];

  /* Setting the value of the DEVICE_TYPE */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::deviceTypeMessage) + strlen(Constants::deviceTypeValue) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::deviceTypeMessage);
  for (unsigned int i=0; i < strlen(Constants::deviceTypeMessage); i++ )
    jcl->message[currentPosition++] = Constants::deviceTypeMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(Constants::deviceTypeValue);
  for ( unsigned int i=0; i < strlen(Constants::deviceTypeValue); i++ )
    jcl->message[currentPosition++] = Constants::deviceTypeValue[i];

  /* Setting the value of the PORT */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::portMessage) + strlen(jcl->getMetadata()->getHostPort()) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::portMessage);
  for ( unsigned int i=0; i < strlen(Constants::portMessage); i++ )
    jcl->message[currentPosition++] = Constants::portMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getHostPort());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getHostPort()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getHostPort()[i];

   if (messageType == -1){
    /* Setting the value of the IP */
    jcl->message[currentPosition++] =  10;
    jcl->message[currentPosition++] = strlen(Constants::ipMessage) + strlen(jcl->getMetadata()->getHostIP()) + 4;
    jcl->message[currentPosition++] =  10;
    jcl->message[currentPosition++] = strlen(Constants::ipMessage);
    for ( unsigned int i=0; i < strlen(Constants::ipMessage); i++ )
      jcl->message[currentPosition++] = Constants::ipMessage[i];
    jcl->message[currentPosition++] =  18;
    jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getHostIP());
    for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getHostIP()); i++ )
      jcl->message[currentPosition++] = jcl->getMetadata()->getHostIP()[i];
   }

  /* Setting the value of the CORE(S) */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::coreMessage) + strlen(Constants::coreValue) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::coreMessage);
  for ( unsigned int i=0; i < strlen(Constants::coreMessage); i++ )
    jcl->message[currentPosition++] = Constants::coreMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(Constants::coreValue);
  for ( unsigned int i=0; i < strlen(Constants::coreValue); i++ )
    jcl->message[currentPosition++] = Constants::coreValue[i];

  /* Setting the value of the MAC */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::macMessage) + strlen(jcl->getMetadata()->getMAC()) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::macMessage);
  for ( unsigned int i=0; i < strlen(Constants::macMessage); i++ )
    jcl->message[currentPosition++] = Constants::macMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getMAC());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getMAC()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getMAC()[i];

  /* Setting the value of the Device Platform */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::devicePlatformMessage) + strlen(Constants::devicePlatformValue) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::devicePlatformMessage);
  for ( unsigned int i=0; i < strlen(Constants::devicePlatformMessage); i++ )
    jcl->message[currentPosition++] = Constants::devicePlatformMessage[i];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(Constants::devicePlatformValue);
  for ( unsigned int i=0; i < strlen(Constants::devicePlatformValue); i++ )
    jcl->message[currentPosition++] = Constants::devicePlatformValue[i];

  /* Setting the value of the NUMBER_SENSORS */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::numberSensorsMessage) + strlen(jcl->getMetadata()->getNumConfiguredSensors()) + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::numberSensorsMessage);
  for ( unsigned int k=0; k < strlen(Constants::numberSensorsMessage); k++ )
    jcl->message[currentPosition++] = Constants::numberSensorsMessage[k];
  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getNumConfiguredSensors());
  for ( unsigned int k=0; k < strlen(jcl->getMetadata()->getNumConfiguredSensors()); k++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getNumConfiguredSensors()[k];

  /* Setting the value of the STANDBY */
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::standByMessage) + standBySize + 4;
  jcl->message[currentPosition++] =  10;
  jcl->message[currentPosition++] = strlen(Constants::standByMessage);
  for ( unsigned int k=0; k < strlen(Constants::standByMessage); k++ )
    jcl->message[currentPosition++] = Constants::standByMessage[k];
  jcl->message[currentPosition++] =  18;
  if (jcl->getMetadata()->isStandBy()){
    jcl->message[currentPosition++] = strlen(Constants::trueMessage);
    for ( unsigned int k=0; k < strlen(Constants::trueMessage); k++ )
      jcl->message[currentPosition++] = Constants::trueMessage[k];
  }else{
    jcl->message[currentPosition++] = strlen(Constants::falseMessage);
    for ( unsigned int k=0; k < strlen(Constants::falseMessage); k++ )
      jcl->message[currentPosition++] = Constants::falseMessage[k];
  }



//  if (atoi(jcl->getMetadata()->getNumConfiguredSensors()) != 0){

    /* Setting the value of the ENABLE_SENSOR */
    jcl->message[currentPosition++] =  10;
    jcl->message[currentPosition++] = strlen(Constants::enableSensorMessage) + enableSensorSize + 4;
    jcl->message[currentPosition++] =  10;
    jcl->message[currentPosition++] = strlen(Constants::enableSensorMessage);
    for ( unsigned int k=0; k < strlen(Constants::enableSensorMessage); k++ )
      jcl->message[currentPosition++] = Constants::enableSensorMessage[k];
    jcl->message[currentPosition++] =  18;
    jcl->message[currentPosition++] = enableSensorSize;

    for ( int k=0; k < JCL::getTotalSensors(); k++ ){
      if ( jcl->getSensors()[k] != NULL ){
        for (unsigned i=0; i < strlen(jcl->getSensors()[k]->getPin()); i++ )
          jcl->message[currentPosition++] =  jcl->getSensors()[k]->getPin()[i];
        jcl->message[currentPosition++] = ';';
      }
    }
  jcl->message[currentPosition++] = ';';

    /* Setting the value of the SENSOR_ALIAS */
    for (i=0; i< JCL::getTotalSensors(); i++){
      if ( jcl->getSensors()[i] != NULL ){
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorAliasMessage) + strlen(jcl->getSensors()[i]->getPin()) + strlen(jcl->getSensors()[i]->getSensorNickname()) + 4;
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorAliasMessage) + strlen(jcl->getSensors()[i]->getPin());
        for ( unsigned int k=0; k < strlen(Constants::sensorAliasMessage); k++ )
          jcl->message[currentPosition++] = Constants::sensorAliasMessage[k];
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getPin()); k++ )
          jcl->message[currentPosition++] = (char) jcl->getSensors()[i]->getPin()[k];
        jcl->message[currentPosition++] =  18;
        jcl->message[currentPosition++] = strlen(jcl->getSensors()[i]->getSensorNickname());
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getSensorNickname()); k++ )
          if (jcl->getSensors()[i]->getSensorNickname()[k] != 0)
            jcl->message[currentPosition++] = jcl->getSensors()[i]->getSensorNickname()[k];
      }
    }

    /* Setting the value of the SENSOR_SIZE */
    for (i=0; i< JCL::getTotalSensors(); i++){
      if ( jcl->getSensors()[i] != NULL ){
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorSizeMessage) + strlen(jcl->getSensors()[i]->getPin()) + strlen(jcl->getSensors()[i]->getSensorSize()) + 4;
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorSizeMessage) + strlen(jcl->getSensors()[i]->getPin());
        for ( unsigned int k=0; k < strlen(Constants::sensorSizeMessage); k++ )
          jcl->message[currentPosition++] = Constants::sensorSizeMessage[k];
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getPin()); k++ )
          jcl->message[currentPosition++] = (char) jcl->getSensors()[i]->getPin()[k];
        jcl->message[currentPosition++] =  18;
        jcl->message[currentPosition++] = strlen(jcl->getSensors()[i]->getSensorSize());
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getSensorSize()); k++ )
          if (jcl->getSensors()[i]->getSensorSize()[k] != 0)
            jcl->message[currentPosition++] = jcl->getSensors()[i]->getSensorSize()[k];
      }
    }

    /* Setting the value of the SENSOR_DIR */
    for (i=0; i< JCL::getTotalSensors(); i++){
      if ( jcl->getSensors()[i] != NULL ){
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorDirMessage) + strlen(jcl->getSensors()[i]->getPin()) + 1 + 4;
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorDirMessage) + strlen(jcl->getSensors()[i]->getPin());
        for ( unsigned int k=0; k < strlen(Constants::sensorDirMessage); k++ )
          jcl->message[currentPosition++] = Constants::sensorDirMessage[k];
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getPin()); k++ )
          jcl->message[currentPosition++] = (char) jcl->getSensors()[i]->getPin()[k];
        jcl->message[currentPosition++] =  18;
        jcl->message[currentPosition++] = 1;
        jcl->message[currentPosition++] = jcl->getSensors()[i]->getTypeIO();
      }
    }

    /* Setting the value of the SENSOR_SAMPLING */
    for (i=0; i < JCL::getTotalSensors(); i++){
      if ( jcl->getSensors()[i] != NULL ){
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorSamplingMessage) + strlen(jcl->getSensors()[i]->getPin()) + strlen(jcl->getSensors()[i]->getDelay()) + 4;
        jcl->message[currentPosition++] =  10;
        jcl->message[currentPosition++] = strlen(Constants::sensorSamplingMessage) + strlen(jcl->getSensors()[i]->getPin());
        for ( unsigned int k=0; k < strlen(Constants::sensorSamplingMessage); k++ )
          jcl->message[currentPosition++] = Constants::sensorSamplingMessage[k];
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getPin()); k++ )
          jcl->message[currentPosition++] = (char) jcl->getSensors()[i]->getPin()[k];
        jcl->message[currentPosition++] =  18;
        jcl->message[currentPosition++] = strlen(jcl->getSensors()[i]->getDelay());
        for ( unsigned int k=0; k < strlen(jcl->getSensors()[i]->getDelay()); k++ )
          if (jcl->getSensors()[i]->getDelay()[k] != 0)
            jcl->message[currentPosition++] = jcl->getSensors()[i]->getDelay()[k];
      }
    }
//  }
  boolean activateEncryption = false;
  if ( jcl->isEncryption() && messageType == -1 ){
    jcl->setEncryption(false);
    activateEncryption = true;
  }
  size = completeHeader(currentPosition, 16, true, jcl->getMetadata()->getMAC(), 0);

  if (activateEncryption && messageType == -1)
    jcl->setEncryption(true);

  jcl->getClient().write(jcl->message, size);
  jcl->getClient().flush();

  if ( messageType == -1)
    receiveRegisterServerAnswer();
  else
    receiveServerAnswer();
}

void Message::sensing(int pin, boolean sensorNow){
Serial.print("sensing: "); Serial.println(pin);
  int size;
  unsigned int i;
  int currentPosition = 0;
  if ( jcl->getSensors()[pin] == NULL || jcl->getSensors()[pin]->getTypeIO() == Constants::CHAR_OUTPUT )
  return;

  jcl->message[currentPosition++] =  8; // Indicates first field
  jcl->message[currentPosition++] =  27;  // Fix field. Also indicates sensing data

  jcl->message[currentPosition++] =  18; // Indicates second field. In this case is a string with MAC and PORT
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getHostPort()) + strlen(jcl->getMetadata()->getMAC()); // Indicates the size of the string

  for ( i=0; i < strlen(jcl->getMetadata()->getMAC()); i++ )
  jcl->message[currentPosition++] = jcl->getMetadata()->getMAC()[i];

  for ( i=0; i < strlen(jcl->getMetadata()->getHostPort()); i++ )
  jcl->message[currentPosition++] = jcl->getMetadata()->getHostPort()[i];

  jcl->message[currentPosition++] =  24; // Indicates third field. In this case is the ID of the sensor
  jcl->message[currentPosition++] = pin;

  jcl->message[currentPosition++] =  34; // Indicates fourth field. In this case the value of the sensing

  uint8_t array [10];
  int sensorValue;
  if ( pin >= JCL::getTotalDigitalSensors() ){
    sensorValue = analogRead(pin - JCL::getTotalDigitalSensors());
  }
  else
    sensorValue = digitalRead(pin);

  int totalBytes = encode_unsigned_varint(array, sensorValue);
  jcl->message[currentPosition++] = totalBytes + 1;
  jcl->message[currentPosition++] = 40;
  for (int i=0; i<totalBytes;i++)
    jcl->message[currentPosition++] = (int) array[i];

  jcl->message[currentPosition++] = 40; // Indicates fifth field. In this case the time in long
  uint8_t arrayInt[10];
  int total = encode_signed_varint(arrayInt, millis());
  for (int i=0; i<total; i++)
    jcl->message[currentPosition++] = arrayInt[i];


  jcl->message[currentPosition++] = 50; // Indicates sixth field. In this case the data type in a String format
  jcl->message[currentPosition++] = strlen(Constants::dataTypeValue);     // Indicates the size of the String
  for ( i=0; i < strlen(Constants::dataTypeValue); i++ )
    jcl->message[currentPosition++] = Constants::dataTypeValue[i];

  if (sensorNow){
    size = completeHeader(currentPosition, 15, false, jcl->getMetadata()->getMAC(), 0);
    jcl->getRequestListener().write(jcl->message, size);
    jcl->getRequestListener().flush();
  }
  else{
    size = completeHeader(currentPosition, 15, true, jcl->getMetadata()->getMAC(), 0);

jcl->getSensors()[pin]->count++;

    jcl->getClient().write(jcl->message, size);
    jcl->getClient().flush();
// while (!jcl->getClient().available());
// while (jcl->getClient().available()) jcl->getClient().read();


    jcl->getSensors()[pin]->setLastExecuted(millis());

/*     if (jcl->getMQTTClient()->connected()){
      Serial.println(jcl->getSensors()[pin]->getSensorNickname());
      char mqttMessage[6];
      sprintf(mqttMessage, "%d", sensorValue);
      jcl->getMQTTClient()->publish(jcl->getSensors()[pin]->getSensorNickname(), mqttMessage);
    }*/
  }
  jcl->getSensors()[pin]->setValue(sensorValue);
  if (!sensorNow)
    receiveServerAnswer();
}

void Message::unregister(){
  int size;
  int currentPosition = 0;

  jcl->message[currentPosition++] =  8; // Indicates first field

  /* The next fields indicates the value -2. It takes multiple bytes to indicate negative field because
  Protocol Buffer uses base 128 varints */
  jcl->message[currentPosition++] =  -2;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  -1;
  jcl->message[currentPosition++] =  1;

  jcl->message[currentPosition++] =  18;

  // Calculate size to append to the message
  int totalSize = 2 +
                strlen(jcl->getMetadata()->getHostIP()) + 2 +
                strlen(jcl->getMetadata()->getHostPort()) + 2 +
                strlen(jcl->getMetadata()->getMAC()) + 2 +
                1 + 2 +  // strlen(coreValue)
                1 + 2;   // strlen(deviceType)

  /* If total size is less then 128 we only need one byte to store it */
  if ( totalSize < 128 )
    jcl->message[currentPosition++] = totalSize;
  else{
    int lastByte = (int) totalSize / 128;
    int rest = 128 - ( (int) totalSize % 128 );
    rest *= -1;
    jcl->message[currentPosition++] = rest;
    jcl->message[currentPosition++] = lastByte;
  }

  jcl->message[currentPosition++] = 8;
  jcl->message[currentPosition++] = 5;

  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getHostIP());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getHostIP()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getHostIP()[i];

  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getHostPort());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getHostPort()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getHostPort()[i];

  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = strlen(jcl->getMetadata()->getMAC());
  for ( unsigned int i=0; i < strlen(jcl->getMetadata()->getMAC()); i++ )
    jcl->message[currentPosition++] = jcl->getMetadata()->getMAC()[i];

  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = 1;  //strlen(corevalue)
  for ( unsigned int i=0; i < 1; i++ )
    jcl->message[currentPosition++] = Constants::coreValue[i];

  jcl->message[currentPosition++] =  18;
  jcl->message[currentPosition++] = 1;
  for ( unsigned int i=0; i < 1; i++ )
    jcl->message[currentPosition++] = Constants::deviceTypeValue[i];  // Device Type

  size = completeHeader(currentPosition, 2, true, jcl->getMetadata()->getMAC(), 0);

  jcl->getClient().write(jcl->message, size);
  jcl->getClient().flush();
  receiveServerAnswer();
}

bool Message::removeContextAction(bool isActing){
  uint16_t pos = 17;
  while (jcl->message[pos] != 74)
    pos++;
  pos++;
  uint8_t nChars=jcl->message[pos++];
  char contextName[nChars+1];
  for (uint8_t x=0; x < nChars; x++)
    contextName[x] = jcl->message[pos + x];
  contextName[nChars] = '\0';

  /*uint8_t nChars=jcl->message[43];
  char contextName[nChars+1];
  for (uint8_t x=0; x < nChars; x++)
    contextName[x] = jcl->message[44 + x];
  contextName[nChars] = '\0';*/
  Context *ctx = NULL;
  for (uint16_t x=0; x<TOTAL_SENSORS;x++)
  if (jcl->getSensors()[x]->getNumContexts() > 0){
    for (uint8_t ctxNumber=0; ctxNumber<jcl->getSensors()[x]->getNumContexts(); ctxNumber++)
      if ( strcmp(jcl->getSensors()[x]->getEnabledContexts()[ctxNumber]->getNickname(), contextName) == 0 ){
        ctx = jcl->getSensors()[x]->getEnabledContexts()[ctxNumber];
        break;
    }
  }
Serial.print("1: ");
Serial.println(ctx == NULL);
  if ( ctx == NULL )
    return false;

  if (isActing){
      uint16_t pos = 47 + nChars;
      nChars = jcl->message[pos++];
      char* hostIP = (char*)malloc(sizeof(char)*nChars+1);
      for (uint8_t x=0; x<nChars; x++)
        hostIP[x] = jcl->message[pos + x];
      hostIP[nChars] = '\0';

      pos +=nChars;
      while (jcl->message[pos] != 74)
        pos++;
      nChars = jcl->message[++pos];
      pos++;
      char* hostPort = (char*)malloc(sizeof(char)*nChars+1);
      for (uint8_t x=0; x<nChars; x++)
        hostPort[x] = jcl->message[pos + x];
      hostPort[nChars] = '\0';

      pos +=nChars;
      while (jcl->message[pos] != 74)
        pos++;
      nChars = jcl->message[++pos];
      pos++;
      char* portSuperPeer = (char*)malloc(sizeof(char)*nChars+1);
      for (uint8_t x=0; x<nChars; x++)
        portSuperPeer[x] = jcl->message[pos + x];
      portSuperPeer[nChars] = '\0';

      pos += nChars+95;
      while (jcl->message[pos] != 3)
        pos++;
      /*pos++;
      while (jcl->message[pos] != 3)
        pos++;*/

      nChars = messageSize - pos;
      char* param = (char*)malloc(sizeof(char)*nChars+1);
      for (uint16_t x=0; x<nChars; x++)
        param[x] = jcl->message[pos + x];
      param[nChars] = '\0';

      for (int actPos=0; actPos < ctx->getNumActions(); actPos++){
        Action *act = ctx->getEnabledActions()[actPos];
        if ( act->isActing() && strcmp(act->getHostIP(), hostIP) == 0 && strcmp(act->getHostPort(), hostPort) == 0
                  && strcmp(act->getSuperPeerPort(), portSuperPeer) == 0 && strcmp(act->getParam(), param) == 0){

              if ( ctx->getNumActions() == 1 || actPos == ctx->getNumActions() - 1){
                ctx->getEnabledActions()[actPos] = NULL;
              }else{
                ctx->getEnabledActions()[actPos] = ctx->getEnabledActions()[ctx->getNumActions() - 1];
                ctx->getEnabledActions()[ctx->getNumActions()-1] = NULL;
              }

              act->deleteAction();
              ctx->setNumActions(ctx->getNumActions() - 1);
              return true;
        }
      }
      return false;
  }else{
    bool useSensorValue;
    pos += nChars;
    while (jcl->message[pos] != 74)
      pos++;
    pos++;
    nChars = jcl->message[pos++];
    if (jcl->message[pos] == 't')
      useSensorValue = true;
    else
      useSensorValue = false;

    pos += nChars;
    while (jcl->message[pos] != 74)
      pos++;
    pos++;
    nChars = jcl->message[pos++];
    char* className = new char[nChars+1];
    for (uint8_t x=0; x < nChars; x++)
      className[x] = jcl->message[pos + x];
    className[nChars] = '\0';

    pos += nChars;
    while (jcl->message[pos] != 74)
      pos++;
    nChars = jcl->message[++pos];
    char* methodName = new char[nChars+1];
    for (uint8_t x=0; x < nChars; x++)
      methodName[x] = jcl->message[pos + x + 1];
    methodName[nChars] = '\0';

    while (jcl->message[pos] != 122)
      pos++;
    pos--;
    char* param = new char[messageSize - pos + 1];
    for (int x=pos; x < messageSize; x++)
      param[x-pos] = jcl->message[x];
    param[messageSize - pos] = '\0';

    for (int actPos=0; actPos < ctx->getNumActions(); actPos++){
      Action *act = ctx->getEnabledActions()[actPos];
      if ( !act->isActing() && act->isUseSensorValue() == useSensorValue
                && strcmp(act->getClassName(), className) == 0 && strcmp(act->getMethodName(), methodName) == 0
                && strcmp(act->getParam(), param) == 0){

            if ( ctx->getNumActions() == 1 || actPos == ctx->getNumActions() - 1){
              ctx->getEnabledActions()[actPos] = NULL;
            }else{
              ctx->getEnabledActions()[actPos] = ctx->getEnabledActions()[ctx->getNumActions() - 1];
              ctx->getEnabledActions()[ctx->getNumActions()-1] = NULL;
            }

            act->deleteAction();
            ctx->setNumActions(ctx->getNumActions() - 1);
            return true;
      }
    }

    return false;
  }
}

bool Message::setMetadata(){
  char *pointer;
  char pin[3];
  char value[4];
  int pos = 0;
  Sensor *s;

  for (int i=0; i<JCL::getTotalSensors(); i++)
  if (jcl->getSensors()[i] != NULL){
    jcl->getSensors()[i]->deleteSensor();
    jcl->getSensors()[i] = NULL;
  }

  jcl->message[0] = 52;
  jcl->message[1] = 52;
  for (int i=0; i< 17; i++)
    jcl->message[i] = 52;

  pointer = strstr(jcl->message, "DEVICE_ID");
  if ( pointer != NULL ){
    int c = 0;
    while ( pointer[c] != 18 )
      c++;
    c++;
    int l = pointer[c];
    c++;
    char* nameBoard = (char*)malloc(sizeof(char)*l+1);
    for (int i=0; i < l; i++)
      nameBoard[i] = pointer[c++];
    nameBoard[l] = '\0';
    jcl->getMetadata()->setBoardName(nameBoard);
  }

  pointer = strstr(jcl->message, "ENABLE_SENSOR");
  if ( pointer != NULL ){
    int c = 0;
    while ( pointer[c] != 18 )
      c++;
    c++;
    int l = pointer[c];
    c++;
    pos = 0;
    uint8_t numSensors = 0;

    for (int i=0; i <= l; i++){
      if ( pointer[c] == ';' || i == l ){
        //pin[pos] = '\0';
        if ( !Sensor::validPin(atoi(pin)) )
          return false;
        s = new Sensor();
        jcl->getSensors()[atoi(pin)] = s;
        jcl->getSensors()[atoi(pin)]->setPin(pin);
        jcl->getSensors()[atoi(pin)]->setLastExecuted(0);
        char defaultName[] = "not set",
        defaultDelay[] = "60",
        defaultSensorSize[] = "1";
        jcl->getSensors()[atoi(pin)]->setSensorNickname(defaultName);   // In case the user didn't set the sensor name we use the default
        jcl->getSensors()[atoi(pin)]->setDelay(defaultDelay);   // In case the user didn't set the delay we use the default 60
        jcl->getSensors()[atoi(pin)]->setTypeIO(Constants::CHAR_INPUT);   // the default is input sensor
        jcl->getSensors()[atoi(pin)]->setType(0);   // default is generic sensor
        jcl->getSensors()[atoi(pin)]->setSensorSize(defaultSensorSize);   // default is 1MB
jcl->availableSensors[jcl->numSensors++] = atoi(pin);
        pos = 0;
        c++;
        numSensors++;
      }else{
        pin[pos++] = pointer[c++];
        pin[pos] = '\0';
      }
    }
    char nSensors[4];
    sprintf( nSensors, "%d", numSensors );
    jcl->getMetadata()->setNumConfiguredSensors(nSensors);
  }

  pointer = jcl->message;

  while ( (pointer=strstr(pointer, "SENSOR_SAMPLING_") ) != NULL ) {
    int c = 16;
    pos = 0;
    while ( pointer[c] != 18 ){
      pin[pos++] = pointer[c++];
    }
    pin[pos] = '\0';
    /* Verificar se é para criar mesmo quando não estiver no ENABLE_SENSOR ou se é para retornar false*/
    if (jcl->getSensors()[atoi(pin)] == NULL){
      /*Sensors *s = (Sensors*) (malloc(sizeof(Sensors)));
      sensors[atoi(pin)] = s;
      strcpy(sensors[atoi(pin)]->pin, pin);*/
      return false;
    }

    c++;
    int l = pointer[c++];
    char* delay = (char*)malloc(sizeof(char)*l+1);
    pos = 0;
    for (int i=0; i < l; i++){
      delay[pos++] = pointer[c++];
    }
    delay[pos] = '\0';
    s->setDelay(delay);
    pointer++;
  }

  pointer = jcl->message;
  while ( (pointer=strstr(pointer, "SENSOR_ALIAS_") ) != NULL ) {
    int c = 13;
    pos = 0;
    while ( pointer[c] != 18 ){
      pin[pos++] = pointer[c++];
    }
    pin[pos] = '\0';
    if ( !Sensor::validPin(atoi(pin)) || jcl->getSensors()[atoi(pin)] == NULL){
      return false;
    }

    c++;
    int l = pointer[c++];
    char* nameSensor = (char*)malloc(sizeof(char)*l+1);
    pos = 0;
    for (int i=0; i < l; i++){
      nameSensor[pos++] = pointer[c++];
    }
    nameSensor[pos] = '\0';
    s->setSensorNickname(nameSensor);
    pointer++;
  }

  pointer = jcl->message;
  while ( (pointer=strstr(pointer, "SENSOR_SIZE_") ) != NULL ) {
    int c = 12;
    pos = 0;
    while ( pointer[c] != 18 ){
      pin[pos++] = pointer[c++];
    }
    pin[pos] = '\0';
    if ( !Sensor::validPin(atoi(pin)) || jcl->getSensors()[atoi(pin)] == NULL){
      return false;
    }

    c++;
    int l = pointer[c++];
    char* sensorSize = (char*)malloc(sizeof(char)*l+1);
    pos = 0;
    for (int i=0; i < l; i++){
      sensorSize[pos++] = pointer[c++];
    }
    sensorSize[pos] = '\0';
    s->setSensorSize(sensorSize);
    pointer++;
  }

  pointer = jcl->message;
  while ( (pointer=strstr(pointer, "SENSOR_TYPE_") ) != NULL ) {
    int c = 12;
    pos = 0;
    while ( pointer[c] != 18 ){
      pin[pos++] = pointer[c++];
    }
    pin[pos] = '\0';
    if ( !Sensor::validPin(atoi(pin)) || jcl->getSensors()[atoi(pin)] == NULL){
      return false;
    }

    c++;
    int l = pointer[c++];

    pos = 0;
    for (int i=0; i < l; i++){
      value[pos++] = pointer[c++];
    }
    value[pos] = '\0';
    s->setType(atoi(value));
    pointer++;
  }

  pointer = jcl->message;
  while ( (pointer=strstr(pointer, "SENSOR_DIR_") ) != NULL ) {
    int c = 11;
    pos = 0;
    while ( pointer[c] != 18 ){
      pin[pos++] = pointer[c++];
    }
    pin[pos] = '\0';
    if ( !Sensor::validPin(atoi(pin)) || jcl->getSensors()[atoi(pin)] == NULL){
      return false;
    }

    c++;
    c++;

    pos = 0;
    s->setTypeIO(toUpperCase(pointer[c++]));

    if ( s->getTypeIO() != Constants::CHAR_OUTPUT && s->getTypeIO() != Constants::CHAR_INPUT)
    s->setTypeIO(Constants::CHAR_INPUT);

    s->configurePinMode();
    pointer++;
  }

  jcl->listSensors();
  return true;
}

boolean Message::registerContext(boolean isMQTTContext){
  int pos = 14;
  Context* ctx = new Context();
  while ( jcl->message[pos] != 74 )
    pos++;
  int nChars = jcl->message[++pos];
  char* expression = (char*)malloc(sizeof(char)*nChars+1);
  for (uint8_t x=0; x<nChars; x++)
    expression[x] = jcl->message[x + pos + 1];
  expression[nChars] = '\0';
  ctx->setExpression(expression);
  pos += nChars;

  while ( jcl->message[pos] != 74 )
    pos++;
  nChars = jcl->message[++pos];
  char pinC[nChars];
  for (uint8_t x=0; x<nChars; x++)
    pinC[x] = jcl->message[x + pos + 1];
  pinC[nChars] = '\0';
  pos += nChars;
  int pin = atoi(pinC);

  if (jcl->getSensors()[pin] == NULL ){
    ctx->deleteContext();
    sendResultBool(false);
  }else{
    while ( jcl->message[pos] != 74 )
    pos++;
    nChars = jcl->message[++pos];
    char* nickname = (char*)malloc(sizeof(char)*nChars+1);
    for (uint8_t x=0; x<nChars; x++)
    nickname[x] = jcl->message[x + pos + 1];
    nickname[nChars] = '\0';
    ctx->setNickname(nickname);
    pos += nChars;

    char *token = strtok(ctx->getExpression(), ";");
    while (token != NULL){
      MatchState ms;
      ms.Target (token);
      char res1 = ms.Match ("^S[0-9]+[><=~]=?-?[0-9]+$");
      char res2 = ms.Match("^S[0-9]+[><=~]=?-?[0-9]+.[0-9]+$");
      if (res1 == REGEXP_MATCHED || res2 == REGEXP_MATCHED )
      {
        uint8_t current = 2, p = 0;;
        char* operators = (char*)malloc(sizeof(char)*OPERATORS_MAX_SIZE+1);
        while ( !( (token[current] >=48 && token[current] <= 57) || token[current] == 45) )
        operators[p++] = token[current++];
        operators[p] = '\0';
        ctx->getOperators()[ctx->getNumExpressions()] = operators;

        p = 0;
        char* threshold = (char*)malloc(sizeof(char)*OPERATORS_MAX_SIZE+1);
        while ( token[current] != '\0' )
        threshold[p++] = token[current++];
        threshold[p] = '\0';
        ctx->getThreshold()[ctx->getNumExpressions()] = threshold;
      }
      else
      return false;

      ctx->setNumExpressions(ctx->getNumExpressions() + 1);
      token = strtok(NULL, ";");

      ctx->setMQTTContext(isMQTTContext);
    }
    jcl->getSensors()[pin]->getEnabledContexts()[jcl->getSensors()[pin]->getNumContexts()] = ctx;
  }
  jcl->getSensors()[pin]->setNumContexts(jcl->getSensors()[pin]->getNumContexts() + 1);
  return true;
}

bool Message::unregisterContext(boolean isMQTTContext){
  int pos = 14;
  while ( jcl->message[pos] != 74 )
    pos++;
  int nChars = jcl->message[++pos];

  char* nickname = (char*)malloc(sizeof(char)*nChars+1);
  for (uint8_t x=0; x<nChars; x++)
    nickname[x] = jcl->message[x + pos + 1];
  nickname[nChars] = '\0';

  for (int sensorPos=0; sensorPos < jcl->getTotalSensors(); sensorPos++){
    if (jcl->getSensors()[sensorPos] != NULL){
      Sensor* s = jcl->getSensors()[sensorPos];
      for (int ctxPos=0; ctxPos < s->getNumContexts(); ctxPos++){
        Context* ctx = s->getEnabledContexts()[ctxPos];
        if ( strcmp(ctx->getNickname(), nickname) == 0 && isMQTTContext == ctx->isMQTTContext() ){
          Serial.println("Exists");

          if ( s->getNumContexts() == 1 || ctxPos == s->getNumContexts() - 1)
            s->getEnabledContexts()[ctxPos] = NULL;
          else{
            s->getEnabledContexts()[ctxPos] = s->getEnabledContexts()[s->getNumContexts()-1];
            s->getEnabledContexts()[s->getNumContexts()-1] = NULL;
          }
          ctx->deleteContext();
          s->setNumContexts(s->getNumContexts() - 1);
          return true;
        }
      }
    }
  }

  return false;
}

void Message::sendContextActionMessage(Action* act){
  int currentPosition = 0;
  if (act->isActing()){
    jcl->message[currentPosition++] = 8;
    jcl->message[currentPosition++] = 51;
    jcl->message[currentPosition++] = 18;
    jcl->message[currentPosition++] = 37;
    jcl->message[currentPosition++] = 122;
    jcl->message[currentPosition++] = 16;
    jcl->message[currentPosition++] = 106;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 118;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 46;
    jcl->message[currentPosition++] = 108;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 110;
    jcl->message[currentPosition++] = 103;
    jcl->message[currentPosition++] = 46;
    jcl->message[currentPosition++] = 79;
    jcl->message[currentPosition++] = 98;
    jcl->message[currentPosition++] = 106;
    jcl->message[currentPosition++] = 101;
    jcl->message[currentPosition++] = 99;
    jcl->message[currentPosition++] = 116;
    jcl->message[currentPosition++] = 24;
    jcl->message[currentPosition++] = 2;
    jcl->message[currentPosition++] = 16;
    jcl->message[currentPosition++] = 1;
    jcl->message[currentPosition++] = 10;

    for (uint8_t x=0; x<act->getParamSize(); x++)
      jcl->message[currentPosition++] = act->getParam()[x];

    jcl->message[3] = currentPosition - 6;

    int* ip = Utils::getIPAsArray(act->getHostIP());
    if (strcmp(act->getHostIP(), jcl->getMetadata()->getHostIP()) == 0 ){
      // printMessagePROGMEM(Constants::actingMessage);
      Serial.println("Acting");
      int pos = currentPosition - 1;
      int value = 0;
      int pin = 0;
      int k, j;
      char command[12];

      pos = 0;
      while ( jcl->message[pos] != 74 )
        pos++;
      k = jcl->message[++pos];

      if (k == 1)
        pin = jcl->message[pos+1] - 48;
      else{
        pin = 10 * (jcl->message[pos+1]- 48) ;
        pin += jcl->message[pos+2] - 48 ;
      }
      while ( jcl->message[pos] != 16 )
        pos++;
      pos++;
      pos++;
      uint8_t numCmd = (int) jcl->message[pos];
      pos++;
      pos++;

      for (k=0; k<numCmd; k++){
        uint8_t nChars = jcl->message[pos++];
        for (j=0; j<nChars && j <12; j++)
          command[j] = jcl->message[pos++];
        command[j] = '\0';
        pos++;

        float f = atof(command);
        value = (int) f;
        if ( !jcl->getSensors()[pin]->acting(value) )
          break;
      }
    }else{
      int msgSize = completeHeader(currentPosition, 9, true, act->getHostMAC(), atoi(act->getSuperPeerPort()));
      EthernetClient host;
      host.connect(IPAddress(ip[0], ip[1], ip[2], ip[3]), atoi(act->getHostPort()));
      host.write(jcl->message, msgSize);
      host.flush();
      while (!host.available());
      while (host.available())
        host.read();
      if (host.connected())
        host.stop();
    }
  }else{
    jcl->message[currentPosition++] = 8;
    jcl->message[currentPosition++] = 58;
    jcl->message[currentPosition++] = 18;
    jcl->message[currentPosition++] = 127;
    jcl->message[currentPosition++] = 122;
    jcl->message[currentPosition++] = 16;
    jcl->message[currentPosition++] = 106;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 118;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 46;
    jcl->message[currentPosition++] = 108;
    jcl->message[currentPosition++] = 97;
    jcl->message[currentPosition++] = 110;
    jcl->message[currentPosition++] = 103;
    jcl->message[currentPosition++] = 46;
    jcl->message[currentPosition++] = 79;
    jcl->message[currentPosition++] = 98;
    jcl->message[currentPosition++] = 106;
    jcl->message[currentPosition++] = 101;
    jcl->message[currentPosition++] = 99;
    jcl->message[currentPosition++] = 116;
    jcl->message[currentPosition++] = 24;
    jcl->message[currentPosition++] = 9;
    jcl->message[currentPosition++] = 16;
    jcl->message[currentPosition++] = 1;
    jcl->message[currentPosition++] = 10;

    jcl->message[currentPosition++] = 2;
    jcl->message[currentPosition++] = 8;
    if (act->isUseSensorValue())
      jcl->message[currentPosition++] = 1;
    else
      jcl->message[currentPosition++] = 0;

    jcl->message[currentPosition++] = 10;
    jcl->message[currentPosition++] = strlen(act->getTicket()) + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = strlen(act->getTicket());
    for (uint8_t x=0; x<strlen(act->getTicket()); x++)
      jcl->message[currentPosition++] = act->getTicket()[x];

    jcl->message[currentPosition++] = 10;
    jcl->message[currentPosition++] = strlen(act->getHostIP()) + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = strlen(act->getHostIP());
    for (uint8_t x=0; x<strlen(act->getHostIP()); x++)
      jcl->message[currentPosition++] = act->getHostIP()[x];

    jcl->message[currentPosition++] = 10;
    jcl->message[currentPosition++] = strlen(act->getHostPort()) + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = strlen(act->getHostPort());
    for (uint8_t x=0; x<strlen(act->getHostPort()); x++)
      jcl->message[currentPosition++] = act->getHostPort()[x];

    jcl->message[currentPosition++] = 10 ;
    jcl->message[currentPosition++] = strlen(act->getHostMAC()) + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = strlen(act->getHostMAC());
    for (uint8_t x=0; x<strlen(act->getHostMAC()); x++)
      jcl->message[currentPosition++] = act->getHostMAC()[x];

if (act->getSuperPeerPort() !=0){
    jcl->message[currentPosition++] = 10 ;
    jcl->message[currentPosition++] = strlen(act->getSuperPeerPort()) + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = strlen(act->getSuperPeerPort());
    for (uint8_t x=0; x<strlen(act->getSuperPeerPort()); x++)
      jcl->message[currentPosition++] = act->getSuperPeerPort()[x];
}
    jcl->message[currentPosition++] = 10 ;
    jcl->message[currentPosition++] = act->getClassNameSize() + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = act->getClassNameSize();
    for (uint8_t x=0; x<act->getClassNameSize(); x++)
      jcl->message[currentPosition++] = act->getClassName()[x];

    jcl->message[currentPosition++] = 10 ;
    jcl->message[currentPosition++] = act->getMethodNameSize() + 2;
    jcl->message[currentPosition++] = 74;
    jcl->message[currentPosition++] = act->getMethodNameSize();
    for (uint8_t x=0; x< act->getMethodNameSize(); x++)
      jcl->message[currentPosition++] = act->getMethodName()[x];

    jcl->message[currentPosition++] = 10;
    for (uint8_t x=0; x<act->getParamSize(); x++)
      jcl->message[currentPosition++] = act->getParam()[x];

    uint8_t arrayChar[10];
    int totalBytes = encode_unsigned_varint(arrayChar, currentPosition - 4 -2);

    if (totalBytes > 1){
      for (int aux=currentPosition; aux>3; aux--)
          jcl->message[aux+1] = jcl->message[aux];
    }
    for (int i=0; i<totalBytes;i++)
      jcl->message[3+i] = (int) arrayChar[i];
    currentPosition += totalBytes - 1;

    int msgSize = completeHeader(currentPosition, 9, true, act->getHostMAC(), atoi(act->getSuperPeerPort()));

    int* ip = Utils::getIPAsArray(act->getHostIP());
    EthernetClient host;
    host.connect(IPAddress(ip[0], ip[1], ip[2], ip[3]), atoi(act->getHostPort()));
    host.connect(IPAddress(192,168,2,109), 7070);
    host.write(jcl->message, msgSize);
    host.flush();
    long time = millis();
    while (!host.available() && millis() - time < 10000 );
    while (host.available())
      host.read();
    if (host.connected())
      host.stop();
  }
}
