FROM openjdk:21-slim

ENV APP_HOME=/opt/app \
    APP_USER=appuser \
    APP_GROUP=appgroup \
    TZ=Asia/Shanghai \
    TEST_DB_URL=your_db_url \
    TEST_DB_USER_NAME=your_db_username \
    TEST_DB_PASSWORD=your_db_password \
    TEST_DB_NAME=your_db_name \
    hive_manager_token=your_token

# 创建非root用户
RUN groupadd -r ${APP_GROUP} && \
    useradd -r -g ${APP_GROUP} -d ${APP_HOME} ${APP_USER} && \
    mkdir -p ${APP_HOME} && \
    chown -R ${APP_USER}:${APP_GROUP} ${APP_HOME} && \
    echo ${TZ} > /etc/timezone

# 复制应用文件
COPY --chown=${APP_USER}:${APP_GROUP} target/hive-manager-*.jar ${APP_HOME}/app.jar

WORKDIR ${APP_HOME}

# 切换到非root用户
USER ${APP_USER}

ENTRYPOINT ["java","--add-opens", "java.base/java.time=ALL-UNNAMED","--add-opens", "java.base/java.util=ALL-UNNAMED","--add-opens", "java.base/java.lang=ALL-UNNAMED","--add-opens", "java.base/java.math=ALL-UNNAMED","--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED","--add-opens", "java.base/java.sql=ALL-UNNAMED","--add-opens", "java.base/java.util.concurrent.atomic=ALL-UNNAMED","--add-opens", "java.xml/com.sun.org.apache.xerces.internal.impl.dv.util=ALL-UNNAMED","--enable-preview","-Dschedule.enable=true","-Duser.timezone=Asia/Shanghai","-Djava.security.egd=file:/dev/./urandom","-Dcom.sun.jndi.ldap.object.trustURLCodebase=false","-Dcom.sun.jndi.rmi.object.trustURLCodebase=false","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]