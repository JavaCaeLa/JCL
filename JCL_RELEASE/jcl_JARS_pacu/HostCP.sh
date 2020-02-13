#!/bin/sh

java -XX:+TieredCompilation -XX:+UseNUMA -Xms2G -Xmx2G -Djava.net.preferIPv4Stack=true -cp "./JCL_Host.jar" implementations.dm_kernel.host.MainHost
