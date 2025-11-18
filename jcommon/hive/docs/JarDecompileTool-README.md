# JarDecompileTool 使用说明

## 概述

`JarDecompileTool` 是hive项目中的一个工具，用于反编译JAR包并搜索反编译后的Java源代码。它使用**CFR反编译器**将编译后的.class文件转换为可读的Java源代码，然后支持通过正则表达式搜索代码内容。

## 功能特性

- **自动扫描JAR包**: 自动在工作空间(workspacePath)下查找JAR文件
- **CFR反编译**: 使用现代化的CFR反编译器，支持Java 8-21的所有特性
- **正则搜索**: 支持使用正则表达式搜索反编译后的代码
- **上下文显示**: 搜索结果会显示匹配行前后的上下文(默认3行)
- **缓存机制**: 反编译结果会缓存在临时目录，避免重复反编译
- **批量处理**: 支持批量反编译多个JAR文件
- **纯Java实现**: 无需安装外部工具，开箱即用

## 前置要求

✅ **无需任何外部依赖！**

CFR是纯Java实现的反编译器，已作为Maven依赖集成到项目中。不需要安装任何外部工具，项目编译后即可直接使用。

```xml
<!-- 已集成到pom.xml中 -->
<dependency>
    <groupId>org.benf</groupId>
    <artifactId>cfr</artifactId>
    <version>0.152</version>
</dependency>
```

## CFR反编译器优势

相比传统的JAD反编译器，CFR具有以下优势：

| 特性 | CFR | JAD |
|------|-----|-----|
| **部署方式** | Maven依赖，自动集成 | 需手动安装外部工具 |
| **Java版本支持** | Java 8-21 (包括最新特性) | 仅支持到Java 1.4 |
| **Lambda表达式** | ✅ 完美支持 | ❌ 不支持 |
| **Stream API** | ✅ 完美支持 | ❌ 不支持 |
| **Record类** | ✅ 完美支持 | ❌ 不支持 |
| **跨平台** | ✅ 纯Java，任何平台 | ❌ 需要平台特定二进制 |
| **反编译质量** | ✅ 高质量，可读性好 | ⚠️ 老旧，质量一般 |
| **维护状态** | ✅ 持续维护更新 | ❌ 已停止维护 |

## 使用方式

### 1. 列出工作空间中的所有JAR文件

```xml
<jar_decompile>
</jar_decompile>
```

**返回结果**:
```
Found 25 JAR files in workspace:
commons-lang3-3.8.1.jar (500.5 KB)
gson-2.9.1.jar (230.2 KB)
...
```

### 2. 反编译指定的JAR包

```xml
<jar_decompile>
<jar_name>commons-lang3-3.8.1.jar</jar_name>
</jar_decompile>
```

**返回结果**:
```
Decompiled JAR files:

✓ commons-lang3-3.8.1.jar
  Output: /tmp/jar_decompile/commons-lang3-3.8.1
  Classes: 523
```

### 3. 搜索特定方法的实现

```xml
<jar_decompile>
<jar_name>commons-lang3</jar_name>
<regex>public static String join\\(</regex>
</jar_decompile>
```

**返回结果**:
```
Found 12 results in decompiled code.

JAR: commons-lang3-3.8.1.jar
============================================================

org/apache/commons/lang3/StringUtils.java
------------------------------------------------------------
  /**
   * Joins the elements of the provided array into a single String
► public static String join(Object[] array, String separator) { [Line 3254]
      if (array == null) {
          return null;
      }

...
```

### 4. 查找注解使用

```xml
<jar_decompile>
<jar_name>spring-core</jar_name>
<regex>@Component|@Service|@Repository</regex>
</jar_decompile>
```

### 5. 在特定包中搜索

```xml
<jar_decompile>
<jar_name>mylib.jar</jar_name>
<regex>executeQuery|executeUpdate</regex>
<class_pattern>com/example/dao/*.class</class_pattern>
</jar_decompile>
```

### 6. 搜索Lambda表达式

```xml
<jar_decompile>
<jar_name>java-library.jar</jar_name>
<regex>-&gt;|::​</regex>
</jar_decompile>
```

### 7. 查找Stream操作

```xml
<jar_decompile>
<jar_name>modern-java-lib.jar</jar_name>
<regex>\\.stream\\(\\)|\\.filter\\(|\\.map\\(</regex>
</jar_decompile>
```

## 参数说明

| 参数 | 必填 | 说明 |
|------|------|------|
| jar_name | 否 | JAR文件名称或模式，支持通配符。不提供则列出所有JAR |
| regex | 否 | 正则表达式搜索模式。不提供则仅反编译不搜索 |
| class_pattern | 否 | 类文件过滤模式，使用glob语法(如 `com/example/*.class`) |

## 工作原理

1. **扫描阶段**: 遍历workspacePath目录查找匹配的JAR文件
2. **反编译阶段**: 使用CFR反编译器处理JAR包中的class文件
3. **搜索阶段**: 如果提供了regex，则在反编译的.java文件中搜索匹配项
4. **结果展示**: 返回格式化的搜索结果，包含上下文信息

## 缓存机制

反编译结果会缓存在系统临时目录中：
```
{系统临时目录}/jar_decompile/{jar包名称}/
```

如果该目录已存在且包含.java文件，工具会直接使用缓存，避免重复反编译。

要清空缓存，可以手动删除临时目录：
```bash
# Linux/Mac
rm -rf /tmp/jar_decompile

# Windows
del /s /q %TEMP%\jar_decompile
```

## 性能考虑

- **大型JAR包**: 首次反编译大型JAR包(如spring-core)可能需要几分钟
- **纯Java处理**: CFR在JVM中运行，无需外部进程调用，性能稳定
- **搜索限制**: 默认最多返回200个搜索结果，防止输出过大
- **输出限制**: 结果最大1MB，超出会截断

## 常见使用场景

### 1. 分析第三方库实现
```xml
<jar_decompile>
<jar_name>okhttp-4.12.0.jar</jar_name>
<regex>class.*Client.*\\{</regex>
</jar_decompile>
```

### 2. 查找弃用的API使用
```xml
<jar_decompile>
<jar_name>legacy-lib</jar_name>
<regex>@Deprecated</regex>
</jar_decompile>
```

### 3. 理解异常处理逻辑
```xml
<jar_decompile>
<jar_name>business-core</jar_name>
<regex>catch\\s*\\(.*Exception</regex>
</jar_decompile>
```

### 4. 搜索配置或常量
```xml
<jar_decompile>
<jar_name>config-lib</jar_name>
<regex>public static final String.*URL</regex>
</jar_decompile>
```

### 5. 分析现代Java特性使用
```xml
<jar_decompile>
<jar_name>modern-lib.jar</jar_name>
<regex>record\\s+\\w+|sealed\\s+class</regex>
</jar_decompile>
```

## 注意事项

1. **开箱即用**: 无需安装任何外部工具，Maven编译后即可使用
2. **反编译质量**: CFR提供高质量反编译，但仍无法100%还原源代码
3. **法律合规**: 仅用于调试、学习和合法的逆向工程，遵守软件许可协议
4. **性能影响**: 首次反编译大型JAR包会消耗较多CPU和内存
5. **缓存清理**: 定期清理临时目录中的缓存文件
6. **Java版本**: CFR支持所有现代Java特性（8-21），包括Lambda、Stream、Record等

## 故障排查

### 反编译失败
**可能原因**:
- JAR包损坏或不是有效的Java字节码
- 包含混淆过的代码（可能导致反编译结果不理想）
- 磁盘空间不足
- 内存不足（大型JAR包）

**解决方案**:
- 验证JAR文件完整性
- 增加JVM堆内存 (`-Xmx`)
- 清理缓存目录释放空间

### 搜索结果为空
**检查**:
- 正则表达式是否正确(注意转义)
- JAR包是否包含匹配的代码
- class_pattern过滤是否过于严格
- 确认JAR已成功反编译

### 反编译质量问题
**说明**:
- CFR已经是最好的Java反编译器之一
- 某些高度混淆的代码可能难以还原
- 编译器优化可能导致源码与反编译结果略有差异

## 技术实现

- **工具类**: `run.mone.hive.roles.tool.JarDecompileTool`
- **接口**: 实现了 `ITool` 接口
- **反编译器**: CFR 0.152 (纯Java实现)
- **缓存目录**: `System.getProperty("java.io.tmpdir") + "/jar_decompile"`
- **最大结果数**: 200个匹配项
- **最大输出**: 1MB
- **上下文行数**: 匹配行前后各3行

## 示例输出格式

```
Found 3 results in decompiled code.

JAR: commons-lang3-3.8.1.jar
============================================================

org/apache/commons/lang3/StringUtils.java
------------------------------------------------------------
  /**
   * Joins the elements of the provided array
► public static String join(Object[] array, String separator) { [Line 3254]
      if (array == null) {
          return null;
```

## 与其他反编译工具对比

| 特性 | JarDecompileTool (CFR) | 命令行JAD | 其他工具 |
|------|------------------------|----------|---------|
| 集成度 | ✅ 完全集成 | ❌ 需手动安装 | ⚠️ 视情况而定 |
| 现代Java支持 | ✅ Java 8-21 | ❌ 仅1.4 | ⚠️ 部分支持 |
| 搜索功能 | ✅ 内置正则搜索 | ❌ 无 | ❌ 通常无 |
| 批量处理 | ✅ 支持 | ⚠️ 需脚本 | ⚠️ 需脚本 |
| 缓存机制 | ✅ 智能缓存 | ❌ 无 | ❌ 通常无 |
| 上下文显示 | ✅ 自动显示 | ❌ 无 | ❌ 通常无 |

## 相关资源

- **CFR官网**: https://www.benf.org/other/cfr/
- **CFR GitHub**: https://github.com/leibnitz27/cfr
- **ITool接口**: [ITool.java](../src/main/java/run/mone/hive/roles/tool/ITool.java)
- **工具实现**: [JarDecompileTool.java](../src/main/java/run/mone/hive/roles/tool/JarDecompileTool.java)
- **测试用例**: [JarDecompileToolTest.java](../src/test/java/run/mone/hive/roles/tool/JarDecompileToolTest.java)

## 更新日志

### 当前版本 (CFR 0.152)
- ✅ 采用CFR替代JAD，无需外部依赖
- ✅ 支持Java 8-21所有特性
- ✅ 提供高质量反编译结果
- ✅ 纯Java实现，跨平台兼容
- ✅ 更好的Lambda、Stream、Record等现代特性支持

### 之前版本 (已废弃)
- ❌ 基于JAD，需要手动安装
- ❌ 仅支持老旧Java版本
- ❌ 需要平台特定二进制文件
