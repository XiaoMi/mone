FROM miserver:0.2.31-jaeger

WORKDIR /home/work/tesla-file-server

RUN mkdir -p /home/work/log/fsserver/

COPY ./target/fileserver.jar /home/work/tesla-file-server/

ENTRYPOINT ["java", "-jar", "-Xms1024M", "-Xmx1024M", "-XX:MaxDirectMemorySize=512M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-XX:+PrintReferenceGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-XX:+PrintHeapAtGC", "-verbose:gc", "-Xloggc:/home/work/log/fsserver/gc.log", "/home/work/tesla-file-server/fileserver.jar"]
