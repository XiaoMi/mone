FROM miserver
MAINTAINER dongzhenxing@xiaomi.com
RUN mkdir -p /home/work/mock-server/
RUN mkdir -p /home/work/log/mock-server/
COPY mock-server-server/target/mock-server-server-1.0.0-SNAPSHOT.jar /home/work/mock-server/
ENTRYPOINT ["java","-jar","-Xms2048M","-Xmx2048M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/mock-server/gc.log","/home/work/mock-server/mock-server-server-1.0.0-SNAPSHOT.jar"]