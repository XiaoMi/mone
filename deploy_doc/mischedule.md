## mischedule迁移部署

### 配置
新建对应业务的properties，在根目录pom的profile模块添加对应的profile

基于已有的配置
复制c3.properties内容到
按照需求修改以下配置

+ dubbo+nacos相关
	- dubbo.registry.address=
	- dubbo.group=
	- nacos.config.addrs=
	- app.nacos=

+ database
	- spring.datasource.url=
	- spring.datasource.username=
	- 数据库已经迁移到nacos配置中 dataId:mischedule 内容 mischedule_db_key=your_pw
+ mq
 - rocketmq.namesrv.addr=
+ 集群模式
	- 若为单机模式 schedule.server.type=standalone
+ 文件服务
 - file.server.url=



### 编译

mischedule-server下

maven clean -U compile package -P yourprofile -Dmaven.test.skip=true

### 部署

登录到指定机器上
cd xxxx

scp yourname@yourip:XXX/mischedule-server/target/mischedule-server-1.0-SNAPSHOT.jar xxxx

nohup java -jar mischedule-server-1.0-SNAPSHOT.jar &

