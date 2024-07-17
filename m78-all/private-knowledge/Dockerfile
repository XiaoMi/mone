FROM openjdk:8

ENV APP_HOME /opt/app

RUN mkdir -p ${APP_HOME}

RUN echo 'Asia/Shanghai' >/etc/timezone

COPY private-knowledge-server/target/private-knowledge-server-*.jar ${APP_HOME}/private-knowledge-server.jar

WORKDIR ${APP_HOME}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar private-knowledge-server.jar"]