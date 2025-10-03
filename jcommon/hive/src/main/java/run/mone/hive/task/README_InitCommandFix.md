# /init 指令集成修复

## 🔍 问题分析

根据用户提供的日志分析，发现 `/init` 指令没有正确执行，Agent 直接进入了普通的工具调用流程，而不是执行我们实现的 `InitCommand` 逻辑。

### 问题根源
**斜杠命令解析器只在 `TaskExecutionLoop` 中被调用，但是 Agent 使用的是 `ReactorRole`，它有自己的执行流程，没有集成 `SlashCommandParser`。**

从日志可以看出：
- Agent 接收到了 `/init` 输入
- 但是 Agent 直接开始使用 `list_files` 工具探索项目结构
- 没有触发我们实现的 `InitCommand` 逻辑

## 🔧 解决方案

### 1. 修改 ReactorRole 集成斜杠命令解析

#### 1.1 添加斜杠命令解析器字段
```java
// 斜杠命令解析器
private SlashCommandParser slashCommandParser;
```

#### 1.2 在构造函数中初始化
```java
public ReactorRole(String name, CountDownLatch countDownLatch, LLM llm) {
    this(name, "", "", "", "", "", 0, llm, Lists.newArrayList(), Lists.newArrayList());
    // 初始化意图分类服务
    this.classificationService = new IntentClassificationService();
    // 初始化斜杠命令解析器
    this.slashCommandParser = new SlashCommandParser();
}
```

#### 1.3 修改 buildUserPrompt 方法
```java
//构建用户提问的prompt
//1.支持从网络获取内容  2.支持从知识库获取内容  3.支持斜杠命令解析
public String buildUserPrompt(Message msg, String history, FluxSink sink) {
    // ... 其他逻辑 ...
    
    // 处理斜杠命令
    String processedContent = processSlashCommands(msg.getContent(), sink);
    
    return AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.<String, String>builder()
            // ... 其他参数 ...
            .put("question", processedContent)
            .build());
}
```

#### 1.4 添加 processSlashCommands 方法
```java
/**
 * 处理斜杠命令
 * @param content 用户输入内容
 * @param sink 流式输出
 * @return 处理后的内容
 */
private String processSlashCommands(String content, FluxSink sink) {
    if (slashCommandParser == null) {
        return content;
    }
    
    try {
        // 创建FocusChainSettings（如果需要的话）
        FocusChainSettings focusChainSettings = new FocusChainSettings();
        if (focusChainManager != null && focusChainManager.getFocusChainSettings() != null) {
            focusChainSettings = focusChainManager.getFocusChainSettings();
        }
        
        // 解析斜杠命令
        SlashCommandParser.ParseResult parseResult = slashCommandParser.parseSlashCommands(content, focusChainSettings);
        
        // 如果解析到了命令，记录日志
        if (!parseResult.getProcessedText().equals(content)) {
            log.info("斜杠命令解析成功，原始内容: {}, 处理后内容: {}", content, parseResult.getProcessedText());
            sink.next("🔧 检测到斜杠命令，正在处理...\n");
        }
        
        return parseResult.getProcessedText();
    } catch (Exception e) {
        log.error("斜杠命令解析失败: {}", e.getMessage(), e);
        return content; // 解析失败时返回原始内容
    }
}
```

### 2. 修复 SlashCommandParser 支持直接斜杠命令

#### 2.1 问题
原来的 `SlashCommandParser` 只在XML标签内查找斜杠命令，但是用户直接输入 `/init` 时，没有XML标签包装。

#### 2.2 解决方案
在 `parseSlashCommands` 方法中添加对直接斜杠命令的支持：

```java
public ParseResult parseSlashCommands(String text, FocusChainSettings focusChainSettings) {
    // 首先检查直接的斜杠命令（如 /init）
    String trimmedText = text.trim();
    if (trimmedText.startsWith("/")) {
        String commandName = trimmedText.substring(1).split("\\s+")[0]; // 获取命令名（去掉斜杠和参数）
        
        if (SUPPORTED_DEFAULT_COMMANDS.contains(commandName)) {
            SlashCommand command = registeredCommands.get(commandName);
            if (command != null) {
                String processedText = command.execute(text, focusChainSettings);
                return new ParseResult(processedText, commandName.equals("newrule"));
            }
        }
    }
    
    // 然后检查XML标签内的斜杠命令
    // ... 原有逻辑 ...
}
```

## 🎯 修复效果

### 修复前
```
用户输入: /init
Agent行为: 直接使用 list_files 工具探索项目
结果: 没有触发 InitCommand 逻辑
```

### 修复后
```
用户输入: /init
Agent行为: 
1. 检测到斜杠命令
2. 解析 /init 命令
3. 执行 InitCommand.execute() 方法
4. 生成完整的 init 指令提示词
5. 开始执行 MCODE.md 生成流程
```

## 🔄 执行流程

### 1. 用户输入 `/init`
### 2. ReactorRole.act() 调用 buildUserPrompt()
### 3. buildUserPrompt() 调用 processSlashCommands()
### 4. processSlashCommands() 调用 SlashCommandParser.parseSlashCommands()
### 5. SlashCommandParser 识别 `/init` 命令
### 6. 调用 InitCommand.execute() 生成提示词
### 7. 返回包含 init 指令的完整提示词
### 8. Agent 开始执行 MCODE.md 生成流程

## 📋 测试验证

创建了 `SlashCommandTest` 类来验证斜杠命令解析是否正确工作：

```java
public static void main(String[] args) {
    SlashCommandParser parser = new SlashCommandParser();
    FocusChainSettings settings = new FocusChainSettings();
    
    // 测试/init命令
    String testInput = "/init";
    SlashCommandParser.ParseResult result = parser.parseSlashCommands(testInput, settings);
    
    System.out.println("原始输入: " + testInput);
    System.out.println("解析结果: " + result.getProcessedText());
    System.out.println("是否包含init指令: " + result.getProcessedText().contains("explicit_instructions type=\"init\""));
}
```

## 🎉 总结

通过以上修改，我们成功将斜杠命令解析集成到了 `ReactorRole` 的工作流程中，使得 `/init` 指令能够正确识别和执行。现在当用户输入 `/init` 时，Agent 会：

1. 正确识别斜杠命令
2. 执行 InitCommand 逻辑
3. 生成完整的 MCODE.md 分析流程
4. 开始执行八步骤的代码库分析

这个修复确保了 `/init` 指令能够按照预期工作，为用户提供完整的 MCODE.md 生成功能。