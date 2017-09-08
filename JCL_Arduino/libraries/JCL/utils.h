#ifndef Utils_h
#define Utils_h

#include <Arduino.h>

class Utils{
  public:
    static unsigned char hexDigit(char ch);
    static byte* macAsByteArray(char* mac);
    static int* getIPAsArray(char* IP);
};

#endif
