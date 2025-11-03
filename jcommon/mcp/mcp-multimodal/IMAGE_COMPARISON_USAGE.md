# ImageComparisonUtil 使用指南

## 简介

`ImageComparisonUtil` 是一个基于 AI 多模态能力的图片界面比较工具类，可以智能判断两张截图是否是同一个软件的同一个界面。

## 主要特性

- ✅ 智能识别界面布局结构
- ✅ 忽略内容差异，关注界面特征
- ✅ 支持多种 LLM 模型（Doubao、Gemini、Claude 等）
- ✅ 返回详细的比较结果和置信度
- ✅ 提供 JSON 格式输出

## 快速开始

### 1. 基本使用

```java
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.util.ImageComparisonUtil;

// 配置 LLM
LLMConfig config = LLMConfig.builder()
        .llmProvider(LLMProvider.DOUBAO_VISION)
        .model("doubao-vision-pro-32k")
        .maxTokens(4000)
        .build();

LLM llm = new LLM(config);

// 比较两张图片
String imagePath1 = "/path/to/screenshot1.png";
String imagePath2 = "/path/to/screenshot2.png";

ImageComparisonUtil.InterfaceComparisonResult result = 
        ImageComparisonUtil.compareInterfaces(llm, imagePath1, imagePath2);

// 获取结果
boolean isSame = result.isSameInterface();
double confidence = result.getConfidence();
String explanation = result.getExplanation();

System.out.println("是否同一界面: " + isSame);
System.out.println("置信度: " + confidence);
System.out.println("解释: " + explanation);
```

### 2. 结果对象 (InterfaceComparisonResult)

比较结果包含以下字段：

```java
{
    "isSameInterface": true,           // 是否是同一界面
    "confidence": 0.95,                 // 置信度 (0-1)
    "explanation": "两张图片显示的都是VS Code编辑器...",
    "similarities": [                   // 相似点列表
        "都是VS Code界面",
        "左侧都有文件浏览器",
        "顶部都有菜单栏"
    ],
    "differences": [                    // 差异点列表
        "打开的文件不同",
        "光标位置不同"
    ],
    "interfaceType": "代码编辑器",      // 界面类型
    "imagePath1": "/path/to/img1.png",
    "imagePath2": "/path/to/img2.png"
}
```

### 3. 结果判断方法

```java
// 判断置信度是否达到阈值
if (result.isReliable(0.8)) {
    System.out.println("结果可信（置信度 >= 0.8）");
}

// 获取简要摘要
String summary = result.getSummary();
System.out.println(summary);
// 输出: "界面比较结果: 同一界面, 置信度: 0.95, 类型: 代码编辑器"

// 获取详细报告
String report = result.getDetailedReport();
System.out.println(report);

// 获取 JSON 格式结果
String json = result.toJson();
System.out.println(json);
```

## 支持的 LLM 模型

### 1. Doubao Vision (推荐)

```java
LLMConfig config = LLMConfig.builder()
        .llmProvider(LLMProvider.DOUBAO_VISION)
        .model("doubao-vision-pro-32k")
        .maxTokens(4000)
        .build();
```

### 2. Google Gemini

```java
LLMConfig config = LLMConfig.builder()
        .llmProvider(LLMProvider.GOOGLE_2)
        .model("gemini-2.0-flash-exp")
        .maxTokens(4000)
        .build();
```

### 3. OpenRouter + Claude

```java
LLMConfig config = LLMConfig.builder()
        .llmProvider(LLMProvider.OPENROUTER)
        .model("anthropic/claude-3.5-sonnet")
        .maxTokens(4000)
        .build();
```

### 4. Claude Company

```java
LLMConfig config = LLMConfig.builder()
        .llmProvider(LLMProvider.CLAUDE_COMPANY)
        .model("claude-3-sonnet-20240229")
        .maxTokens(4000)
        .build();
```

## 实际使用场景

### 场景1: GUI 自动化测试

```java
// 验证操作后界面是否正确跳转
public boolean verifyPageNavigation(String beforeScreenshot, String afterScreenshot) {
    ImageComparisonUtil.InterfaceComparisonResult result = 
            ImageComparisonUtil.compareInterfaces(llm, beforeScreenshot, afterScreenshot);
    
    // 如果界面发生了变化，说明跳转成功
    return !result.isSameInterface() && result.isReliable(0.8);
}
```

### 场景2: 界面稳定性检测

```java
// 检查刷新后界面是否保持一致
public boolean checkInterfaceStability(String screenshot1, String screenshot2) {
    ImageComparisonUtil.InterfaceComparisonResult result = 
            ImageComparisonUtil.compareInterfaces(llm, screenshot1, screenshot2);
    
    if (result.isSameInterface() && result.isReliable(0.9)) {
        log.info("界面稳定，保持一致");
        return true;
    } else {
        log.warn("界面发生变化: {}", result.getExplanation());
        return false;
    }
}
```

### 场景3: 应用状态监控

```java
// 监控应用是否还在指定界面
public void monitorApplicationState() {
    String referenceScreenshot = "/path/to/expected_interface.png";
    String currentScreenshot = captureCurrentScreen();
    
    ImageComparisonUtil.InterfaceComparisonResult result = 
            ImageComparisonUtil.compareInterfaces(llm, referenceScreenshot, currentScreenshot);
    
    if (!result.isSameInterface()) {
        alert("应用界面已切换: " + result.getInterfaceType());
    }
}
```

### 场景4: 批量界面分类

```java
// 对多张截图进行分类，找出相同界面的截图
public Map<String, List<String>> classifyScreenshots(List<String> screenshots) {
    Map<String, List<String>> groups = new HashMap<>();
    
    for (String screenshot : screenshots) {
        boolean foundGroup = false;
        
        // 与已有分组比较
        for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
            String representative = entry.getKey();
            
            ImageComparisonUtil.InterfaceComparisonResult result = 
                    ImageComparisonUtil.compareInterfaces(llm, representative, screenshot);
            
            if (result.isSameInterface() && result.isReliable(0.85)) {
                entry.getValue().add(screenshot);
                foundGroup = true;
                break;
            }
        }
        
        // 创建新分组
        if (!foundGroup) {
            groups.put(screenshot, new ArrayList<>(Arrays.asList(screenshot)));
        }
    }
    
    return groups;
}
```

## 判断逻辑说明

### AI 会关注的特征：

✅ **布局结构**
- 顶部导航栏、侧边栏、主内容区等的位置
- 窗口框架和控件的排列方式

✅ **UI 元素**
- 按钮、菜单、图标等的位置和样式
- 工具栏和状态栏的组成

✅ **主题风格**
- 颜色方案、字体样式
- 软件的品牌标识（Logo、图标等）

### AI 会忽略的差异：

❌ **内容变化**
- 文本内容、数据、图片的具体内容不同

❌ **状态变化**
- 按钮高亮、菜单展开/收起等细微状态

❌ **位置变化**
- 滚动位置、窗口大小的轻微调整

## 性能建议

1. **图片格式**: 支持 PNG、JPEG、WEBP 等常见格式
2. **图片大小**: 建议不超过 5MB，避免影响上传速度
3. **分辨率**: 建议使用原始分辨率，不需要额外压缩
4. **并发调用**: 可以并发调用多个比较任务，提高效率

## 错误处理

```java
try {
    ImageComparisonUtil.InterfaceComparisonResult result = 
            ImageComparisonUtil.compareInterfaces(llm, imagePath1, imagePath2);
    
    if (result.getConfidence() == 0.0) {
        // 处理失败的情况
        log.error("比较失败: {}", result.getExplanation());
    }
} catch (Exception e) {
    log.error("发生异常", e);
}
```

## 注意事项

1. **环境变量**: 确保设置了相应 LLM 的 API Key 环境变量
2. **网络连接**: 需要能够访问 LLM 服务的 API 端点
3. **置信度阈值**: 建议根据实际场景设置合适的置信度阈值（通常 0.8-0.9）
4. **成本考虑**: 每次比较会调用一次多模态 LLM API，需要考虑成本

## 完整示例

```java
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.util.ImageComparisonUtil;

public class ImageComparisonExample {
    
    public static void main(String[] args) {
        // 1. 配置 LLM
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.DOUBAO_VISION)
                .model("doubao-vision-pro-32k")
                .maxTokens(4000)
                .build();
        
        LLM llm = new LLM(config);
        
        // 2. 准备图片路径
        String screenshot1 = "/Users/user/screenshots/app_screen1.png";
        String screenshot2 = "/Users/user/screenshots/app_screen2.png";
        
        // 3. 执行比较
        ImageComparisonUtil.InterfaceComparisonResult result = 
                ImageComparisonUtil.compareInterfaces(llm, screenshot1, screenshot2);
        
        // 4. 处理结果
        if (result.isSameInterface() && result.isReliable(0.8)) {
            System.out.println("✅ 确认在同一个界面");
            System.out.println("📊 置信度: " + String.format("%.2f%%", result.getConfidence() * 100));
            System.out.println("🏷️ 界面类型: " + result.getInterfaceType());
            
            System.out.println("\n📝 相似点:");
            result.getSimilarities().forEach(s -> System.out.println("  - " + s));
            
            if (!result.getDifferences().isEmpty()) {
                System.out.println("\n🔍 差异点:");
                result.getDifferences().forEach(d -> System.out.println("  - " + d));
            }
        } else {
            System.out.println("❌ 不在同一个界面");
            System.out.println("原因: " + result.getExplanation());
        }
        
        // 5. 导出 JSON 结果
        System.out.println("\n📄 JSON 结果:");
        System.out.println(result.toJson());
    }
}
```

## 常见问题

### Q1: 如何提高识别准确率？

A: 
1. 使用高质量的截图（清晰、完整）
2. 选择性能更强的模型（如 Claude Sonnet）
3. 确保两张图片的分辨率相近
4. 避免过度压缩的图片

### Q2: 比较速度慢怎么办？

A:
1. 使用更快的模型（如 Gemini Flash）
2. 调整 maxTokens 参数
3. 批量处理时使用并发调用

### Q3: 置信度如何设置？

A:
- 0.9+ : 极高要求，几乎完全确定
- 0.8-0.9 : 高要求，推荐使用
- 0.7-0.8 : 中等要求，可能有误判
- <0.7 : 低要求，不推荐

## 更新日志

- **v1.0.0** (2025-01-03)
  - 初始版本发布
  - 支持基本的界面比较功能
  - 支持多种 LLM 模型

## 联系方式

如有问题或建议，请联系项目维护者。

