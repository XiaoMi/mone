# SkillRequestTool 实现总结

## 概述

成功实现了 `SkillRequestTool` 工具，允许 AI Agent 通过 XML 格式请求获取 skill 定义的完整内容，并将其加载到上下文中继续使用。

## 实现的组件

### 1. SkillRequestTool 工具类
**文件**: `src/main/java/run/mone/hive/roles/tool/SkillRequestTool.java`

参考 `ReadFileTool` 的实现模式，创建了 `SkillRequestTool`：

#### 核心方法

```java
@Override
public JsonObject execute(ReactorRole role, JsonObject inputJson) {
    // 1. 验证参数
    String skillName = inputJson.get("skill_name").getAsString();

    // 2. 加载所有 skills
    String hiveCwd = MonerSystemPrompt.hiveCwd(role);
    List<SkillDocument> skills = skillService.loadSkills(hiveCwd);

    // 3. 查找指定的 skill
    SkillDocument skill = skillService.getSkillByName(skills, skillName);

    // 4. 返回 skill content
    result.addProperty("result", skillContent);
    return result;
}
```

#### 关键特性

1. **自动查找**: 从 `.hive/skills/` 目录自动加载所有 skills
2. **错误处理**: 当 skill 不存在时，返回可用 skills 列表
3. **完整信息**: 返回 skill 的 name, description, location, content
4. **上下文集成**: 返回的 content 直接加载到 AI 上下文中

### 2. 自动注册机制
**文件**: `src/main/java/run/mone/hive/prompt/MonerSystemPrompt.java`

修改了 `mcpPrompt` 方法，实现自动注册逻辑：

```java
//注入skill定义
SkillService skillService = new SkillService();
List<SkillDocument> skills = skillService.loadSkills(MonerSystemPrompt.hiveCwd(role));

//如果有skills可用，自动添加 SkillRequestTool 到工具列表
List<ITool> finalTools = new ArrayList<>(tools);
if (!skills.isEmpty()) {
    //检查是否已经有 skill_request 工具
    boolean hasSkillRequestTool = finalTools.stream()
            .anyMatch(t -> SkillRequestTool.name.equals(t.getName()));
    if (!hasSkillRequestTool) {
        finalTools.add(new SkillRequestTool());
        log.debug("Auto-added SkillRequestTool because {} skills are available", skills.size());
    }
}

//注入工具
data.put("toolList", finalTools);
```

**优势**：
- ✅ 无需手动添加工具
- ✅ 只在有 skills 时才添加
- ✅ 避免重复添加
- ✅ 智能化管理

## 工作流程

### 完整调用链路

```
1. AI Agent 看到系统提示中的 SKILLS 部分
   ↓
2. AI 决定使用某个 skill
   ↓
3. AI 发送 XML 请求
   <skill_request>
   <skill_name>code-review</skill_name>
   </skill_request>
   ↓
4. ReactorRole.callTool() 捕获请求
   ↓
5. 调用 SkillRequestTool.execute()
   ↓
6. SkillService 从 .hive/skills/ 加载 skills
   ↓
7. 查找匹配的 skill
   ↓
8. 返回 JsonObject 包含 skill content
   ↓
9. ReactorRole 格式化结果 (JsonUtils.toolResult())
   ↓
10. skill content 加入到 AI 上下文
   ↓
11. AI 根据 skill 定义继续执行任务
```

## XML 请求格式

### 基本格式
```xml
<skill_request>
<skill_name>skill-name-here</skill_name>
</skill_request>
```

### 带任务进度
```xml
<skill_request>
<skill_name>code-review</skill_name>
<task_progress>
- [x] 准备代码
- [ ] 执行审查
- [ ] 生成报告
</task_progress>
</skill_request>
```

## 返回结果

### 成功返回
```json
{
  "result": "# Code Review Skill\n\n## Review Checklist\n...",
  "skillName": "code-review",
  "skillDescription": "Perform comprehensive code review following best practices",
  "skillLocation": "/path/to/.hive/skills/code-review.md",
  "contentLength": 1234
}
```

### 错误返回 - Skill 不存在
```json
{
  "error": "Skill 'unknown-skill' not found.\n\nAvailable skills:\n- code-review: Perform comprehensive code review following best practices\n- api-design: Design RESTful APIs following best practices"
}
```

### 错误返回 - 无 Skills
```json
{
  "error": "No skills are currently available. Skills should be placed in .hive/skills/ directory."
}
```

## 与 ReadFileTool 的对比

| 特性 | ReadFileTool | SkillRequestTool |
|------|--------------|------------------|
| **目的** | 读取文件内容 | 获取 skill 定义 |
| **输入参数** | path (文件路径) | skill_name (skill 名称) |
| **数据源** | 文件系统任意路径 | `.hive/skills/` 目录 |
| **查找方式** | 直接路径访问 | 按名称查找 |
| **错误处理** | 文件不存在错误 | 返回可用 skills 列表 |
| **返回内容** | 文件原始内容 | Markdown content (不含 front matter) |
| **自动注册** | 需手动添加 | 有 skills 时自动添加 |

## 使用示例

### 1. 创建 Skill 文件

`.hive/skills/code-review.md`:
```markdown
---
name: code-review
description: Perform comprehensive code review
---

# Code Review Checklist

## 1. Code Quality
- Check readability
- Verify naming

## 2. Security
- Check for vulnerabilities
...
```

### 2. AI 请求 Skill
```xml
<skill_request>
<skill_name>code-review</skill_name>
</skill_request>
```

### 3. 系统返回
```
Tool Result:
# Code Review Checklist

## 1. Code Quality
- Check readability
- Verify naming

## 2. Security
- Check for vulnerabilities
...
```

### 4. AI 使用 Skill 执行任务
AI 现在有了完整的 code review checklist，可以按照这个清单逐项审查代码。

## 优势

### 1. 标准化
- Skills 提供标准化的任务模板
- 确保任务执行的一致性

### 2. 可重用
- 同一个 skill 可以在多次对话中使用
- 多个 agents 可以共享相同的 skills

### 3. 可维护
- 集中管理 skill 定义
- 更新 skill 文件即可影响所有使用

### 4. 智能化
- AI 自动发现可用 skills
- 根据任务需求选择合适的 skill
- 无需硬编码指令

### 5. 上下文高效
- 只在需要时加载 skill 内容
- 避免在系统提示中包含所有 skill 细节

## 测试验证

### 编译测试
```bash
mvn clean compile -DskipTests
```
✅ **结果**: BUILD SUCCESS

### 功能测试场景

1. **正常流程**:
   - 创建 skill 文件 → AI 请求 skill → 返回 content → AI 使用

2. **错误场景**:
   - 请求不存在的 skill → 返回可用列表
   - 无 skills 目录 → 返回提示信息

3. **自动注册**:
   - 有 skills → SkillRequestTool 自动添加
   - 无 skills → 不添加工具

## 扩展可能性

未来可以扩展的功能：

1. **参数化 Skills**: 支持 skill 模板变量
2. **Skill 组合**: 多个 skills 组合使用
3. **Skill 缓存**: 缓存已加载的 skills
4. **远程 Skills**: 从远程仓库加载 skills
5. **Skill 版本**: 支持多版本 skills
6. **Skill 依赖**: Skills 之间的依赖关系

## 总结

SkillRequestTool 成功实现了一个完整的 skill 请求和加载机制，使得 AI Agent 可以：

1. ✅ 动态发现可用 skills
2. ✅ 通过 XML 格式请求 skill 定义
3. ✅ 将 skill content 加载到上下文
4. ✅ 根据 skill 指导执行任务
5. ✅ 自动注册和管理工具

这个工具与之前实现的 SkillService 和 SkillDocument 完美配合，构成了一个完整的 Skill 系统。
