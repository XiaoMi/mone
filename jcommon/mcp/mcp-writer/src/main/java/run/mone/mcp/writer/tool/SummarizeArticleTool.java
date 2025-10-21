package run.mone.mcp.writer.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.writer.service.WriterService;

/**
 * 总结文章工具
 * <p>
 * 该工具用于对文章进行总结，提炼出主要观点和关键信息。
 * 生成简洁明了的摘要，帮助读者快速了解文章核心内容。
 * <p>
 * 使用场景：
 * - 生成文章摘要
 * - 提炼核心观点
 * - 快速了解长文内容
 * - 创建内容概览
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class SummarizeArticleTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "summarize_article";

    /**
     * 写作服务
     */
    @Autowired
    private WriterService writerService;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                对文章进行总结，提炼出主要观点和关键信息。
                生成简洁明了的摘要，帮助读者快速掌握文章核心内容。
                
                **总结特点：**
                - 提炼核心观点：抓住文章的主要论点
                - 保留关键信息：保留最重要的事实和数据
                - 简洁明了：用简短的语言表达核心内容
                - 逻辑清晰：保持原文的逻辑结构
                - 客观准确：忠实于原文，不添加主观解读
                
                **适用场景：**
                - 为长文章生成摘要
                - 快速了解文章内容
                - 创建文章导读
                - 制作会议纪要
                - 提取研究要点
                
                **输出格式：**
                - 可以是段落形式的总结
                - 可以是要点列表
                - 包含主要观点和关键结论
                """;
    }

    @Override
    public String parameters() {
        return """
                - article: (必填) 需要总结的文章内容
                - originalRequest: (可选) 用户的原始需求说明，可以指定总结的侧重点或格式
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                任务进度记录（可选）
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <summarize_article>
                <article>需要总结的文章内容</article>
                <originalRequest>总结要求（可选）</originalRequest>
                %s
                </summarize_article>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 总结技术文章
                <summarize_article>
                <article>
                云计算是一种通过互联网提供计算资源的技术模式。它允许用户按需访问和使用
                计算能力、存储空间和各种应用服务，而无需自己购买和维护昂贵的硬件设备。
                云计算主要有三种服务模式：IaaS（基础设施即服务）、PaaS（平台即服务）
                和SaaS（软件即服务）。这种技术大大降低了企业的IT成本，提高了资源利用率，
                并且具有高度的灵活性和可扩展性。
                </article>
                <originalRequest>用要点列表的形式总结</originalRequest>
                </summarize_article>
                
                示例 2: 总结研究论文
                <summarize_article>
                <article>
                本研究探讨了深度学习在医疗影像诊断中的应用。通过训练卷积神经网络模型，
                我们在肺部CT扫描的肺结节检测任务上取得了95%的准确率。研究使用了超过
                10万张医疗影像数据进行训练和验证。结果表明，AI辅助诊断系统能够显著
                提高医生的诊断效率和准确性，尤其是在早期病变的识别方面具有明显优势。
                </article>
                </summarize_article>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("article") || StringUtils.isBlank(inputJson.get("article").getAsString())) {
                log.error("summarize_article 操作缺少必填参数 article");
                result.addProperty("error", "缺少必填参数 'article'");
                return result;
            }

            // 获取参数
            String article = inputJson.get("article").getAsString().trim();
            String originalRequest = inputJson.has("originalRequest") 
                    ? inputJson.get("originalRequest").getAsString().trim() 
                    : null;

            if (article.isEmpty()) {
                log.warn("article 参数为空");
                result.addProperty("error", "参数错误：article不能为空");
                return result;
            }

            log.info("开始总结文章，原文长度: {}", article.length());

            // 调用服务总结文章
            Flux<String> summaryFlux = writerService.summarizeArticle(article, originalRequest);

            // 收集所有文本块
            StringBuilder summaryBuilder = new StringBuilder();
            summaryFlux.toStream().forEach(summaryBuilder::append);
            String summary = summaryBuilder.toString();

            // 设置成功响应
            result.addProperty("result", summary);
            result.addProperty("original_length", article.length());
            result.addProperty("summary_length", summary.length());
            result.addProperty("compression_ratio", 
                    String.format("%.2f%%", (double) summary.length() / article.length() * 100));
            result.addProperty("success", true);

            log.info("成功总结文章，原文长度: {}, 总结长度: {}", 
                    article.length(), summary.length());

            return result;

        } catch (Exception e) {
            log.error("执行 summarize_article 操作时发生异常", e);
            result.addProperty("error", "总结文章失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
