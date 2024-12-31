FROM miserver:0.2.31-jaeger
MAINTAINER youpin-biz-arch@xiaomi.com
RUN mkdir -p /home/work/mimeter-dashboard/
RUN mkdir -p /home/work/log/mimeter-dashboard/
COPY ./target/mimeter-dashboard-server-1.0.0-SNAPSHOT.jar /home/work/mimeter-dashboard/
ENTRYPOINT ["java","-jar","-Xms1024M","-Xmx1024M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/mimeter-dashboard/gc.log","/home/work/mimeter-dashboard/mimeter-dashboard-server-1.0.0-SNAPSHOT.jar"]