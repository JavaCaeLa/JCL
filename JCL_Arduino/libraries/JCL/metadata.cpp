#include "metadata.h"
#include "Arduino.h"
#include <stdint.h>

Metadata::Metadata(){
  char name[] = "arduino", nSensors[] = "0";
  this->setStandBy(false);
  this->setBoardName(name);
  this->setNumConfiguredSensors(nSensors);
}

void Metadata::setBoardName(char* boardName){
  this->boardName = (char *) malloc(sizeof(char) * strlen(boardName) + 1);
  for (uint8_t i=0; i<strlen(boardName); i++)
    this->boardName[i] = boardName[i];
  // strcpy(this->boardName, boardName);
  this->boardName[strlen(boardName)] = '\0';
}

char* Metadata::getBoardName(){
  return this->boardName;
}

void Metadata::setHostIP(char* hostIP){
  this->hostIP = (char *) malloc(sizeof(char) * strlen(hostIP) + 1);
  strcpy(this->hostIP, hostIP);
}

char* Metadata::getHostIP(){
  return this->hostIP;
}

void Metadata::setHostPort(char* hostPort){
  this->hostPort = (char *) malloc(sizeof(char) * strlen(hostPort) + 1);
  strcpy(this->hostPort, hostPort);
}

char* Metadata::getHostPort(){
  return this->hostPort;
}

void Metadata::setServerIP(char* serverIP){
  this->serverIP = (char *) malloc(sizeof(char) * strlen(serverIP) + 1);
  strcpy(this->serverIP, serverIP);
}

char* Metadata::getServerIP(){
  return this->serverIP;
}

void Metadata::setServerPort(char* serverPort){
  this->serverPort = (char *) malloc(sizeof(char) * strlen(serverPort) + 1);
  strcpy(this->serverPort, serverPort);
}

char* Metadata::getServerPort(){
  return this->serverPort;
}

void Metadata::setNumConfiguredSensors(char *numConfiguredSensors){
  this->numConfiguredSensors = (char *) malloc(sizeof(char) * strlen(numConfiguredSensors) + 1);
  strcpy(this->numConfiguredSensors, numConfiguredSensors);
}

char* Metadata::getNumConfiguredSensors(){
  return this->numConfiguredSensors;
}

void Metadata::setMAC(char *mac){
  this->mac = mac;
}

char* Metadata::getMAC(){
  return this->mac;
}

void Metadata::setStandBy(bool standBy){
  this->standBy = standBy;
}

bool Metadata::isStandBy(){
  return this->standBy;
}

void Metadata::setBrokerIP(char* brokerIP){
  this->brokerIP = brokerIP;
}

void Metadata::setBrokerPort(int brokerPort){
this->brokerPort = brokerPort;
}

int Metadata::getBrokerPort(){
  return brokerPort;
}

char* Metadata::getBrokerIP(){
  return brokerIP;
}
