#include "utils.h"

unsigned char Utils::hexDigit( char ch )
{
    if( ( '0' <= ch ) && ( ch <= '9' ) )
      ch -= '0';
    else
    {
        if( ( 'a' <= ch ) && ( ch <= 'f' ) )
          ch += 10 - 'a';
        else
        {
            if( ( 'A' <= ch ) && ( ch <= 'F' ) )
              ch += 10 - 'A';
            else
              ch = 16;
        }
    }
    return ch;
}

byte* Utils::macAsByteArray(char *mac){
  byte *castMac = (byte*)malloc(sizeof(byte)*6);
  for( uint8_t idx = 0; idx < sizeof(mac)/sizeof(mac[0]); ++idx )
  {
      castMac[idx]  = hexDigit( mac[ 3 * idx ] ) << 4;
      castMac[idx] |= hexDigit( mac[ 1 + 3 * idx ] );
  }
  return castMac;
}

int* Utils::getIPAsArray(char *IP){
  char* ipTokenizer = (char*)malloc(sizeof(char)*strlen(IP));
  strcpy(ipTokenizer, IP);
  char *token = strtok(ipTokenizer, ".");
  int* ip = (int *) malloc(sizeof(int)*4),
    cont = 0;
  while (token != NULL){
    ip[cont++] = atoi(token);
    token = strtok(NULL, ".");
  }
  return ip;
}
