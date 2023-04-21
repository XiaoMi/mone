FROM openjdk:8-jre
MAINTAINER zhangsan@qq.com
RUN mkdir -p /home/work/app-server/
RUN mkdir -p /home/work/log/app-server/
COPY ./target/app-server-1.0-SNAPSHOT.jar /home/work/app-server/app-server-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","-Xms512M","-Xmx512M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/app-server/gc.log","/home/work/app-server/app-server-1.0-SNAPSHOT.jar"]