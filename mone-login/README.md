概述：
该项目(mi-tpclogin)给等众多开源项目提供登陆相关能力。赋予接入系统账号密码登陆、三方授权登陆，outh2授权登陆等能力。同时提供统一用户获取，参数校验，调用认证等基础工具能力。
后端：
环境:
jdk1.8,mysql,redis(可选),nacos(注册中心),TPC(依赖服务)
项目配置：
    - 找到mi-tpclogin/mi-tpclogin-dao/src/main/resource/db.sql文件,初始化mysql数据库。
    - 找到mi-tpclogin/mi-tpclogin-server/src/main/resources/config/staging.properties文件，修改配置(配置数据库,缓存等配置)
构建&启动项目：
    - 在mi-tpclogin目录下执行 mvn -U clean install 命令
    - 进入mi-tpclogin/mi-tpclogin-server/target目录
    - 执行java -jar mi-tpclogin-server-1.0.0-SNAPSHOT.jar
前端：
第一步：项目根目录下执行 npm i ,npm run build ,生成dist文件
第二步：将编译好的dist文件放到服务器上。
第三步：进行nginx配置，如下：
server {
  listen 80;
  server_name xxx.xxx.com;

  location ^~ /api-pomission/ {
      proxy_pass http://server-ip:port/;
  }

  # page页
  location / {
      root  /XXX/dist/;
      index  index.html index.htm;
      try_files $uri $uri/ /index.html;
  }
}
