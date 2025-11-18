package run.mone.mcp.multimodal.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.util.ImageComparisonUtil;

/**
 * ImageComparisonUtil 测试类
 */
@Slf4j
public class ImageComparisonUtilTest {

    @Test
    public void testCompareInterfaces() {
        // 配置LLM
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.DOUBAO_VISION) // 使用支持多模态的模型
                .maxTokens(4000)
//                .json(true)
                .build();

        LLM llm = new LLM(config);

        // 测试图片路径(请替换为实际的图片路径)
        String imagePath1 = "/tmp/img/a.png";
        String imagePath2 = "/tmp/img/b.png";

        // 执行比较
        ImageComparisonUtil.InterfaceComparisonResult result = 
                ImageComparisonUtil.compareInterfaces(llm, imagePath1, imagePath2);

        // 输出结果
        log.info("比较结果: {}", result.isSameInterface() ? "同一界面" : "不同界面");
        log.info("置信度: {}", result.getConfidence());
        log.info("解释: {}", result.getExplanation());
        log.info("界面类型: {}", result.getInterfaceType());

        // 输出详细报告
        log.info("\n{}", result.getDetailedReport());

        // 输出JSON格式
        log.info("JSON结果: {}", result.toJson());

        // 检查置信度
        if (result.isReliable(0.8)) {
            log.info("结果可信(置信度 >= 0.8)");
        } else {
            log.info("结果置信度较低,请人工确认");
        }
    }

    @Test
    public void testCompareInterfacesWithOpenRouter() {
        // 使用 OpenRouter + Claude Sonnet 模型
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.OPENROUTER)
                .model("anthropic/claude-3.5-sonnet")
                .maxTokens(4000)
                .build();

        LLM llm = new LLM(config);

        String imagePath1 = "/path/to/your/screenshot1.png";
        String imagePath2 = "/path/to/your/screenshot2.png";

        ImageComparisonUtil.InterfaceComparisonResult result = 
                ImageComparisonUtil.compareInterfaces(llm, imagePath1, imagePath2);

        log.info("简要摘要: {}", result.getSummary());
        log.info("\n详细报告:\n{}", result.getDetailedReport());
    }

    @Test
    public void testCompareInterfacesWithGemini() {
        // 使用 Google Gemini 模型
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.GOOGLE_2)
                .model("gemini-2.0-flash-exp")
                .maxTokens(4000)
                .build();

        LLM llm = new LLM(config);

        String imagePath1 = "/path/to/your/screenshot1.png";
        String imagePath2 = "/path/to/your/screenshot2.png";

        ImageComparisonUtil.InterfaceComparisonResult result = 
                ImageComparisonUtil.compareInterfaces(llm, imagePath1, imagePath2);

        // 输出JSON格式的结果
        System.out.println("JSON结果:");
        System.out.println(result.toJson());
    }

    /**
     * 实际使用示例
     */
    @Test
    public void testRealWorldExample() {
        // 1. 创建LLM实例
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.DOUBAO_VISION)
                .model("doubao-vision-pro-32k")
                .maxTokens(4000)
                .build();

        LLM llm = new LLM(config);

        // 2. 准备图片路径
        String screenshotBefore = "/Users/user/screenshots/vscode_before.png";
        String screenshotAfter = "/Users/user/screenshots/vscode_after.png";

        // 3. 执行比较
        ImageComparisonUtil.InterfaceComparisonResult result = 
                ImageComparisonUtil.compareInterfaces(llm, screenshotBefore, screenshotAfter);

        // 4. 根据结果进行处理
        if (result.isSameInterface() && result.isReliable(0.8)) {
            System.out.println("确认在同一个界面,可以继续操作");
            System.out.println("置信度: " + result.getConfidence());
        } else if (result.isSameInterface() && !result.isReliable(0.8)) {
            System.out.println("可能在同一个界面,但置信度较低: " + result.getConfidence());
            System.out.println("建议: " + result.getExplanation());
        } else {
            System.out.println("不在同一个界面");
            System.out.println("原因: " + result.getExplanation());
        }

        // 5. 查看详细信息
        System.out.println("\n=== 相似点 ===");
        result.getSimilarities().forEach(s -> System.out.println("- " + s));

        System.out.println("\n=== 差异点 ===");
        result.getDifferences().forEach(d -> System.out.println("- " + d));
    }
}

