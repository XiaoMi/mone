# OzHera说明文档

## OzHera是什么？
OzHera是由小米-中国区研发效能团队开源的一款应用性能观测平台(APM)。以应用为核心，集指标监控、链路追踪、日志、报警于一身，并实现了metrics->tracing->logging的串联和联动，OzHera还提供应用健康状态列表、应用指标看板、接口大盘、应用大盘、网关大盘等内容丰富的监测看板，以及简洁明了的可视化明文报警，让用户准确、高效定位故障。

---

## Architecture
![ozhera](readme/images/architecture.png)

---

## Features
- 准：基于业务错误码提取可用性指标
- 快：metrics-tracing-logging联动
- 经济：<5%存储成本，满足99.9%的tracing诉求
- 拥抱云原生：遵循Opentracing标准、深度适配K8S、集成集成Opentelemetry、Grafana、Prometheus、ES等多个开源明星产品
- 企业级可观测产品

---

## Getting started
### 部署
[operator使用文档.md](readme%2Fdeploy%2Fozhera-deploy-document_cn.md)

---

### 应用接入
[应用接入文档.md](readme/application-integeration/application-integration-document_cn.md)

---
