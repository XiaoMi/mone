## tesla-agent迁移部署

### 配置
新建对应业务的properties，在根目录pom的profile模块添加对应的profile

基于已有的配置
复制c3.properties内容到
按照需求修改以下配置

+ 项目相关
    - javaBinPath
    - 文件服务 fileServerUrl useFileServer
    - 项目部署明细缓存路径 cache_path

+ nacos相关
    - nacosAddr
    - nacos_config_server_addr
    - nacos_addrs

+ rocketmq
    - namesrvAddr
    - topic

### 编译

tesla-agent下

mvn clean -U compile package -P yourprofile -Dmaven.test.skip=true

