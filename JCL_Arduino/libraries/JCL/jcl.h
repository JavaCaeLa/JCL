#ifndef JCL_h
#define JCL_h

#include "metadata.h"
#include <Ethernet.h>
//include <UIPEthernet.h>
#include <SPI.h>
#include "sensor.h"
#include "constants.h"
#include <EEPROM.h>
#include <avr/pgmspace.h>
#include <PubSubClient.h>

static const int TOTAL_DIGITAL_SENSORS = 54;
static const int TOTAL_SENSORS = 70;

class JCL{
  public:
    JCL(char* IP, int port, char* mac);
    void changeBoardNickname(char* boardName);
    void configureJCLServer(char* serverIP, int serverPort);
    void startHost();
    void run();
    void setEncryption(bool encryption);
    bool isEncryption();
    void writeEprom();
    void readEprom();
    EthernetClient getClient();
    EthernetClient getRequestListener();
    Sensor** getSensors();
    Metadata* getMetadata();
    void setKey(char* key);
    char* getKey();
    char message[850];
    void listSensors();
    void setBrokerData(char* brokerIP, int brokerPort);
    void conectToBroker();
    static int getTotalSensors();
    static int getTotalDigitalSensors();
    void useEEPROM(bool useEEPROM);
    bool isUseEEPROM();
    PubSubClient* getMQTTClient();
    int freeRam ();
    int availableSensors[TOTAL_SENSORS];
    int numSensors;
  private:
    void beginEthernet();
    void sendBroadcastMessage();
    void connectToServer();
    void makeSensing();
    void checkContext(int pin);
    boolean checkCondition(int sensorValue, char *operation, char *threshold, float lastValue);
    char* boardName;
    Metadata* metadata;
    bool encryption;
    bool eeprom;
    EthernetClient client, requestListener, mqttEthClient;
    Sensor* sensors[TOTAL_SENSORS];
    char* key;
    PubSubClient* mqtt;
};

#endif
