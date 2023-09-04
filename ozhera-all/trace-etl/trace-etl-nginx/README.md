# 概述
# 如何部署
## 依赖
（1）RocketMQ
## 使用maven构建
在项目根目录下（trace-etl）执行：

`mvn clean install -U -P opensource -DskipTests`

会在trace-etl-nginx模块下生成target目录，target目录中的trace-etl-nginx-1.0.0-SNAPSHOT.jar就是运行的jar文件。
## 运行
执行：

`java -jar trace-etl-nginx-1.0.0-SNAPSHOT.jar`

就可以运行trace-etl-nginx。