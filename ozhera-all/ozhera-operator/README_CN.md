# OzHeraOperator概述
OzHeraOperator用于一键部署整个OzHera系统。

# 环境要求
+ k8s集群
+ 最好开启了LB、如果本地测试环境默认会走NodePort方式
+ 有k8s创建ns、crd、service、deployment、pv权限


# 操作步骤
请切到工程 ozhera-operator/ozhera-operator-server/src/main/resources/operator目录
## 创建账号及namespace
kubectl apply -f ozhera_operator_auth.yaml

## 创建ozhera CRD
kubectl apply -f ozhera_operator_crd.yaml

## 部署operator
kubectl apply -f ozhera_operator_deployment.yaml
请注意：operator镜像，如果需要基于源码的自行构建镜像，请修改yaml中的镜像地址

## operator界面操作，完成ozhera部署
请找到ozhera的访问地址，具体service名字在ozhera_operator_deployment.yaml中
kubectl get service -n=ozhera-namespace

找到后在浏览器访问operator界面地址，按界面操作进行部署

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