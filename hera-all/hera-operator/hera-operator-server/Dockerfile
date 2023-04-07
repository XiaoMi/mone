FROM openjdk:8-jre

COPY ./target/hera-operator-server-1.0-SNAPSHOT.jar /home/work/hera-operator-server-1.0-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Shanghai","/home/work/hera-operator-server-1.0-SNAPSHOT.jar","&&","tail","-f","/dev/null"]