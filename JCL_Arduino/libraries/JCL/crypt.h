#ifndef Crypt_h
#define Crypt_h

#include "jcl.h"
#include <SPI.h>
#include <AESLib.h>
#include "sha256.h"

class Crypt{
  public:
    static void update(JCL* jcl);
    int padding(char* message, int dataSize);
    char* generateRegistrationKey(char* cryptedMessage, char* iv, int messageSize, char* myHash);
    char* generateIV();
    int cryptMessage(int messageSize, JCL* jcl, char iv[], char* hash);
    int decryptMessage(int messageSize, JCL* jcl);
    static char iv[];
    static char hash1[];
    static char hash2[];
};

#endif
