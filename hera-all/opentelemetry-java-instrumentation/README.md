# 概况
这是Hera用于拦截方法，提取trace数据的项目，我们一般称它为"探针"。是以开源版本的Opentelemetry为基础，添加了Hera相关的instrumentation、exporter、jvm metrics等功能。
## 构建依赖

### jdk
此项目依赖的jdk版本为11及以上版本

### gradle
1、下载安装gradle-7.0.1

2、指定gradle数据源。在gradle安装目录下，新建init.d目录，在init.d目录下新建init.gradle文件，文件内容可以通过写入：

    allprojects {
        repositories {
            mavenLocal()
            maven { url 'https://maven.aliyun.com/nexus/content/repositories/central/' }
            mavenCentral()
        }
    }

来使用阿里的国内镜像仓库，加快依赖文件的下载速度

3、将项目导入idea之后，需要配置该项目对应的idea内部的gradle：
`Gradle user home`：需要配置gradle依赖的下载位置
`Use gradle from`：需要选择 'gradle-wrapper.properties'file
`Gradle JVM`：需要选择自己安装的jdk11的目录

4、在idea gradle工具栏中执行`Reload All Gradle Projects`，等待gradle下载依赖文件，这个过程对于首次导入来说，可能会花费30-60分钟的时间

5、在项目根目录执行`./gradlew assemble`进行构建。构建成功后，会在`javaagent`模块下的`build/libs`目录下生成`opentelemetry-javaagent-0.1.0-SNAPSHOT-all.jar`，这个jar文件就是最终的探针。

## 运行依赖
### 环境变量
`host.ip`：用于记录当前物理机IP，展示在trace的process.tags里。在k8s里获取的是pod的IP

`node.ip`：用于记录k8s当前node节点的IP

`MIONE_LOG_PATH`：用于记录mione应用上的日志目录，将trace span信息存放在${MIONE_LOG_PATH}/trace/trace.log。如果为空，程序里默认使用/home/work/log/none/trace/trace.log。

`mione.app.name`：用于记录服务名，格式是projectId-projectName。eg：1-test，1是projectId，test是projectName。如果为空，程序里默认使用none

`TESLA_HOST`：同host.ip。用于注册nacos、jvm metrics里的serverIp标签。

`JAVAAGENT_PROMETHEUS_PORT`：当前物理机可用端口号，用于提供给Prometheus拉取jvm metrics的httpServer使用。如果为空，程序里默认使用55433。

`hera.buildin.k8s`：用于记录是否是k8s部署的服务，如果是k8s的服务，标记为1。

`MIONE_PROJECT_ENV_NAME`：当前部署环境的名称，eg：dev、uat、st、preview、production

`MIONE_PROJECT_ENV_ID`：当前部署环境的ID。

### JVM参数
`-javaagent:/opt/soft/opentelemetry-javaagent-all-0.0.1.jar`：用于表示javaagent探针jar包在服务器上的位置，我们一般习惯将探针的jar文件更名为`opentelemetry-javaagent-all-0.0.1.jar`，并放在服务器`/opt/soft`目录下。

`-Dotel.resource.attributes=service.name=1-test`：用于表示当前服务的应用名。应用名应当与`mione.app.name`保持一致

`-Dotel.traces.exporter=log4j2`：用于表示trace export方式

`-Dotel.metrics.exporter=prometheus`：用于表示metrics export方式

`-Dotel.javaagent.debug=false`：用于表示是否开启debug日志，一般线上服务不建议开启

`-Dotel.exporter.prometheus.nacos.addr=${nacosurl}`：用于表示nacos地址。需要将nacos地址端口进行配置，例如：127.0.0.1:80

`-Dotel.javaagent.exclude-classes=com.dianping.cat.*`：过滤不被探针拦截的包。如果使用到了cat，需要将cat所在的目录进行过滤

`-Dotel.exporter.log.isasync=true`：用于表示是否开log4j2启异步日志，一般出于性能考虑，会是true

`-Dotel.exporter.log.pathprefix=/home/work/log/`：用于表示log4j2的日志位置

`-Dotel.propagators=tracecontext`：用于表示trace传输的处理类型，目前只用到了tracecontext。

## Opentelemetry-java
更多细节、配置、设计原理可以查看开源版opentelemetry-java-instrumentation：

https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/v1.3.x