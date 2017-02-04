#!/bin/sh

java -XX:+TieredCompilation -XX:+UseNUMA -Xms2G -Xmx2G -Djava.net.preferIPv4Stack=true -jar JCL_Host-1.0.jar
