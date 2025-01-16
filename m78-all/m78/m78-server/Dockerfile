FROM openjdk:21

ENV APP_HOME /opt/app

RUN mkdir -p ${APP_HOME}

RUN echo 'Asia/Shanghai' >/etc/timezone

COPY target/m78-server-*.jar ${APP_HOME}/m78-server.jar

WORKDIR ${APP_HOME}

ENTRYPOINT ["java","--add-opens", "java.base/java.time=ALL-UNNAMED","--add-opens", "java.base/java.util=ALL-UNNAMED","--add-opens", "java.base/java.lang=ALL-UNNAMED","--add-opens", "java.base/java.math=ALL-UNNAMED","--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED","--add-opens", "java.base/java.sql=ALL-UNNAMED","--add-opens", "java.base/java.util.concurrent.atomic=ALL-UNNAMED","--add-opens", "java.xml/com.sun.org.apache.xerces.internal.impl.dv.util=ALL-UNNAMED","--enable-preview","-Duser.timezone=Asia/Shanghai","-jar","m78-server.jar","&&","tail","-f","/dev/null"]