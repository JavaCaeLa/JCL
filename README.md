# JCL
The JCL (Java Cá&Lá) is a distributed general purpose computing middleware supporting tasks, maps and global variables concepts. It is designed for Java community.

A simple API is responsible to run existing Java code or store/instantiate existing objects, thus code refactoring can be minimal.

Previous versions of JCL can be found at [JCL old versions] (www.javacaela.org).

This JCL version is integrated with Kafka, a stream processing tool. Deployment and programming guides can be found at [JCL Kafka version] (www.javacaela.org).

Distributed Shared Memory (DSM) and Task Oriented (TO) programming models are still widely adopted nowadays due to their HPC code development simplicity. Examples of market-leader tools supporting these models include Memcached, Hazelcast, Infinispan, JPPF and others. Unfortunately, both DSM and TO solutions normally perform unnecessary network communications while solving the coherence problem. To test our hypothesis, in an undergraduate final work, we have designed, reimplemented and evaluated an existing DSM/TO middleware, named JCL, using the Kafka Pub/Sub pattern to reduce the number of messages exchanged and consequently the runtime of applications using both models. DSM/TO objects are no longer explicitly obtained by JCL clients. Instead, clients are notified when object/task topics change. Furthermore, the TO wait-condition primitive, fundamental to perform synchronization barriers for concurrent tasks, is reimplemented using the Kafka Pub/Sub event schema. All DSM and TO API services are preserved, thus Kafka benefits are transparent for JCL users.
