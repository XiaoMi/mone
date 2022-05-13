# K8s操作库

提供了使用Java代码操作K8S集群的方法。

1. build images

```shell
cd src/test/resources/k8s-demo/docker

docker build -t k8s-demo:0.1 .
docker tag k8s-demo:0.1 riskers/k8s-demo:0.1
```

2. deploy k8s

执行 test1: com.xiaomi.youpin.k8s.K8sTest.test1

等同于:

```shell
cd src/test/resources/k8s-demo/k8s
kubectl create -f deployment.yaml
```

3. 验证命令

执行 test2: com.xiaomi.youpin.k8s.K8sTest.test2

等同于:

```shell
kubectl scale -n default deployment k8s-demo-deployment --replicas=3
```

验证是否成功:

```shell
kubectl get deployments
```
