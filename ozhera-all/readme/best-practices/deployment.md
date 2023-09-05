# Best practices during deployment

## Memory adjustment for small clusters
- Some components of the hera system use relatively high default JVM memory because hera was designed for a high QPS business environment (millions+).
- However, for small-scale k8s clusters (such as three 8c16g working nodes), excessive memory usage can cause the initialization of the hera system to fail; or Pod eviction can occur, causing service instability.
- Below are the adjustments for some of the components with the highest memory usage in the default configuration.-
### rocket-mq
- Adjust BROKER_MEM in rocketmq-broker-config, for example:
```yaml
     BROKER_MEM: ' -Xms512m -Xmx512m -Xmn200m '
```
- Simply adjusting the above configuration is not sufficient, you also need to set the environment variable JAVA_OPT_EXT for rocketmq-broker-0-master, for example:
```yaml
   - name: JAVA_OPT_EXT
     value: "-server -Xms512m -Xmx512m -Xmn200m"
```

### trace-etl-es
- Currently, it can only be modified by redefining the entry point, for example:
```yaml
   containers:
     - name: trace-etl-es-container
       command: ["java","-Xms512M","-Xmx512M","-Xss512k","-XX:MetaspaceSize=128m","-XX:MaxMetaspaceSize=256m","-XX:MaxDirectMemorySize=512M"," -XX:+PrintReferenceGC","-XX:+PrintGCDetails","-XX:+PrintGCDateStamps","-XX:+PrintHeapAtGC","-Xloggc:/home/work/log/gc.log","-Duser .timezone=Asia/Shanghai","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/home/rocksdb/dum/oom.dump","-jar","/home/work/trace-etl-es /trace-etl-es-1.0.0-SNAPSHOT.jar","&&","tail","-f","/dev/null"]
```

### nacos
- Similarly, it can only be temporarily modified by redefining the entry point, as follows:
```yaml
   containers:
     - name: nacos-container
       command: ["java", "-Xms512m", "-Xmx512m", "-XX:MetaspaceSize=512M", "-XX:+UseG1GC", "-XX:+PrintReferenceGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-XX:+PrintHeapAtGC", "-verbose:gc", "-Xloggc:/home/work/log/nacos/gc.log", "-jar", "-Dnacos. standalone=true", "/home/work/nacos/nacos-server.jar", "--server.port=80"]
```

## Steps to redeploy
- After the initialization of the hera system is completed, if the system status does not meet expectations and the yml parameters need to be adjusted, it is recommended to redeploy.
- Improper redeployment can cause system abnormalities, and manual correction will be complicated, so be sure to follow these steps:
1. Determine the access method of the hera-op-nginx service. You can use kubectl to query:
```yaml
kubectl get svc hera-op-nginx -n hera-namespace
NAME TYPE CLUSTER-IP EXTERNAL-IP PORT(S) AGE
hera-op-nginx NodePort 172.17.164.114 <none> 7001:30999/TCP 6d2h
```
2. Delete deployed resources through http request:
```yaml
http://any worker node ip:30999/hera/operator/cr/delete
```
3. Delete the namespace
```yaml
kubectl delete ns hera-namespace
```
4. Re-deploy the hera system, starting from deploying the operator, which will not be described in details.

## redeploy es
- The default es deployment does not bind PV, and the index template created by the operator during the initialization phase will be lost after restarting.
- First of all, it is strongly recommended to bind pv to es. You can refer to the default configuration of mysql.
- The index template can be recreated by executing the command below.
```yaml
sh indexTemplate.sh
```
- indexTemplate.sh is in the current directory and has two execution options:
1. The default service type of es is clusterIp, which needs to be executed in a pod under the same namespace.
2. You can also expose the NodePort service to es and execute it outside the cluster. Modify elasticsearch:9200 in the script accordingly.

## How to access the hostnetwork mode application
If you do not have to specify a hostNetwork for the Pod, such an application can also be connected to hera, but you need to pay attention to the following points:
1. First of all, compared with ordinary Pods, after specifying hostNetwork as true, you cannot access the svc in the cluster with clusterIp.
2. Need to expose NodePort/LoadBalance type services for nacos.
3. It is necessary to modify the address of nacos in the startup parameters of the probe, such as
```yaml
-Dotel.exporter.prometheus.nacos.addr=Node ip:NodePort port
```
4. It is necessary to modify the address of the log-agent to connect to nacos, which can be achieved by specifying env, such as
```yaml
   env:
     - name: nacosAddr
       value: node ip:NodePort port
```
5. Need to expose NodePort/LoadBalance type services for rocketmq-name-server.
6. You need to edit the rocketmq resource under hera system->log service->resource management, and change the mq address to the accessible address modified in step 5.
