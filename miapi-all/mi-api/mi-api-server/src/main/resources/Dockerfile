FROM miserver
MAINTAINER dongzhenxing@xiaomi.com
RUN mkdir -p /home/work/mi-api/
RUN mkdir -p /home/work/log/mi-api/
COPY mi-api-server-1.0.0-SNAPSHOT.jar /home/work/mi-api/
ENTRYPOINT ["java","-jar","-Xms512M","-Xmx512M","-XX:+UseG1GC","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/mi-api/gc.log","/home/work/mi-api/mi-api-server-1.0.0-SNAPSHOT.jar"]