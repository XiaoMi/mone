FROM micr.cloud.mioffice.cn/mixiao/miserver:0.2.37-jaeger-jacoco
MAINTAINER shanwb
LABEL ENV_ID=1002 PROJECT_ID=1002
WORKDIR /home/work/hera-operator
COPY target/hera-operator-server-1.0-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar","-Dkeycenter.agent.host=172.17.0.1", "/home/work/hera-operator/hera-operator-server-1.0-SNAPSHOT.jar"]
