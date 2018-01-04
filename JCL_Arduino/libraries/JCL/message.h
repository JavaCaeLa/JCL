#ifndef Message_h
#define Message_h

#include "jcl.h"
#include "sensor.h"
#include "action.h"
#include <Ethernet.h>
//#include <UIPEthernet.h>
#include <SPI.h>

#define BUFFER 850

class Message{
  public:
    Message(JCL* jcl);
    Message(JCL* jcl, int messageSize);
    void sendMetadata(int messageType);
    int completeHeader(int messageSize, int messageType, boolean sendMAC, char* hostMAC, int superPeerPort);
    void receiveServerAnswer();
    void receiveRegisterServerAnswer();
    void sendResultBool(boolean result);
    void sensing(int pin, boolean sensorNow);
    int encode_unsigned_varint(uint8_t *const buffer, uint64_t value);
    int encode_signed_varint(uint8_t *const buffer, int64_t value);
    bool setMetadata();
    boolean registerContext(boolean isMQTTContext);
    void listen();
    void treatMessage();
    void sendResult(int result);
    static void printMessagePROGMEM(const char *m);
    bool messageSetSensor();
    void sendContextActionMessage(Action* act);
    void unregister();
    bool unregisterContext(boolean isMQTTContext);
    bool removeContextAction(bool isActing);
  private:
    int messageSize;
    JCL* jcl;
};

#endif
