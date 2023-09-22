# 概述
# 如何部署
### 依赖
（1）MySQL，建表语句同trace-etl-manager

（2）RocketMQ

（3）Nacos

（4）ES
### 环境变量

`CONTAINER_S_IP`：本机的ip，如果是k8s部署，则为pod的ip

`CONTAINER_S_HOSTNAME`：本机的host name，如果是k8s部署，则为pod的name

以上两个环境变量，在trace-etl-server中使用`System.getenv()`方法获取，并注册到nacos，以供prometheus-agent拉取。

### 端口号

`4446`：4446端口是用于trace-etl-server的`HTTPServer`启动需要，所以不能被占用。

## 使用maven构建
在项目根目录下（trace-etl）执行：

`mvn clean install -U -P opensource -DskipTests`

会在trace-etl-server模块下生成target目录，target目录中的trace-etl-server-1.0.0-SNAPSHOT.jar就是运行的jar文件。
## 运行
执行：

`java -jar trace-etl-server-1.0.0-SNAPSHOT.jar`

就可以运行trace-etl-server。