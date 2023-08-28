# Overview

* Based on Java20
* JVM parameters that need to be added: --enable-preview--add-modulesjdk.incubator.concurrent-ea--add-opensjava.base/java.lang=ALL-UNNAMED--add-opensjava.base/jdk.internal.misc=ALL-UNNAMED-Dio.netty.tryReflectionSetAccessible=true
* A lightweight microservice development framework that can be embedded into the Spring framework.
* A lightweight microservices development framework. It can be embedded into the Spring framework.
* Features: Compliant with Java standards, lightweight, no unnecessary libraries, low memory footprint, fast service
  requests, high maintainability, and supports plugin extensions.
* 支持IOC、AOP，提供mysql、dubbo、redis、nacos、rocketmq、sentinel Waiting for plugins
* Supports IOC and AOP, offers plugins for MySQL, Dubbo, Redis, Nacos, RocketMQ, Sentinel, and more.
* User Manual：
*         <dependency>
            <artifactId>docean</artifactId>
            <groupId>com.xiaomi.youpin</groupId>
            <version>1.4-SNAPSHOT</version>
        </dependency>
        <dependency>
            <artifactId>docean-plugin</artifactId>
            <groupId>com.xiaomi.youpin</groupId>
            <version>1.4-SNAPSHOT</version>
        </dependency>
  The docean package mainly provides the initialization process of the project framework, including annotation scanning
  processing, bean initialization, and loading into the container.
  The plugin package has added extended support for some commonly used dependencies in projects, such as nacos, dubbo,
  mybatis, and so on.
* rate limited or exceeded quota
*     public static void main(String... args) {
        try {
            Aop.ins().init(Maps.newLinkedHashMap());
            Ioc.ins().init("com.xiaomi.mone");
            HttpServerConfig config = new HttpServerConfig();
            config.setPort(8999);
            config.setSsl(false);
            config.setWebsocket(true);
            DoceanHttpServer server = new DoceanHttpServer(config);
            server.start();
        } catch (Throwable throwable) {
            System.exit(-1);
        }
  }
* Main authors: Wenbang, Dingpei, Zhang Zhiyong, Ding Tao, Zhidong, Zhenxing