#ifndef Constants_h
#define Constants_h

#include <Arduino.h>

class Constants{
  public:
    const static char CHAR_INPUT;
    const static char CHAR_OUTPUT;
    const static int SERVO_ACTUATOR;
    const static char SEPARATOR;
    // const static long TIMEOUT;
    const static char turnOnMessage[]; // PROGMEM;
    const static char restartMessage[] ;// PROGMEM;
    const static char setMetadataMessage[] ;// PROGMEM;
    const static char setSensorMessage[] ;// PROGMEM;
    const static char setEncryptionMessage[] ;// PROGMEM;
    const static char removeSensorMessage[] ;// PROGMEM;
    const static char actingMessage[] ;// PROGMEM;
    const static char registerContextMessage[] ;// PROGMEM;
    const static char addContextActionMessage[] ;// PROGMEM;
    const static char ipMessage[] ;// PROGMEM;
    const static char serverIpMessage[] ;// PROGMEM;
    const static char serverPortMessage[] ;// PROGMEM;
    const static char portMessage[] ;// PROGMEM;
    const static char coreMessage[] ;// PROGMEM;
    const static char coreValue[] ;// PROGMEM;
    const static char macMessage[] ;// PROGMEM;
    const static char deviceTypeMessage[] ;// PROGMEM;
    const static char devicePlatformMessage[] ;// PROGMEM;
    const static char deviceTypeValue[] ;// PROGMEM;
    const static char devicePlatformValue[] ;// PROGMEM;
    const static char deviceIDMessage[] ;// PROGMEM;
    const static char trueMessage[] ;// PROGMEM;
    const static char falseMessage[] ;// PROGMEM;
    const static char standByMessage[] ;// PROGMEM;
    const static char numberSensorsMessage[] ;// PROGMEM;
    const static char enableSensorMessage[] ;// PROGMEM;
    const static char sensorAliasMessage[] ;// PROGMEM;
    const static char sensorSamplingMessage[] ;// PROGMEM;
    const static char sensorDirMessage[] ;// PROGMEM;
    const static char sensorTypeMessage[] ;// PROGMEM;
    const static char sensorSizeMessage[] ;// PROGMEM;
    const static char configuredSensorsMessage[] ;// PROGMEM;
    const static char connectedMessage[] ;// PROGMEM;
    const static char connectionErrorMessage[] ;// PROGMEM;
    const static char lineMessage[] ;// PROGMEM;
    const static char pintNotConfiguredMessage[] ;// PROGMEM;
    const static char commandUnkwownMessage[] ;// PROGMEM;
    const static char dataTypeValue[] ;// PROGMEM;
    const static char sensorNowMessage[] ;// PROGMEM;
    const static char standByMessageListen[] ;// PROGMEM;
};

#endif
