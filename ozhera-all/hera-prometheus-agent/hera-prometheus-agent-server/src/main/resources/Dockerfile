FROM openjdk:8-jre
MAINTAINER youpin-biz-arch@xiaomi.com
RUN mkdir -p /home/work/prometheus-agent/
RUN mkdir -p /home/work/log/prometheus-agent/
COPY prometheus-agent-server-1.0.0-SNAPSHOT.jar /home/work/prometheus-agent/
ENTRYPOINT ["java","-jar","-Xms512M","-Xmx512M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/prometheus-agent/gc.log","/home/work/prometheus-agent/prometheus-agent-server-1.0.0-SNAPSHOT.jar"]