#ifndef Action_h
#define Action_h

#include <Arduino.h>

class Action{
  public:
    void deleteAction();
    void setUseSensorValue(bool useSensorValue);
    bool isUseSensorValue();
    void setActing(bool acting);
    bool isActing();
    void setClassName(char* className);
    char* getClassName();
    void setMethodName(char* methodName);
    char* getMethodName();
    void setHostIP(char* hostIP);
    char* getHostIP();
    void setHostPort(char* hostPort);
    char* getHostPort();
    void setHostMAC(char* hostMAC);
    char* getHostMAC();
    void setSuperPeerPort(char* superPeerPort);
    char* getSuperPeerPort();
    void setTicket(char* ticket);
    char* getTicket();
    void setParam(char* param);
    char* getParam();
    uint16_t getParamSize();
    void setParamSize(uint16_t paramSize);
    uint16_t getClassNameSize();
    void setClassNameSize(uint16_t classNameSize);
    uint16_t getMethodNameSize();
    void setMethodNameSize(uint16_t methodNameSize);
  private:
    boolean useSensorValue;
    boolean acting;
    char* className;
    char* methodName;
    char* hostIP;
    char* hostPort;
    char* hostMac;
    char* superPeerPort;
    char* ticket;
    char* param;
    uint16_t paramSize;
    uint8_t classNameSize, methodNameSize;
};

#endif
