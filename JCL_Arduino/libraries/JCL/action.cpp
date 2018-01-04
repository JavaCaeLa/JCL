#include "action.h"

void Action::deleteAction(){
  free(className);
  free(methodName);
  free(hostIP);
  free(hostPort);
  free(hostMac);
  free(ticket);
  free(param);
  free(this);
}

void Action::setUseSensorValue(bool useSensorValue){
  this->useSensorValue = useSensorValue;
}

bool Action::isUseSensorValue(){
  return this->useSensorValue;
}

void Action::setActing(bool acting){
  this->acting = acting;
}

bool Action::isActing(){
  return this->acting;
}

void Action::setClassName(char* className){
  this->className = className;
}

char* Action::getClassName(){
  return this->className;
}

void Action::setMethodName(char* methodName){
  this->methodName = methodName;
}

char* Action::getMethodName(){
  return this->methodName;
}

void Action::setHostIP(char* hostIP){
  this->hostIP = hostIP;
}

char* Action::getHostIP(){
  return this->hostIP;
}

void Action::setHostPort(char* hostPort){
  this->hostPort = hostPort;
}

char* Action::getHostPort(){
  return this->hostPort;
}

void Action::setHostMAC(char* hostMAC){
  this->hostMac = hostMAC;
}

char* Action::getHostMAC(){
  return this->hostMac;
}

void Action::setTicket(char* ticket){
  this->ticket = ticket;
}

char* Action::getTicket(){
  return this->ticket;
}

void Action::setParam(char* param){
  this->param = param;
}

char* Action::getParam(){
  return this->param;
}

uint16_t Action::getParamSize(){
  return this->paramSize;
}

void Action::setParamSize(uint16_t paramSize){
  this->paramSize = paramSize;
}

uint16_t Action::getClassNameSize(){
  return this->classNameSize;
}

void Action::setClassNameSize(uint16_t classNameSize){
  this->classNameSize = classNameSize;
}

uint16_t Action::getMethodNameSize(){
  return this->methodNameSize;
}

void Action::setMethodNameSize(uint16_t methodNameSize){
  this->methodNameSize = methodNameSize;
}

void Action::setSuperPeerPort(char *superPeerPort){
  this->superPeerPort = superPeerPort;
}

char* Action::getSuperPeerPort(){
  return this->superPeerPort;
}
