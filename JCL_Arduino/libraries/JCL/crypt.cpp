#include "crypt.h"

char Crypt::iv[16];
char Crypt::hash1[16];
char Crypt::hash2[16];

static void Crypt::update(JCL* jcl){
  unsigned char* hash;
  Sha256.init();
  Sha256.print(jcl->getKey());
  hash = Sha256.result();
  for (int i=0; i<16; i++){
    Crypt::hash1[i] = hash[i];
    Crypt::hash2[i] = hash[i + 16];
  }
}

int Crypt::padding(char* message, int dataSize){
  int x;
  if ( dataSize % 16 == 0 ){
    for (x = dataSize; x < dataSize + 16; x++){
      message[x] = 16;
    }
  }else{
    int existingBytes = (dataSize / 16) + 1;
    int startingBit = existingBytes * 16 - dataSize;
    for (x = dataSize; x < dataSize + startingBit ; x++){
      message[x] = startingBit;
    }
  }
  return x;
}

char* Crypt::generateRegistrationKey(char* cryptedMessage, char* iv, int messageSize, char* myHash){
  Sha256.initHmac(myHash, 16);
  Sha256.write(iv, 16);
  Sha256.write(cryptedMessage, messageSize);
  char* regKey= Sha256.resultHmac();
  return regKey;
}

char* Crypt::generateIV(){
  for (uint8_t it=0; it<16; it++)
    iv[it] = random(255);
  return iv;
}

int Crypt::cryptMessage(int messageSize, JCL* jcl, char iv[], char* hash){
  int bytes = padding(jcl->message, messageSize);
  aes128_cbc_enc(hash, iv, jcl->message, bytes);
  return bytes;
}

int Crypt::decryptMessage(int messageSize, JCL* jcl){
  char regKey[32];
  for (int i=0; i<16; i++)
    iv[i] = jcl->message[i + 13];

  for (int i=0; i<32; i++)
    regKey[i] = jcl->message[i + 29];

  char data[messageSize - 61];
  int x;
  for (x=0; x<messageSize-61; x++)
    data[x] = jcl->message[x + 61];

  char* m = generateRegistrationKey(data, iv, messageSize-61, hash2);
  for (int it=0; it<32; it++)
    if ( regKey[it] != m[it] )
      return -1;

  aes_context ctx = aes128_cbc_dec_start(hash1, iv);
  aes128_cbc_dec_continue(ctx, data, messageSize-61);
  aes128_cbc_dec_finish(ctx);

  for (int i=13; i<messageSize; i++)
    jcl->message[i] = data[i-13];

  int unpadding = messageSize - 48 - jcl->message[messageSize - 48 - 1];
  jcl->message[unpadding] = '\0';

  messageSize = unpadding;
  char *value = (char*)&messageSize;
  jcl->message[0] = 0;
  jcl->message[1] = 0;
  if ( messageSize <= 255 ){  // If the size is less then 256, we only need one byte to store the size
      jcl->message[2] = 0;
      jcl->message[3] = value[0];
  }
  else{       // Otherwise we use two bytes to store the size (up to 32000 bytes)
      jcl->message[2] = value[1];
      jcl->message[3] = value[0];
  }
  return messageSize;
}
