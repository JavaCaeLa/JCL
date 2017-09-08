
#ifndef Metadata_h
#define Metadata_h

#include <Arduino.h>

class Metadata{
  public:
    Metadata();
    void setBoardName(char* boardName);
    void setHostIP(char* IP);
    void setHostPort(char* port);
    void setServerIP(char* serverIP);
    void setServerPort(char* serverPort);
    void setNumConfiguredSensors(char* numConfiguredSensors);
    void setStandBy(bool standBy);
    void setMAC(char* mac);
    void setBrokerIP(char* brokerIP);
    void setBrokerPort(int brokerPort);
    int getBrokerPort();
    char* getBrokerIP();
    char* getBoardName();
    char* getHostIP();
    char* getHostPort();
    char* getServerIP();
    char* getServerPort();
    char* getNumConfiguredSensors();
    bool isStandBy();
    bool teste();
    char* getMAC();
  private:
    char* boardName;
    char* numConfiguredSensors;
    char* hostIP;
    char* hostPort;
    char* serverIP;
    char* serverPort;
    char* mac;
    char* brokerIP;
    int brokerPort;
    bool standBy;
};

#endif
