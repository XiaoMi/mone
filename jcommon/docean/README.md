# 概述
* 一个轻量级微服务开发框架。可以嵌入spring框架
* A lightweight microservices development framework. It can be embedded into the Spring framework. 
* 特点: 符合Java标准、轻量级、没有无用的库、内存占用少、快速服务请求、可维护性高 支持plugin扩展
* Features: Compliant with Java standards, lightweight, no unnecessary libraries, low memory footprint, fast service requests, high maintainability, and supports plugin extensions.
* 支持IOC、AOP，提供mysql、dubbo、redis、nacos、rocketmq、sentinel等插件
* 使用说明：
新创建的java项目只需要引入一下两个依赖：
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
  docean包中主要提供了包括注解的扫描处理、bean的初始化及载入容器等项目框架的初始化流程。
  plugin包中加入了对于一些项目常用的依赖的扩展支持，如nacos、dubbo、mybatis等等。
* 启动类中指定服务端口：
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
启动Bootstrap类即可运行server。
* 对于提供http接口的项目，docean中提供与spring保持一致的注解例如:@Controller、@RequestMapping等
* 主要作者: 文榜 丁佩 张志勇 刘玉冲 张秀华 丁涛 志东 振兴