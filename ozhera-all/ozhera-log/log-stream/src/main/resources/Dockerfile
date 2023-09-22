FROM openjdk:8-jre

ENV APP_HOME /opt/app

# timezone to Asia/Shanghai
ENV TZ=Asia/Shanghai

RUN mkdir -p ${APP_HOME}

COPY target/log-stream-1.0-SNAPSHOT.jar ${APP_HOME}

WORKDIR ${APP_HOME}

ENTRYPOINT ["java", "-Duser.timezone=${TZ}", "-jar", "log-stream-1.0-SNAPSHOT.jar","&&","tail","-f","/dev/null"]

