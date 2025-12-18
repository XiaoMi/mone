FROM eclipse-temurin:21-jdk-jammy

# 先声明构建参数
ARG DOUBAO_API_KEY
ARG DOUBAO_VISION_MODEL_KEY
ARG DOUBAO_UI_TARS_MODEL_KEY
ARG HIVE_MANAGER_TOKEN
ARG HIVE_MANAGER_BASE_URL_OZ

ENV APP_HOME=/opt/app \
    APP_USER=appuser \
    APP_GROUP=appgroup \
    TZ=Asia/Shanghai \
    DOUBAO_API_KEY=${DOUBAO_API_KEY} \
    DOUBAO_VISION_MODEL_KEY=${DOUBAO_VISION_MODEL_KEY} \
    DOUBAO_UI_TARS_MODEL_KEY=${DOUBAO_UI_TARS_MODEL_KEY} \
    HIVE_MANAGER_BASE_URL=${HIVE_MANAGER_BASE_URL_OZ} \
    HIVE_MANAGER_TOKEN=${HIVE_MANAGER_TOKEN}

# 创建非root用户
RUN groupadd -r ${APP_GROUP} && \
    useradd -r -g ${APP_GROUP} -d ${APP_HOME} ${APP_USER} && \
    mkdir -p ${APP_HOME} ${APP_HOME}/log ${APP_HOME}/config && \
    chown -R ${APP_USER}:${APP_GROUP} ${APP_HOME} && \
    echo ${TZ} > /etc/timezone

# 复制应用文件
COPY --chown=${APP_USER}:${APP_GROUP} target/app.jar ${APP_HOME}/app.jar

WORKDIR ${APP_HOME}

# 切换到非root用户
USER ${APP_USER}

# 使用原有参数，但删除不必要的tail命令
ENTRYPOINT ["java","--add-opens", "java.base/java.time=ALL-UNNAMED","--add-opens", "java.base/java.util=ALL-UNNAMED","--add-opens", "java.base/java.lang=ALL-UNNAMED","--add-opens", "java.base/java.math=ALL-UNNAMED","--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED","--add-opens", "java.base/java.sql=ALL-UNNAMED","--add-opens", "java.base/java.util.concurrent.atomic=ALL-UNNAMED","--add-opens", "java.xml/com.sun.org.apache.xerces.internal.impl.dv.util=ALL-UNNAMED","--enable-preview","-Dschedule.enable=false","-Duser.timezone=Asia/Shanghai","-Djava.security.egd=file:/dev/./urandom","-Dcom.sun.jndi.ldap.object.trustURLCodebase=false","-Dcom.sun.jndi.rmi.object.trustURLCodebase=false","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
