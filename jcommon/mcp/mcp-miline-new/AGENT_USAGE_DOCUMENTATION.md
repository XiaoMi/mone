# Miline Agent 使用文档

## 概述

这是一个基于 Hive MCP 框架的 Miline Agent 项目，专门用于在 Miline 平台上进行项目创建、代码生成、流水线管理和自动化部署。

## Agent 基本信息

- **Agent名称**: miline-new
- **角色定位**: 优秀的miline助手
- **目标**: 更好地帮助用户
- **约束**: 专注于提供帮助

## 可用工具列表

### 1. 项目创建工具 (create_project)

**功能描述**: 创建Miline项目的工具，用于在Miline平台创建新项目。

**使用场景**:
- 需要创建新的Miline项目
- 初始化项目配置和基本信息
- 设置项目的生成参数和域名

**参数**:
- `projectName`: (必填) 项目名称，将同时作为name和gitName
- `env`: (可选) 环境类型，默认为staging
- `baseUserName`: (可选) 用户名，默认为liguanchen

**使用示例**:
```xml
<create_project>
<projectName>我的新项目</projectName>
<env>staging</env>
<domain>git.n.xiaomi.com</domain>
</create_project>
```

### 2. Git代码生成工具 (generate_git_code)

**功能描述**: Git代码生成工具，为指定项目生成初始化的Git仓库代码。

**使用场景**:
- 为新创建的项目生成初始代码结构
- 初始化项目的Git仓库
- 自动创建项目模板代码
- 创建完成后自动提交代码

**参数**:
- `projectId`: (必填) 项目ID
- `env`: (必填) 环境

**使用示例**:
```xml
<generate_git_code>
<projectId>12345</projectId>
<env>staging</env>
</generate_git_code>
```

### 3. 流水线创建工具 (create_pipeline)

**功能描述**: 创建Miline流水线的工具，为指定项目创建包含编译和部署阶段的完整CI/CD流水线。

**使用场景**:
- 为新项目创建标准的CI/CD流水线
- 配置Maven项目的编译和K8s部署流程
- 自动化构建和部署配置

**参数**:
- `projectId`: (必填) 项目ID
- `pipelineName`: (必填) 流水线名称
- `gitUrl`: (必填) Git仓库地址
- `gitName`: (必填) 项目名称，默认为git项目名
- `gitBranch`: (选填) Git分支，默认为master
- `env`: (选填) 环境，默认为staging
- `pipelineCname`: (选填) 流水线中文名称
- `desc`: (选填) 流水线描述

**使用示例**:
```xml
<create_pipeline>
<projectId>600941</projectId>
<pipelineName>流水线-测试</pipelineName>
<gitUrl>https://git.n.xiaomi.com/cefe/lgc.git</gitUrl>
<gitBranch>master</gitBranch>
<env>staging</env>
</create_pipeline>
```

### 4. 流水线运行工具 (run_pipeline)

**功能描述**: 运行Miline流水线的工具，触发指定项目下指定流水线以最新提交执行。

**使用场景**:
- 需要在CI/CD中触发某个流水线
- 验证最近一次提交是否能通过流水线
- 集成到自动化流程中进行构建/部署

**参数**:
- `projectId`: (必填) 项目ID
- `pipelineId`: (必填) 流水线ID

**使用示例**:
```xml
<run_pipeline>
<projectId>12345</projectId>
<pipelineId>67890</pipelineId>
</run_pipeline>
```

### 5. 其他辅助工具

- `chat`: 通用对话工具
- `ask_followup_question`: 询问后续问题工具
- `attempt_completion`: 任务完成确认工具
- `list_files`: 文件列表查看工具
- `execute_command`: 命令执行工具
- `read_file`: 文件读取工具
- `search_files`: 文件搜索工具
- `replace_in_file`: 文件内容替换工具
- `list_code_definition_names`: 代码定义名称列表工具
- `write_to_file`: 文件写入工具

## 工作流程

Agent 遵循以下标准工作流程：

1. **根据projectName生成项目**
2. **根据提供的projectId、env生成代码**
3. **拉取代码到本地**
4. **修改service的pom文件**，不要排除spring-boot-starter-tomcat这个包
5. **修改application.properties文件**中的占位符为config中的xxx.properties中的值
6. **根据需求进行代码修改**（如果提供了要实现的需求则进行代码实现，否则跳过代码实现并检查下没有语法bug后，再进行后续提交操作）
7. **提交代码** - 添加完代码后，一定要将本地代码使用git_commit工具进行git commit，commit信息如果是修复代码提交信息为："自动代码修复"否则根据commit提交范式进行补充，使用git_push进行git push
8. **创建流水线** - 根据projectId、pipelineName、gitUrl、gitName创建流水线
9. **触发流水线** - 根据projectId、pipelineId触发流水线进行发布

## 环境配置

### 必需环境变量
```bash
export req_base_url=your_base_url_here
export hive_manager_token=your_token_here
export hive_manager_base_url=your_base_url_here
```

### 应用配置
主要配置文件位于 `src/main/resources/application.properties`：
- `mcp.agent.name`: Agent名称
- `mcp.agent.group`: Agent分组（dev/staging/production）
- `mcp.grpc.port`: gRPC服务端口
- `hive.manager.base-url`: Hive Manager的基础URL
- `mcp.llm`: 使用的LLM模型

## 项目结构

```
mcp-miline-new/
├── src/main/java/run/mone/mcp/milinenew/
│   ├── MilineNewMcpBootstrap.java      # 启动类
│   ├── config/
│   │   └── AgentConfig.java            # Agent配置
│   └── tools/
│       ├── CreateProjectTool.java      # 项目创建工具
│       ├── GenerateGitCodeTool.java    # Git代码生成工具
│       ├── CreatePipelineTool.java     # 流水线创建工具
│       └── RunPipelineTool.java        # 流水线运行工具
├── src/main/resources/
│   └── application.properties          # 应用配置
└── pom.xml
```

## 使用示例

### 完整项目创建流程示例

```xml
<!-- 1. 创建项目 -->
<create_project>
<projectName>demo-service</projectName>
<env>staging</env>
</create_project>

<!-- 2. 生成代码 -->
<generate_git_code>
<projectId>12345</projectId>
<env>staging</env>
</generate_git_code>

<!-- 3. 创建流水线 -->
<create_pipeline>
<projectId>12345</projectId>
<pipelineName>demo-service-pipeline</pipelineName>
<gitUrl>https://git.n.xiaomi.com/cefe/demo-service.git</gitUrl>
<gitName>demo-service</gitName>
<env>staging</env>
</create_pipeline>

<!-- 4. 运行流水线 -->
<run_pipeline>
<projectId>12345</projectId>
<pipelineId>67890</pipelineId>
</run_pipeline>
```

## 注意事项

1. **环境变量配置**: 使用前必须配置 `req_base_url` 环境变量
2. **参数验证**: 所有必填参数必须提供，否则会返回错误信息
3. **错误处理**: 工具会返回详细的错误信息，便于排查问题
4. **日志记录**: 所有操作都会记录日志，便于跟踪和调试
5. **超时设置**: 网络请求设置了合理的超时时间，避免长时间等待

## 故障排除

### 常见问题

1. **环境变量未设置**
   - 错误信息: "配置错误: req_base_url 环境变量未设置"
   - 解决方案: 设置 `req_base_url` 环境变量

2. **缺少必填参数**
   - 错误信息: "缺少必填参数'xxx'"
   - 解决方案: 提供所有必填参数

3. **参数格式错误**
   - 错误信息: "'projectId'必须是数字"
   - 解决方案: 确保参数格式正确

4. **网络连接问题**
   - 错误信息: "执行操作失败: ..."
   - 解决方案: 检查网络连接和API服务状态

## 技术支持

如有问题，请检查：
1. 环境变量配置是否正确
2. 网络连接是否正常
3. API服务是否可用
4. 查看详细日志获取更多信息

日志文件默认保存在：`~/mcp/miline-new.log`