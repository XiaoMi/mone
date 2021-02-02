## tesla-quota迁移部署

### 配置
新建对应业务的properties，在根目录pom的profile模块添加对应的profile

基于已有的配置
复制c3.properties内容到
按照需求修改以下配置

+ dubbo+nacos相关
	- dubbo.group
	- dubbo.protocol.id
	- dubbo.protocol.name
	- dubbo.protocol.port
    - dubbo.registry.address
    - nacos.config.addrs
    - app.nacos

+ database
	- spring.datasource.url
	- 数据库已经迁移到nacos配置中 dataId:tesla_quota_config 内容spring.datasource.username=gwdash_intra_x spring.datasource.password=xxxxxx


### 编译

tesla-quota-server下

mvn clean -U compile package -P yourprofile -Dmaven.test.skip=true

