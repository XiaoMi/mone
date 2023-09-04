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

## 重新部署es
- 默认的es部署没有绑定pv，重启之后初始化阶段由operator创建的索引模板会丢失。
- 首先强烈建议给es绑定pv，可以参考mysql的默认配置。
- 可以通过执行下面的命令来重新创建索引模板。
```yaml
sh indexTemplate.sh
```
- indexTemplate.sh在当前目录下，执行方式有两种选项：
1. 默认es的服务类型是clusterIp，需要在同名字空间下的某个pod内执行。
2. 也可以给es暴露NodePort服务，在集群外执行，把脚本中的elasticsearch:9200做相应修改。

## 如何接入hostnetwork模式的应用
如果不得以必须为Pod指定hostNetwork，这样的应用也是可以接入hera的，但是需要注意下面几点：
1. 首先与普通的Pod相比，指定了hostNetwork为true之后就不能以clusterIp访问集群内的svc了。
2. 需要为nacos暴露NodePort/LoadBalance类型的服务。
3. 需要修改探针的启动参数中nacos的地址，比如
```yaml
-Dotel.exporter.prometheus.nacos.addr=节点ip:NodePort端口
```
4. 需要修改log-agent连接nacos的地址，这可以通过指定env实现，比如
```yaml
  env:
    - name: nacosAddr
      value: 节点ip:NodePort端口
```
5. 需要为rocketmq-name-server暴露NodePort/LoadBalance类型的服务。
6. 需要在hera系统->日志服务->资源管理下编辑rocketmq资源，将mq地址修改为第5步中修改成的可访问地址。
