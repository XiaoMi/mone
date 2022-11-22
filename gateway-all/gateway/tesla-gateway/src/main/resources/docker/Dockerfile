FROM miserver
MAINTAINER dingpei@xiaomi.com
ENV MIONE_PROJECT_ENV_NAME='mione'
ENV MIONE_LOG_PATH='/tmp/log/tesla/'

LABEL ENV_ID="1" PROJECT_ID="1"
RUN mkdir -p /home/work/youpin-gateway_mione/
RUN mkdir -p /home/work/log/tesla/
COPY tesla-gateway-1.0.0-SNAPSHOT.jar /home/work/youpin-gateway_mione/
ENTRYPOINT ["bash","-c","exec java -jar -Dkeycenter.agent.host=172.17.0.1 -XX:ActiveProcessorCount=4 -Xms6g -Xmx6g -XX:MaxDirectMemorySize=2048M -XX:ReservedCodeCacheSize=256m -XX:InitialCodeCacheSize=256m -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/work/log/tesla/gc-%t.log --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xerces.internal.impl.dv.util=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED -Dotel.resource.attributes=service.name=tesla-getaway-mione -Dotel.traces.exporter=log4j2 -Dotel.javaagent.exclude-classes=com.dianping.cat.* -Dotel.exporter.log.isasync=true -Dotel.exporter.log.pathprefix=/home/work/log/ -Dotel.propagators=tracecontext -Dotel.instrumentation.exclude.module=docean,external-annotations,grpc,java-http-client,jdbc,jedis,jedis-factory,jetty,lettuce,logback,methods,okhttp,rocketmq-client,servlet,spring-webmvc,tomcat,http-url-connection /home/work/youpin-gateway_mione/tesla-gateway-1.0.0-SNAPSHOT.jar"]