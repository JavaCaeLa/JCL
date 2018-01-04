#include "context.h"
#include "action.h"

Context::Context(){
  mqttContext = false;
  this->operators = new char * [MAX_ACTIONS_PER_CONTEXT];
  for (int i=0; i<MAX_ACTIONS_PER_CONTEXT;i++)
    this->operators[i] = new char[OPERATORS_MAX_SIZE];
  this->threshold = new char * [MAX_ACTIONS_PER_CONTEXT];
  for (int i=0; i<MAX_ACTIONS_PER_CONTEXT;i++)
    this->threshold[i] = new char[MAX_NUMBER_DIGITS];

  this->numExpressions = 0;
  for (uint8_t x = 0; x < MAX_ACTIONS_PER_CONTEXT; x++)
    this->enabledActions[x] = NULL;
  this->numActions = 0;
  this->triggered = false;
}

void Context::deleteContext(){
  for (int i=0; i<numActions;i++)
    enabledActions[i]->deleteAction();
  free(this->nickname);
  free(this->expression);
  for (int i=0; i<MAX_ACTIONS_PER_CONTEXT;i++){
    free(this->operators[i]);
    free(this->threshold[i]);
  }
  free(this->operators);
  free(this->threshold);
  free(this);
}

void Context::setNickname(char* nickname){
  this->nickname = nickname;
}

char* Context::getNickname(){
  return this->nickname;
}

void Context::setExpression(char* expression){
  this->expression = expression;
}

char* Context::getExpression(){
  return this->expression;
}

void Context::setNumExpressions(int numExpressions){
  this->numExpressions = numExpressions;
}

int Context::getNumExpressions(){
  return this->numExpressions;
}

void Context::setNumActions(int numActions){
  this->numActions = numActions;
}

int Context::getNumActions(){
  return this->numActions;
}

void Context::setTriggered(bool triggered){
  this->triggered = triggered;
}

bool Context::isTriggered(){
  return this->triggered;
}

Action** Context::getEnabledActions(){
  return this->enabledActions;
}

char** Context::getOperators(){
  return this->operators;
}

char** Context::getThreshold(){
  return this->threshold;
}

void Context::setMQTTContext(bool mqttContext){
  this->mqttContext = mqttContext;
}

bool Context::isMQTTContext(){
  return this->mqttContext;
}
