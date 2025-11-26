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
 * 文章改进建议工具
 * <p>
 * 该工具用于分析现有文章并提供具体的改进建议。
 * 从结构、内容、论证、表达等多个维度进行专业分析，
 * 帮助作者发现问题并提供针对性的改进方案。
 * <p>
 * 使用场景：
 * - 文章审阅和评估
 * - 发现写作问题
 * - 获取改进方向
 * - 提升文章质量
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class SuggestImprovementsTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "suggest_improvements";

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
                分析文章并提供具体的改进建议。从多个维度进行专业分析，
                帮助作者发现问题并获得针对性的改进方案。
                
                **分析维度：**
                - 结构分析：文章组织、段落划分、逻辑层次
                - 内容分析：主题深度、论据充分性、信息完整性
                - 论证分析：论点明确性、论证方法、说服力
                - 表达分析：语言风格、用词准确性、句式多样性
                - 受众分析：是否符合目标读者需求
                
                **建议类型：**
                - 结构调整建议：如何优化文章结构
                - 内容补充建议：需要增加哪些内容
                - 论证强化建议：如何增强说服力
                - 表达优化建议：如何改进语言表达
                - 删减建议：哪些内容可以精简
                
                **输出特点：**
                - 建议具体、可操作
                - 指出问题的具体位置
                - 提供改进的具体方向
                - 兼顾整体和细节
                """;
    }

    @Override
    public String parameters() {
        return """
                - article: (必填) 需要分析的文章内容
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
                <suggest_improvements>
                <article>需要分析的文章内容</article>
                %s
                </suggest_improvements>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 分析技术文章
                <suggest_improvements>
                <article>
                云计算是一种新技术。它能让企业更方便地使用计算资源。
                很多公司都在使用云计算，因为它很好用。
                </article>
                </suggest_improvements>
                
                示例 2: 分析议论文
                <suggest_improvements>
                <article>
                读书很重要。我们应该多读书。读书可以增长知识，也可以提高素养。
                所以大家都应该养成读书的习惯。
                </article>
                </suggest_improvements>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("article") || StringUtils.isBlank(inputJson.get("article").getAsString())) {
                log.error("suggest_improvements 操作缺少必填参数 article");
                result.addProperty("error", "缺少必填参数 'article'");
                return result;
            }

            // 获取参数
            String article = inputJson.get("article").getAsString().trim();

            if (article.isEmpty()) {
                log.warn("article 参数为空");
                result.addProperty("error", "参数错误：article不能为空");
                return result;
            }

            log.info("开始分析文章并提供改进建议，文章长度: {}", article.length());

            // 调用服务提供改进建议
            Flux<String> suggestionsFlux = writerService.suggestImprovements(article);

            // 收集所有文本块
            StringBuilder suggestionsBuilder = new StringBuilder();
            suggestionsFlux.toStream().forEach(suggestionsBuilder::append);
            String suggestions = suggestionsBuilder.toString();

            // 设置成功响应
            result.addProperty("result", suggestions);
            result.addProperty("article_length", article.length());
            result.addProperty("success", true);

            log.info("成功生成改进建议，原文长度: {}, 建议长度: {}", article.length(), suggestions.length());

            return result;

        } catch (Exception e) {
            log.error("执行 suggest_improvements 操作时发生异常", e);
            result.addProperty("error", "生成改进建议失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
