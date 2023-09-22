# Overview
# How to Deploy
## Deployment Dependencies
(1) MySQL, table creation statements are the same as trace-etl-manager

(2) Redis

(3) Nacos

(4) ES

(5) RocketMQ

### Containerization
`k8s Stateful Set`: trace-etl-es currently needs to be deployed in k8s, and the type is Stateful Set.

### File Disk Mount Directory
`/home/rocksdb`: This directory contains the span data cached by rocksdb. It requires persistent storage and won't be deleted even if the container restarts. It's necessary to set the trace-etl-es deployment service as Stateful Set and then create a pvc to mount this directory.

### Environment Variables
`CONTAINER_S_POD_NAME`: This is the podName of k8s Stateful Set. The pods of the k8s Stateful Set type will automatically append an incrementing number starting from 0 to the podName, for example,

`trace-etl-es-podname-0`

`trace-etl-es-podname-1`

`trace-etl-es-podname-2...`

## Building with Maven
Execute in the project root directory (trace-etl):

`mvn clean install -U -P opensource -DskipTests`

This will generate a target directory under the trace-etl-es module. The trace-etl-es-1.0.0-SNAPSHOT.jar in the target directory is the executable jar file.

## Running
Execute:

`java -jar trace-etl-es-1.0.0-SNAPSHOT.jar`

to run trace-etl-es.
