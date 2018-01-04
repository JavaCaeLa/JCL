#ifndef Sensor_h
#define Sensor_h

#include <Arduino.h>
#include "context.h"

const int MAX_CONTEXTS = 15;

class Sensor{
  public:
    Sensor();
    void deleteSensor();
    void setSensorNickname(char* sensorNickname);
    bool acting(float value);
    void configurePinMode();
    boolean digitalPin();
    static boolean validPin(int pin);
    int getAnalogPin();
    char* getSensorNickname();
    void setTypeIO(char typeIO);
    char getTypeIO();
    void setPin(char* pin);
    char* getPin();
    void setDelay(char* delay);
    char* getDelay();
    void setLastExecuted(long lastExecuted);
    long getLastExecuted();
    void setValue(float value);
    float getValue();
    void setType(int type);
    int getType();
    void setSensorSize(char* sensorSize);
    char* getSensorSize();
    uint8_t getNumContexts();
    void setNumContexts(uint8_t numContexts);
    Context** getEnabledContexts();
long count;
   private:
    char* sensorNickname;
    char typeIO;
    char* pin;
    char* delay;
    long lastExecuted;
    float value;
    int type;
    char* sensorSize;
    uint8_t numContexts;
    Context* enabledContexts[MAX_CONTEXTS];
};

#endif
