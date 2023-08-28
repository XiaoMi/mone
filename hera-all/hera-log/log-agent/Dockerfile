FROM micr.cloud.mioffice.cn/mixiao/openjdk:20-jdk-bullseye

ENV APP_HOME /opt/app

RUN mkdir -p ${APP_HOME}

COPY target/log-agent-1.0-SNAPSHOT.jar ${APP_HOME}

WORKDIR ${APP_HOME}

ENTRYPOINT ["java","--enable-preview", "-jar", "log-agent-1.0-SNAPSHOT.jar"]