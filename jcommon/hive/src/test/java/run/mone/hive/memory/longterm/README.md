# 长期记忆模块测试指南

本目录包含了长期记忆模块的各种测试用例，帮助您验证功能是否正常工作。

## 📋 测试文件说明

- 可以使用[src/main/resources/memory-config.yml](../../../main/resources/memory-config.yml)来一键配置所需组件
- 本地运行Chroma: `docker run -d --name chroma-test -p 8000:8000 chromadb/chroma:0.6.4.dev226`
- 本地运行Neo4j:
```shell
docker run -d \           
  --name my-neo4j \
  -p 7474:7474 -p 7687:7687 \
  --env NEO4J_AUTH=neo4j/password \
  --env NEO4J_PLUGINS='["apoc"]' \
  neo4j:5.15.0
```
    - 或者使用kuzu默认随进程启动(InMemory模式，不会持久化到文件)
- 运行[LocalMemoryIntegrationTest.java](LocalMemoryIntegrationTest.java) 验证向量和图存储功能