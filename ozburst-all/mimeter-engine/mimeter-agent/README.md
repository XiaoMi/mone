+ 压测agent
+ 使用jdk19-loom
+ 启动函数
+ com.xiaomi.sautumn.server.bootstrap.SautumnApplication
+ env 
+ function.App=mibench-agent;mione.faas.func.env=staging;mione.faas.func.env.id=92;mione.faas.func.id=65;mione.faas.local.jar.path=/Users/zzy/IdeaProjects/mibench-engine/mibench-agent/target/mibench-agent-1.0-SNAPSHOT.jar;mione.faas.test.jar.env=true
+ jvm 参数
+ --add-opens java.base/java.util=ALL-UNNAMED
+ --add-opens java.base/java.lang=ALL-UNNAMED
+ --add-opens java.base/java.math=ALL-UNNAMED
+ --add-opens java.xml/com.sun.org.apache.xerces.internal.impl.dv.util=ALL-UNNAMED
+ 配置名称
+ mione.faas.func.env.id=1;mione.faas.func.id=1  (agent)
+ serviceName=new_mibench_manager
+ nacosAddr=127.0.0.1:80
+ mione.faas.web.port=8080
+ 支持压测类型
  + http
  + dubbo
  + demo(用来测试代码)
+ 支持debug模式(可以获取到结果)
