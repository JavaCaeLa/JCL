#include "sensor.h"
#include "constants.h"
#include "jcl.h"
#include <Servo.h>

Sensor::Sensor(){
  lastExecuted = 0;
count = 0;
  this->numContexts = 0;
  for (uint8_t x=0; x<MAX_CONTEXTS; x++)
    enabledContexts[x] = NULL;
}

bool Sensor::acting(float value){
  if ( this->type == Constants::SERVO_ACTUATOR ){
    Servo servo;
    servo.attach(atoi(this->pin));
    servo.write(value);
    return true;
  }
  else if ( digitalPin() ){
//      pinMode(pin, OUTPUT);
    if ( value != LOW && value != HIGH)
      return false;
    digitalWrite(atoi(this->pin), value);
  }
  else{
    if ( value < 0 || value > 255)
      return false;
    analogWrite(getAnalogPin(), value);
  }
  return true;
}

void Sensor::deleteSensor(){
  for (int i=0; i<numContexts; i++){
    if (enabledContexts[i] != NULL){
      enabledContexts[i]->deleteContext();
      enabledContexts[i] = NULL;
    }
  }
  free(this->pin);
  free(this->sensorNickname);
  free(this);
}

void Sensor::configurePinMode(){
    if ( this->type == Constants::SERVO_ACTUATOR ){
      Servo servo;
      servo.attach(atoi(pin));
    }
    else if (digitalPin()){
      if ( typeIO == Constants::CHAR_OUTPUT )
         pinMode(atoi(pin), OUTPUT);
      else
        pinMode(atoi(pin), INPUT);
    }
}

boolean Sensor::digitalPin(){
  if ( atoi(pin) < JCL::getTotalDigitalSensors() )
    return true;
  return false;
}

boolean Sensor::validPin(int pin){
  if ( pin < 0 || pin >= JCL::getTotalSensors())
    return false;
  return true;
}

int Sensor::getAnalogPin(){
  return atoi(pin) - JCL::getTotalDigitalSensors();
}

void Sensor::setPin(char* pin){
  this->pin = pin;
}

char* Sensor::getPin(){
    return this->pin;
}

void Sensor::setSensorNickname(char* sensorNickname){
    this->sensorNickname = sensorNickname;
}

char* Sensor::getSensorNickname(){
  return this->sensorNickname;
}

void Sensor::setTypeIO(char typeIO){
  this->typeIO = typeIO;
}

char Sensor::getTypeIO(){
  return this->typeIO;
}

void Sensor::setDelay(char* delay){
  this->delay = delay;
}

char* Sensor::getDelay(){
  return this->delay;
}

void Sensor::setLastExecuted(long lastExecuted){
  this->lastExecuted = lastExecuted;
}

long Sensor::getLastExecuted(){
  return this->lastExecuted;
}

void Sensor::setValue(float value){
  this->value = value;
}

float Sensor::getValue(){
  return this->value;
}

void Sensor::setType(int type){
  this->type = type;
}

int Sensor::getType(){
  return this->type;
}

void Sensor::setSensorSize(char* sensorSize){
  this->sensorSize = sensorSize;
}

char* Sensor::getSensorSize(){
  return this->sensorSize;
}

Context** Sensor::getEnabledContexts(){
    return this->enabledContexts;
}

uint8_t Sensor::getNumContexts(){
  return this->numContexts;
}

void Sensor::setNumContexts(uint8_t numContexts){
  this->numContexts = numContexts;
}
