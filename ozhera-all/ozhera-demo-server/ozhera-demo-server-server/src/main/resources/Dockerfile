FROM miserver
MAINTAINER youpin-biz-arch@xiaomi.com
RUN mkdir -p /home/work/zxw_test2/
RUN mkdir -p /home/work/log/zxw_test2/
COPY zxw_test2-server-null-SNAPSHOT.jar /home/work/zxw_test2/
ENTRYPOINT ["java","-jar","-Xms512M","-Xmx512M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/zxw_test2/gc.log","/home/work/zxw_test2/zxw_test2-server-null-SNAPSHOT.jar"]