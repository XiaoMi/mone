FROM openjdk:8-jre

ENV APP_HOME /opt/app

RUN mkdir -p ${APP_HOME}

COPY target/log-agent-1.0-SNAPSHOT.jar ${APP_HOME}

WORKDIR ${APP_HOME}

ENTRYPOINT ["java", "-jar", "log-agent-1.0-SNAPSHOT.jar"]