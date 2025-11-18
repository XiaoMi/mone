package run.mone.mcp.multimodal.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片界面比较工具类
 * 用于比较两个图片是否是同一个软件界面
 */
@Slf4j
public class ImageComparisonUtil {

    private static final Gson gson = new Gson();

    /**
     * 比较两个图片是否是同一个软件界面
     *
     * @param llm        LLM实例,用于调用AI进行图片分析
     * @param imagePath1 第一张图片的文件路径
     * @param imagePath2 第二张图片的文件路径
     * @return InterfaceComparisonResult 包含比较结果的对象
     */
    public static InterfaceComparisonResult compareInterfaces(LLM llm, String imagePath1, String imagePath2) {
        try {
            // 将两张图片转换为base64
            String base64Image1 = ImageProcessingUtil.imageToBase64(imagePath1);
            String base64Image2 = ImageProcessingUtil.imageToBase64(imagePath2);

            log.info("开始比较两个界面: {} vs {}", imagePath1, imagePath2);

            // 构建系统提示词
            String systemPrompt = """
                    你是一个专业的界面识别专家。请仔细观察两张图片,判断它们是否是同一个软件的同一个界面。
                    
                    判断标准：
                    1. 整体布局结构是否一致(如：顶部导航栏、侧边栏、主内容区等)
                    2. 主要UI元素的位置和样式是否相同(如：按钮、菜单、图标等)
                    3. 界面的主题风格是否一致(如：颜色方案、字体风格等)
                    4. 软件的特征性标识是否存在(如：Logo、品牌元素等)
                    5. 窗口框架和控件样式是否一致
                    
                    注意事项：
                    - 界面中的具体内容(如文本内容、数据、图片)可能不同,这不影响判断
                    - 细微的状态变化(如按钮高亮、菜单展开/收起)不影响判断
                    - 滚动位置不同不影响判断
                    - 主要关注界面的整体结构和布局特征
                    
                    请严格按照以下JSON格式返回结果：
                    {
                        "isSameInterface": true/false,
                        "confidence": 0.95,
                        "explanation": "详细的判断理由",
                        "similarities": ["相似点1", "相似点2", "相似点3"],
                        "differences": ["差异点1", "差异点2"],
                        "interfaceType": "界面类型描述(如：文件管理器、浏览器、文本编辑器等)"
                    }
                    
                    要求：
                    1. isSameInterface 表示是否是同一个界面
                    2. confidence 是置信度,范围0-1
                    3. explanation 详细说明判断理由
                    4. similarities 列出主要的相似点
                    5. differences 列出主要的差异点
                    6. interfaceType 描述这是什么类型的界面
                    7. 只返回JSON,不要其他内容
                    \n
                    """;

            // 构建用户提示词
            String userPrompt = """
                    请比较以下两张图片,判断它们是否是同一个软件的同一个界面。
                    
                    第一张图片是图片1,第二张图片是图片2。
                    
                    请仔细观察两张图片的整体布局、UI元素、样式风格等特征,给出专业的判断。
                    """;

            // 构建多模态消息
            List<LLM.LLMPart> parts = new ArrayList<>();
            parts.add(LLM.LLMPart.builder()
                    .type(LLM.TYPE_IMAGE)
                    .data(base64Image1)
                    .mimeType("image/jpeg")
                    .build());
            parts.add(LLM.LLMPart.builder()
                    .type(LLM.TYPE_IMAGE)
                    .data(base64Image2)
                    .mimeType("image/jpeg")
                    .build());

            LLM.LLMCompoundMsg msg = LLM.LLMCompoundMsg.builder()
                    .content(userPrompt)
                    .parts(parts)
                    .build();

            // 调用LLM进行比较
            String response = llm.call(msg, systemPrompt);

            log.info("界面比较响应: {}", response);

            // 解析JSON响应
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            boolean isSameInterface = jsonResponse.has("isSameInterface")
                    ? jsonResponse.get("isSameInterface").getAsBoolean()
                    : false;
            double confidence = jsonResponse.has("confidence")
                    ? jsonResponse.get("confidence").getAsDouble()
                    : 0.0;
            String explanation = jsonResponse.has("explanation")
                    ? jsonResponse.get("explanation").getAsString()
                    : "";
            String interfaceType = jsonResponse.has("interfaceType")
                    ? jsonResponse.get("interfaceType").getAsString()
                    : "未知";

            // 解析相似点列表
            List<String> similarities = new ArrayList<>();
            if (jsonResponse.has("similarities") && jsonResponse.get("similarities").isJsonArray()) {
                jsonResponse.getAsJsonArray("similarities").forEach(element ->
                        similarities.add(element.getAsString())
                );
            }

            // 解析差异点列表
            List<String> differences = new ArrayList<>();
            if (jsonResponse.has("differences") && jsonResponse.get("differences").isJsonArray()) {
                jsonResponse.getAsJsonArray("differences").forEach(element ->
                        differences.add(element.getAsString())
                );
            }

            return InterfaceComparisonResult.builder()
                    .isSameInterface(isSameInterface)
                    .confidence(confidence)
                    .explanation(explanation)
                    .similarities(similarities)
                    .differences(differences)
                    .interfaceType(interfaceType)
                    .imagePath1(imagePath1)
                    .imagePath2(imagePath2)
                    .build();

        } catch (IOException e) {
            log.error("图片读取失败: {}", e.getMessage(), e);
            return InterfaceComparisonResult.builder()
                    .isSameInterface(false)
                    .confidence(0.0)
                    .explanation("图片读取失败: " + e.getMessage())
                    .similarities(new ArrayList<>())
                    .differences(new ArrayList<>())
                    .interfaceType("未知")
                    .imagePath1(imagePath1)
                    .imagePath2(imagePath2)
                    .build();
        } catch (Exception e) {
            log.error("界面比较失败: {}", e.getMessage(), e);
            return InterfaceComparisonResult.builder()
                    .isSameInterface(false)
                    .confidence(0.0)
                    .explanation("界面比较失败: " + e.getMessage())
                    .similarities(new ArrayList<>())
                    .differences(new ArrayList<>())
                    .interfaceType("未知")
                    .imagePath1(imagePath1)
                    .imagePath2(imagePath2)
                    .build();
        }
    }

    /**
     * 界面比较结果类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterfaceComparisonResult {
        private boolean isSameInterface;      // 是否是同一个界面
        private double confidence;            // 置信度 (0-1)
        private String explanation;           // 判断理由
        private List<String> similarities;    // 相似点列表
        private List<String> differences;     // 差异点列表
        private String interfaceType;         // 界面类型
        private String imagePath1;            // 第一张图片路径
        private String imagePath2;            // 第二张图片路径

        /**
         * 判断结果是否可信
         *
         * @param threshold 置信度阈值
         * @return 是否可信
         */
        public boolean isReliable(double threshold) {
            return confidence >= threshold;
        }

        /**
         * 获取JSON格式的结果
         *
         * @return JSON字符串
         */
        public String toJson() {
            return gson.toJson(this);
        }

        /**
         * 获取简要的比较摘要
         *
         * @return 比较摘要
         */
        public String getSummary() {
            return String.format("界面比较结果: %s, 置信度: %.2f, 类型: %s",
                    isSameInterface ? "同一界面" : "不同界面",
                    confidence,
                    interfaceType);
        }

        /**
         * 获取详细报告
         *
         * @return 详细报告字符串
         */
        public String getDetailedReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== 界面比较详细报告 ===\n");
            report.append(String.format("图片1: %s\n", imagePath1));
            report.append(String.format("图片2: %s\n", imagePath2));
            report.append(String.format("判断结果: %s\n", isSameInterface ? "同一界面" : "不同界面"));
            report.append(String.format("置信度: %.2f%%\n", confidence * 100));
            report.append(String.format("界面类型: %s\n", interfaceType));
            report.append(String.format("判断理由: %s\n", explanation));

            if (!similarities.isEmpty()) {
                report.append("\n相似点:\n");
                for (int i = 0; i < similarities.size(); i++) {
                    report.append(String.format("  %d. %s\n", i + 1, similarities.get(i)));
                }
            }

            if (!differences.isEmpty()) {
                report.append("\n差异点:\n");
                for (int i = 0; i < differences.size(); i++) {
                    report.append(String.format("  %d. %s\n", i + 1, differences.get(i)));
                }
            }

            return report.toString();
        }
    }
}

