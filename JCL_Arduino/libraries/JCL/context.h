#ifndef Context_h
#define Context_h

#define MAX_NUMBER_DIGITS 8
#define MAX_ACTIONS_PER_CONTEXT 6
#define OPERATORS_MAX_SIZE 3

#include "action.h"

class Context{
  public:
    Context();
    void setNickname(char* nickname);
    char* getNickname();
    void setExpression(char* expression);
    char* getExpression();
    char** getOperators();
    char** getThreshold();
    void setNumExpressions(int numExpressions);
    int getNumExpressions();
    void setNumActions(int numActions);
    int getNumActions();
    void setTriggered(bool triggered);
    bool isTriggered();
    void setMQTTContext(bool mqttContext);
    bool isMQTTContext();
    Action** getEnabledActions();
    void deleteContext();
  private:
    char* nickname;
    char* expression;
    char** operators;
    char** threshold;
    int numExpressions;
    int numActions;
    bool triggered;
    bool mqttContext;
    Action* enabledActions[MAX_ACTIONS_PER_CONTEXT];
};

#endif
