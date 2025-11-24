# 文件操作工具演示

这个演示展示了 `WriteToFileTool` 和 `ReplaceInFileTool` 的协同使用。

## 完整的工作流程示例

### 步骤1: 使用WriteToFileTool创建初始文件

```java
// 创建一个简单的Java服务类
WriteToFileTool writeToFileTool = new WriteToFileTool();
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example/UserService.java");
input.addProperty("content", """
    package com.example;
    
    public class UserService {
        
        public String getUserName(Long id) {
            // TODO: implement user lookup
            return "User " + id;
        }
        
        public void saveUser(String name) {
            // TODO: implement user saving
            System.out.println("Saving user: " + name);
        }
    }
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

### 步骤2: 使用ReplaceInFileTool添加依赖注入

```java
// 添加Spring注解和依赖
ReplaceInFileTool replaceInFileTool = new ReplaceInFileTool();
JsonObject input2 = new JsonObject();
input2.addProperty("path", "src/main/java/com/example/UserService.java");
input2.addProperty("diff", """
    ------- SEARCH
    package com.example;
    
    public class UserService {
    =======
    package com.example;
    
    import org.springframework.stereotype.Service;
    import org.springframework.beans.factory.annotation.Autowired;
    
    @Service
    public class UserService {
        
        @Autowired
        private UserRepository userRepository;
    +++++++ REPLACE
    """);

JsonObject result2 = replaceInFileTool.execute(role, input2);
```

### 步骤3: 使用ReplaceInFileTool实现具体方法

```java
// 实现getUserName方法
JsonObject input3 = new JsonObject();
input3.addProperty("path", "src/main/java/com/example/UserService.java");
input3.addProperty("diff", """
    ------- SEARCH
        public String getUserName(Long id) {
            // TODO: implement user lookup
            return "User " + id;
        }
    =======
        public String getUserName(Long id) {
            User user = userRepository.findById(id);
            return user != null ? user.getName() : "Unknown User";
        }
    +++++++ REPLACE
    """);

JsonObject result3 = replaceInFileTool.execute(role, input3);
```

### 步骤4: 使用WriteToFileTool创建配套的Repository接口

```java
// 创建UserRepository接口
JsonObject input4 = new JsonObject();
input4.addProperty("path", "src/main/java/com/example/UserRepository.java");
input4.addProperty("content", """
    package com.example;
    
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;
    
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        
        User findById(Long id);
        
        User findByName(String name);
        
    }
    """);

JsonObject result4 = writeToFileTool.execute(role, input4);
```

## 工具选择指南

### 何时使用WriteToFileTool

1. **创建新文件**
   - 新的类文件
   - 配置文件
   - 脚本文件
   - 文档文件

2. **完全重写文件**
   - 文件结构发生重大变化
   - 需要替换大部分内容
   - 从模板生成文件

3. **文件内容简单**
   - 小型文件
   - 结构简单的文件

### 何时使用ReplaceInFileTool

1. **精确修改**
   - 添加导入语句
   - 修改方法实现
   - 添加注解
   - 更新配置项

2. **保持文件结构**
   - 只修改特定部分
   - 保留其他内容不变
   - 多个小改动

3. **大文件修改**
   - 避免重写整个文件
   - 减少出错风险
   - 保持版本控制友好

## 实际应用场景

### 场景1: 创建Spring Boot项目结构

```java
// 1. 创建主类
writeToFileTool.execute(role, createInput("src/main/java/com/example/Application.java", 
    springBootMainClass));

// 2. 创建Controller
writeToFileTool.execute(role, createInput("src/main/java/com/example/controller/UserController.java", 
    userControllerClass));

// 3. 添加新的端点到Controller
replaceInFileTool.execute(role, createReplaceInput("src/main/java/com/example/controller/UserController.java",
    addNewEndpoint));

// 4. 创建配置文件
writeToFileTool.execute(role, createInput("src/main/resources/application.yml", 
    applicationConfig));
```

### 场景2: 前端项目开发

```java
// 1. 创建HTML页面
writeToFileTool.execute(role, createInput("public/index.html", htmlTemplate));

// 2. 创建CSS样式
writeToFileTool.execute(role, createInput("public/styles.css", cssStyles));

// 3. 添加新的CSS规则
replaceInFileTool.execute(role, createReplaceInput("public/styles.css", addNewStyles));

// 4. 创建JavaScript文件
writeToFileTool.execute(role, createInput("public/app.js", jsApplication));
```

### 场景3: 配置文件管理

```java
// 1. 创建基础配置
writeToFileTool.execute(role, createInput("config/database.properties", baseDbConfig));

// 2. 更新特定配置项
replaceInFileTool.execute(role, createReplaceInput("config/database.properties", updateDbUrl));

// 3. 添加新的配置项
replaceInFileTool.execute(role, createReplaceInput("config/database.properties", addNewConfig));
```

## 最佳实践

### 1. 工具选择原则
- 新文件 → WriteToFileTool
- 修改现有文件 → ReplaceInFileTool
- 大量修改 → WriteToFileTool
- 精确修改 → ReplaceInFileTool

### 2. 错误处理
```java
JsonObject result = tool.execute(role, input);
if (result.has("error")) {
    log.error("工具执行失败: {}", result.get("error").getAsString());
    // 处理错误情况
} else {
    log.info("工具执行成功: {}", result.get("result").getAsString());
}
```

### 3. 内容验证
- WriteToFileTool: 确保提供完整的文件内容
- ReplaceInFileTool: 确保SEARCH内容精确匹配

### 4. 路径管理
- 使用相对路径
- 确保路径安全
- 考虑跨平台兼容性

## 工具组合使用模式

### 模式1: 创建-修改模式
1. WriteToFileTool创建基础文件
2. ReplaceInFileTool进行精确修改

### 模式2: 模板-定制模式
1. WriteToFileTool从模板创建文件
2. ReplaceInFileTool定制特定内容

### 模式3: 批量处理模式
1. WriteToFileTool创建多个相关文件
2. ReplaceInFileTool统一修改所有文件

这两个工具的结合使用可以满足几乎所有的文件操作需求，从简单的文件创建到复杂的代码重构都能胜任。
