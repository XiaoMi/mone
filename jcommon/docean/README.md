# Overview
* Because ozhera's log agent needs strong restrictions on the size of the code, docean was developed here (the
  functionality may not be perfect, but it is very small).
* Based on Java20
* Fully utilized coroutines and ScopeValue
* JVM parameters that need to be added:
  --enable-preview --add-modulesjdk.incubator.concurrent -ea --add-opensjava.base/java.lang=ALL-UNNAMED--add-opensjava.base/jdk.internal.misc=ALL-UNNAMED-Dio.netty.tryReflectionSetAccessible=true
* A lightweight microservices development framework. It can be embedded into the Spring framework.
* Features: Compliant with Java standards, lightweight, no unnecessary libraries, low memory footprint, fast service
  requests, high maintainability, and supports plugin extensions.
* Support IOC, AOP，Provide mysql、dubbo、redis、nacos、rocketmq、sentinel Waiting for plugins
* Supports IOC and AOP, offers plugins for MySQL, Dubbo, Redis, Nacos, RocketMQ, Sentinel, and more.
* User Manual：
  The docean package mainly provides the initialization process of the project framework, including annotation scanning
  processing, bean initialization, and loading into the container.
  The plugin package has added extended support for some commonly used dependencies in projects, such as nacos, dubbo,
  mybatis, and so on.
* rate limited or exceeded quota