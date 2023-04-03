# MoneOperator概述
MoneOperator用于一键部署和更新整个Mone系统。
部署完成后，请进入k8s dashboard，查看NodePort类型的Service，进行页面访问：
+ "mifaas-dash" service：mifaas产品访问: http://nodePort:32198/
+ "nacos-mone-b2c-srv" service：nacos产品访问: http://nodePort:32188/
+ "mi-tpc" service：tpc产品访问: http://nodePort:32197/

# 使用说明
+ 作用初始化mione
+ 这个crd的实质就是创建若干个deployment和service
+ 1. 先要创建自定义资源(如果测试中有问题,可以先删除自定义资源,再创建)
  + kubectl apply -f mone_bootstrap_crd.yaml
  + 删除: kubectl delete -f mone_bootstrap_crd.yaml
+ 2. 部署MoneOperator
  + kubectl apply -f mone_operator_auth.yaml
  + kubectl apply -f mone_operator_deployment.yaml

+ 3. 创建cr，触发operator
  + apply -f mone_bootstrap_cr.yaml
  + operator内部初始化Mone流程
    + 初始化中间件deployment
      + redis、mysql、nacos等
      + db schema初始化
    + 初始化中间件service
      + redis、mysql、nacos等
    + 初始化Mone系统deployment
      + mi-tpc、mifaas-dash、mifaas-fe等
    + 初始化Mone系统Service
      + mifaas-dash、mifaas-fe等

# Mone体系应用打包说明
+ miline-all
  + miline
    + 分支：mone-operator
    + profile：mone-operator
  + miline-fe
    + 分支：mone-operator
+ mifaas-all
  + mifaas-dash
    + 分支：staging-open
    + profile：mone-operator
  + mifaas-dash-fe
    + 分支：staging-open
    + profile：mone-operator
+ mitpc-all
  + mi-tpc
    + 分支：mone-operator
    + profile：mone-operator
  + mi-tpc-fe(mi-permission)
    + 分支：mone-k8s-operator
  + mi-tpc-login(mi-user-manage)
    + 分支：staging
+ fileserver-all
  + fileserver
    + 分支：mone-operator
    + profile：staging





# 附录
+ 一些k8s操作(方便测试)
  + 初始化自定义资源
  + kubectl apply -f mone_bootstrap_crd.yaml
  + 查看创建的自定义资源
  + kubectl get monebootstraps
  + 删除某个指定的自定义资源
  + kubectl delete monebootstraps example-podset
  + 删除某个deployment
  + kubectl delete deployment nginx-deployment
  + 查看所有deployment
  + kubectl get deployments
+ 在这个测试用例中其实就是安装2个deployment和两个service
+ 安装后测试:curl http://localhost:31080/
+ 拉取mysql 5.7 镜像
+ docker pull mysql:5.7  (arm64 m1: docker pull mariadb:latest)
+ 在node中执行指令
+ kubectl exec mysql-deployment-7b65855b66-9qhp7 - mysql -uroot -pMone!123456 -e "select 1"
+ sidecar中执行命令
+ kubectl exec -it redis-deployment-74667fdc89-727kp -c toolbox