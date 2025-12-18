# MCP SmartSRE

基于 Hive 框架的智能运维 Agent，集成了知识库问答、操作指引、K8s 诊断等多种能力。

## 项目简介

MCP SmartSRE 是一个智能运维 Agent，通过集成多种工具和服务，提供以下核心能力：

- **知识库问答**：基于 WeKnora 知识库，回答功能咨询、使用方法、配置说明等问题
- **操作指引**：获取 Lark 文档中的标准化排查流程和操作步骤
- **K8s 诊断**：查询 Pod 状态、查看日志、进入容器执行诊断命令
- **服务查询**：通过 dayu_server MCP 查询 Nacos 实例和 Dubbo3 元数据
- **链路追踪**：通过 hera MCP 进行调用链路分析和错误定位

## 技术架构

### 核心框架
- **Hive 框架**：基于 run.mone:hive 和 hive-spring-starter
- **Spring Boot**：版本 2.7.14
- **JDK**：21

### Skills 集成
项目集成了三个 Claude Skills：

1. **knowledge skill**
   - 搜索 WeKnora 知识库
   - 支持标签过滤和混合搜索
   - 脚本：`get_tags.py`、`search.py`

2. **lark-doc skill**
   - 获取 Lark/Feishu 文档内容
   - 归档故障排查记录
   - 脚本：`fetch_doc.py`、`append_summary.py`

3. **k8s-troubleshoot skill**
   - 搜索 Kubernetes Pods
   - 在容器内执行诊断命令
   - 脚本：`search_pods.py`、`exec_pod.py`

## 项目结构

```
mcp-smartsre/
├── .claude/
│   ├── skills/
│   │   ├── knowledge/          # 知识库搜索 skill
│   │   │   ├── SKILL.md
│   │   │   └── scripts/
│   │   │       ├── get_tags.py
│   │   │       └── search.py
│   │   ├── lark-doc/           # Lark 文档 skill
│   │   │   ├── SKILL.md
│   │   │   ├── fetch_doc.py
│   │   │   └── append_summary.py
│   │   └── k8s-troubleshoot/   # K8s 诊断 skill
│   │       ├── SKILL.md
│   │       └── scripts/
│   │           ├── search_pods.py
│   │           └── exec_pod.py
│   └── settings.local.json
├── src/
│   └── main/
│       ├── java/
│       │   └── run/mone/mcp/smartsre/
│       │       ├── SmartSreMcpBootstrap.java    # 启动类
│       │       └── config/
│       │           └── AgentConfig.java         # Agent 配置
│       └── resources/
│           └── application.properties           # 应用配置
├── pom.xml
└── README.md
```

## 配置说明

### 环境变量配置

在运行前需要配置以下环境变量：

#### Hive Manager 配置
```properties
mcp.agent.name=smartsre-agent
hive.manager.base-url=<Hive Manager 地址>
hive_manager_token=<Hive Manager Token>
```

#### Skills 相关环境变量

**Knowledge Skill (WeKnora):**
```bash
WEKNORA_BASE_URL=http://your-weknora-server:8080
WEKNORA_API_KEY=your-api-key
```

**Lark-doc Skill:**
```bash
LARK_APP_ID=your-app-id
LARK_APP_SECRET=your-app-secret
LARK_DOC_URL=https://your-lark-domain/docx/xxx
LARK_DOMAIN=https://your-lark-domain  # 可选，企业飞书域名
```

**K8s-troubleshoot Skill:**
```bash
# 方式1：使用 Service Account Token
K8S_API_SERVER=https://your-k8s-api-server:6443
K8S_SA_TOKEN=your-service-account-token
K8S_SKIP_TLS_VERIFY=true  # 可选，跳过 TLS 验证

# 方式2：使用 kubeconfig 文件
KUBECONFIG=/path/to/kubeconfig

# 方式3：在 K8s Pod 内运行时会自动使用 in-cluster 配置
```

## 构建和运行

### 构建项目
```bash
cd mcp-smartsre
mvn clean package
```

### 运行项目
```bash
java -jar target/app.jar
```

或者使用 Spring Boot Maven 插件：
```bash
mvn spring-boot:run
```

## Agent 工作流程

### 1. 使用咨询类问题
- 获取知识库标签列表
- 根据问题选择相关标签
- 执行搜索并返回结果

### 2. 故障排查类问题
- 获取 Lark 文档中的操作指引
- 如有 traceId，调用链路追踪分析
- 根据错误类型决定是否进容器诊断
- 在容器内查看日志，定位问题
- 归档新的故障案例

### 3. 混合问题
- 综合使用知识库查询和故障诊断流程
- 确保关键步骤都得到执行

## 从 Python 版本迁移

本项目是从 `customer-agent-claude-agent` (Python 版本) 迁移而来：

### 主要变化

1. **框架迁移**
   - 从 Claude Agent SDK (Python) → Hive 框架 (Java)
   - 保持了相同的 Agent 能力和工作流程

2. **Skills 保留**
   - 完整保留了三个 Python Skills
   - Skills 通过 Hive 的 Skill 工具调用

3. **提示词迁移**
   - 将 Python 版本的 system_prompt 迁移到 AgentConfig
   - 转换为 Hive 的 RoleMeta 配置格式
   - 包含：profile、goal、constraints、workflow

4. **配置方式**
   - Python: .env 文件 + ClaudeAgentOptions
   - Java: application.properties + AgentConfig Bean

## 依赖说明

### Maven 依赖
- `run.mone:hive` - Hive 核心框架
- `run.mone:hive-spring-starter` - Hive Spring Boot Starter
- `okhttp3:okhttp` - HTTP 客户端

### Skills 依赖
Skills 中的 Python 脚本需要以下依赖（通过 `uv` 管理）：
- `lark-oapi` - Lark/Feishu SDK
- `kubernetes` - Kubernetes Python 客户端
- `httpx` - HTTP 客户端
- `python-dotenv` - 环境变量加载

## 注意事项

1. **安全性**
   - 生产环境中请妥善保管 Token 和密钥
   - K8s 操作需要合适的 RBAC 权限
   - 建议为不同环境配置不同的 Service Account

2. **Skills 执行**
   - Skills 中的 Python 脚本通过 Skill 工具执行
   - 需要确保环境中已安装 Python 3.11+ 和 uv
   - Skills 的依赖管理独立于 Java 项目

3. **归档规范**
   - 归档内容必须通用化
   - 不包含具体的接口名、Pod 名、配置值
   - 重复问题无需归档

## 联系方式

如有问题或建议，请联系项目维护者。

## 许可证

请遵循 MiOne 项目的许可证规定。
