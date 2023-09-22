# 概述
# 如何部署
## 部署依赖
（1）MySQL，建表语句同trace-etl-manager

（2）Redis

（3）Nacos

（4）ES

（5）RocketMQ

### 容器化
`k8s Stateful Set`：trace-etl-es目前需要部署在k8s中，并且类型是Stateful Set。

### 文件磁盘挂载目录

`/home/rocksdb`：这个目录下是rocksdb缓存的span数据，需要持久化存储，不会因为容器重启而被删除。需要将trace-etl-es部署的服务设置为Stateful Set，然后创建pvc，将此目录挂载出去。

### 环境变量

`CONTAINER_S_POD_NAME`：这个是k8s Stateful Set 的podName。k8s的Stateful Set类型的pod，会在podName后面自动拼接从0开始的递增数，比如

`trace-etl-es-podname-0`

`trace-etl-es-podname-1`

`trace-etl-es-podname-2...`

## 使用maven构建
在项目根目录下（trace-etl）执行：

`mvn clean install -U -P opensource -DskipTests`

会在trace-etl-es模块下生成target目录，target目录中的trace-etl-es-1.0.0-SNAPSHOT.jar就是运行的jar文件。
## 运行
执行：

`java -jar trace-etl-es-1.0.0-SNAPSHOT.jar`

就可以运行trace-etl-es。