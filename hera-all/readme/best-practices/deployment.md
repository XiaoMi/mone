# 部署过程中的最佳实践

## 针对于小集群的内存调整
- 在hera系统的一些组件，默认的jvm内存使用是比较高的，因为hera脱胎于高QPS的业务环境（百万+）。
- 但是对于小规模的k8s集群（比如3个8c16g的工作节点），过高的内存占用会引起hera系统的初始化失败；或出现Pod驱逐，引起服务不稳定。
- 下面是针对几个默认配置下内存占用最高的组件的调整方式。

### rocket-mq
- 调整rocketmq-broker-config中的BROKER_MEM，比如： 
```yaml
    BROKER_MEM: ' -Xms512m -Xmx512m -Xmn200m '
```
- 只调整上面的配置是不够的，还需要给rocketmq-broker-0-master设置环境变量JAVA_OPT_EXT，比如：
```yaml
  - name: JAVA_OPT_EXT
    value: "-server -Xms512m -Xmx512m -Xmn200m"
```

### trace-etl-es
- 暂时只能通过重新定义入口修改，比如：
```yaml
  containers:
    - name: trace-etl-es-container
      command: ["java","-Xms512M","-Xmx512M","-Xss512k","-XX:MetaspaceSize=128m","-XX:MaxMetaspaceSize=256m","-XX:MaxDirectMemorySize=512M","-XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/gc.log","-Duser.timezone=Asia/Shanghai","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/home/rocksdb/dum/oom.dump","-jar","/home/work/trace-etl-es/trace-etl-es-1.0.0-SNAPSHOT.jar","&&","tail","-f","/dev/null"]
```

### nacos
- 同样，暂时只能通过重新定义入口修改，比如：
```yaml
  containers:
    - name: nacos-container
      command: ["java", "-Xms512m", "-Xmx512m", "-XX:MetaspaceSize=512M", "-XX:+UseG1GC", "-XX:+PrintReferenceGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-XX:+PrintHeapAtGC", "-verbose:gc", "-Xloggc:/home/work/log/nacos/gc.log", "-jar", "-Dnacos.standalone=true", "/home/work/nacos/nacos-server.jar", "--server.port=80"]
```

## 重新部署的步骤
- hera系统一次初始化完成之后，如果系统状态不符合预期，需要调整yml参数的情况下，建议重新部署。
- 重新部署如果操作不当，会造成系统状态异常，手动修正会很复杂，所以一定要按步骤操作：
1. 确定hera-op-nginx服务的访问方式，可以用kubectl查询：
```yaml
kubectl get svc hera-op-nginx -n hera-namespace
NAME            TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
hera-op-nginx   NodePort   172.17.164.114   <none>        7001:30999/TCP   6d2h
```
2. 通过http请求，删除已经部署的资源：
```yaml
http://任意worker节点ip:30999/hera/operator/cr/delete
```
3. 删除名字空间
```yaml
kubectl delete ns hera-namespace
```
4. 重新进行hera系统的部署，从部署operator开始，不再赘述。

