#!/bin/sh

java -cp "../applications" -XX:+TieredCompilation -Djava.net.preferIPv4Stack=true -jar JCL_MQTT-1.0.jar
