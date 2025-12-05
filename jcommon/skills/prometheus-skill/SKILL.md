---
name: prometheus-skill
description: 帮助我根据prometheus指标查询服务问题
---

# 基础规则

当用户询问类似 "我的id:xxx name:gis 服务现在什么问题？"、"xxx_gis服务有啥问题没" 或 "帮我看下projectId=xxx, projectName=gis 的服务现状" 等想查询服务监控指标的问题时，请按照以下 7 个步骤进行排查，并将结果汇总分析服务的当前状态(注意替换application)。

1. **查询当前 CPU 使用量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(rate(container_cpu_user_seconds_total{image!="",application="xxx_gis"}[30s])) by(application) * 100'
   ```
   > **结果举例**："查询到的最新值: 32.3487092130255"
   > 代表查询使用了 32% 左右的 CPU（最大不是 100%，而是申请核心数的百分比，如申请 3 核心则最大是 300%）。

2. **查询当前 CPU 申请量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum((container_spec_cpu_quota{application="xxx_gis", image!=""}) /1000) by (application)'
   ```
   > **结果举例**："查询到的最新值: 6181"
   > 代表这个服务总共申请了 6181% 的 CPU。

3. **查询当前 Memory 使用量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(container_memory_rss{image!="",application="xxx_gis"}) by (application)'
   ```
   > **结果举例**："查询到的最新值: 21611335680.0" (单位是 byte)

4. **查询当前 Memory 申请量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(container_spec_memory_limit_bytes{image!="",application="xxx_gis"}) by (application)'
   ```
   > **结果举例**："查询到的最新值: 75161927680.0" (单位是 byte)

5. **查询当前 Load**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(container_cpu_load_average_10s{application="xxx_gis"}) by (application)'
   ```
   > **结果举例**："查询到的最新值: 1242.0" ,注意这个值应该和cpu申请量对比看，理论上小于申请量的80%为最佳，比如cpu申请量是2000，则1242并不高

6. **查询当前 HeapUsed 使用量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(jvm_memory_used_bytes{application="xxx_gis"}) by (application)'
   ```
   > **结果举例**："查询到的最新值: 15085025864.0" (单位 byte)

7. **查询当前 HeapMax 最大量**
   构造 promql 语句并运行 `prometheus.py`，例如：
   ```bash
   uv run prometheus.py 'sum(jvm_memory_max_bytes{application="xxx_gis"}) by (application)'
   ```
   > **结果举例**："查询到的最新值: 100503912448.0" (单位 byte)
