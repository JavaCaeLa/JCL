# JCLforArduino

To use JCL Host for Arduino, download the folder "libaries" and paste its content on the library folder of Arduino installation. All necessary libraries to use JCL is under that directory. An example of how to start Arduino Host is the following:


```arduino
#include <jcl.h>

void setup() {
  Serial.begin(9600); 

  char hostIP[] = "192.168.1.199",
        hostMAC[] = "AA-BB-CC-DD-EE-FF",        
        serverIP[] = "192.168.1.16";

  int hostPort = 5151,
      serverPort = 6969;

  JCL jcl(hostIP, hostPort, hostMAC);
  jcl.configureJCLServer(serverIP, serverPort);
  jcl.setEncryption(false); // optional (Default = false)
  jcl.changeBoardNickname("arduino"); // optional (Board Nickname)
  jcl.useEEPROM(false);  // optional (default = true) if false Arduino won't restore the configuration of all sensors and contexts when a reboot occurs
  jcl.setBrokerData(serverIP, 1883);	// mqtt broker configuration
  jcl.startHost();
}

// no code is necessary in this method
void loop() {
}
```

