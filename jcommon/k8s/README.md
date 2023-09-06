# K8s operation library

# K8s operation library k8s operator lib

# Currently, it is recommended to use docean (which includes a k8s plugin that has already been packaged). It is recommended to use docean-plugin -> docean-plugin-k8s.

# It is currently recommended to use Docean (Docean has a built-in k8s plugin, which is already well-packaged), recommended to use (docean-plugin -> docean-plugin-k8s)

Provides methods for operating K8S clusters using Java code.

1. build images

```shell
cd src/test/resources/k8s-demo/docker

docker build -t k8s-demo:0.1 .
docker tag k8s-demo:0.1 riskers/k8s-demo:0.1
```

2. deploy k8s

Execute test1: com.xiaomi.youpin.k8s.K8sTest.test1

Equivalent to:

```shell
cd src/test/resources/k8s-demo/k8s
kubectl create -f deployment.yaml
```

3. verify command

Execute test2: com.xiaomi.youpin.k8s.K8sTest.test2

Equivalent to:

```shell
kubectl scale -n default deployment k8s-demo-deployment --replicas=3
```

Verify if successful:

```shell
kubectl get deployments
```