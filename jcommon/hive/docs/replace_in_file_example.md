# ReplaceInFileTool 使用示例

这个文档展示了如何使用新实现的 `ReplaceInFileTool` 工具。

## 工具简介

`ReplaceInFileTool` 是一个Java版本的文件内容替换工具，参考了Cline项目中的 `replace_in_file` 工具实现。它使用SEARCH/REPLACE块格式来对文件进行精确的部分内容替换。

## 主要特性

1. **精确匹配**: 支持字符级精确匹配
2. **行级匹配**: 支持忽略行首行尾空格的匹配
3. **锚点匹配**: 对于大块内容，支持使用首尾行作为锚点的匹配
4. **多重替换**: 支持在一次操作中进行多个替换
5. **内容删除**: 支持通过空的REPLACE部分删除内容

## 使用方法

### 1. 添加工具到ReactorRole

```java
ReactorRole role = new ReactorRole();
ReplaceInFileTool replaceInFileTool = new ReplaceInFileTool();
role.addTool(replaceInFileTool);
```

### 2. 基本替换操作

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/Example.java");
input.addProperty("diff", """
    ------- SEARCH
    public void oldMethod() {
        System.out.println("old");
    }
    =======
    public void newMethod() {
        System.out.println("new");
    }
    +++++++ REPLACE
    """);

JsonObject result = replaceInFileTool.execute(role, input);
```

### 3. 多重替换操作

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/Example.java");
input.addProperty("diff", """
    ------- SEARCH
    public class Example {
    =======
    @Component
    public class Example {
    +++++++ REPLACE
    
    ------- SEARCH
        return name;
    =======
        return this.name;
    +++++++ REPLACE
    """);

JsonObject result = replaceInFileTool.execute(role, input);
```

### 4. 删除内容

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/Example.java");
input.addProperty("diff", """
    ------- SEARCH
    // TODO: remove this line
    =======
    +++++++ REPLACE
    """);

JsonObject result = replaceInFileTool.execute(role, input);
```

## SEARCH/REPLACE块格式规则

1. **SEARCH内容必须精确匹配**:
   - 包括所有空格、缩进、换行符
   - 包括注释、文档字符串等

2. **块标记格式**:
   - `------- SEARCH` (开始搜索块)
   - `=======` (搜索结束，替换开始)
   - `+++++++ REPLACE` (替换结束)

3. **匹配策略**:
   - 首先尝试精确字符匹配
   - 如果失败，尝试行级匹配（忽略行首行尾空格）
   - 对于3行以上的块，尝试锚点匹配（使用首尾行）

4. **多重替换**:
   - 按照在文件中出现的顺序列出SEARCH/REPLACE块
   - 每个块只替换第一个匹配的内容

## 错误处理

工具会在以下情况返回错误：

1. 缺少必需参数（path或diff）
2. 文件不存在或是目录
3. SEARCH/REPLACE块格式不正确
4. 搜索内容在文件中未找到匹配

## 测试

项目包含了完整的单元测试，位于：
`src/test/java/run/mone/hive/roles/tool/ReplaceInFileToolTest.java`

测试覆盖了：
- 基本替换功能
- 多重替换
- 内容删除
- 空格处理
- 错误情况处理

## 集成到系统提示中

要在系统提示中使用此工具，需要将其添加到 `MonerSystemPrompt` 的工具列表中。工具会自动生成相应的使用说明和示例。

## 注意事项

1. 确保SEARCH内容与文件中的内容完全匹配
2. 对于大文件，建议使用较小的SEARCH/REPLACE块
3. 在进行多重替换时，注意顺序很重要
4. 工具支持UTF-8编码的文本文件
