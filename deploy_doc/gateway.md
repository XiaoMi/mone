## tesla-gateway迁移部署

### 配置
新建对应业务的properties，在根目录pom的profile模块添加对应的profile

基于已有的配置
复制c3.properties内容到
按照需求修改以下配置

+ 项目相关
    - 插件本地缓存路径 plugin.path
    - api信息本地缓存路径 cache.route.path

+ dubbo+nacos相关
	- dubbo.group
	- dubbo.protocol.id
	- dubbo.protocol.name
	- dubbo.protocol.port
    - dubbo.registry.address
    - nacos.config.addrs
    - app.nacos

+ mysql
	- spring.datasource.url
	- 数据库已经迁移到nacos配置中 dataId:tesla_quota_config 内容spring.datasource.username=gwdash_intra_x spring.datasource.password=xxxxx

+ redis
    - spring.redis
    - 是否选择集群模式 redis.cluster

+ rocketmq
    - rocketmq.namesrv.addr

### 编译

tesla-gateway下

mvn clean -U compile package -P yourprofile -Dmaven.test.skip=true

