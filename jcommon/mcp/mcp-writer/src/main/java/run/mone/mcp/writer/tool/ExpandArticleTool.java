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
 * 扩写文章工具
 * <p>
 * 该工具用于扩展现有文章，增加更多细节、例子和深度分析。
 * 在保持原文主旨的基础上，使文章内容更加丰富和具体。
 * <p>
 * 使用场景：
 * - 文章篇幅不足需要扩充
 * - 需要增加更多细节和例子
 * - 深化文章内容和论述
 * - 提升文章的信息量
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class ExpandArticleTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "expand_article";

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
                扩展现有文章，增加更多细节、例子和深度分析。
                在保持原文主旨和风格的基础上，使内容更加丰富具体。
                
                **扩写方式：**
                - 增加具体例子：用实例说明观点
                - 深化论述：对关键点进行更详细的分析
                - 补充背景：添加相关背景信息
                - 扩展细节：对重要内容进行详细描述
                - 增加数据：补充统计数据和事实支撑
                
                **适用场景：**
                - 文章内容过于简略
                - 需要满足特定字数要求
                - 论述不够深入需要扩展
                - 需要增强文章的说服力
                
                **保持特点：**
                - 保留原文核心观点
                - 维持原有写作风格
                - 保持逻辑连贯性
                - 不偏离主题
                """;
    }

    @Override
    public String parameters() {
        return """
                - article: (必填) 需要扩写的文章内容
                - originalRequest: (可选) 用户的原始需求说明，可以指定扩写的重点方向
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
                <expand_article>
                <article>需要扩写的文章内容</article>
                <originalRequest>扩写要求（可选）</originalRequest>
                %s
                </expand_article>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 扩写技术文章
                <expand_article>
                <article>
                微服务架构是一种软件开发方法。它将应用拆分成多个小服务，
                每个服务独立部署和扩展。这种方式提高了系统的灵活性。
                </article>
                <originalRequest>增加具体的实现案例和技术细节</originalRequest>
                </expand_article>
                
                示例 2: 扩写议论文
                <expand_article>
                <article>
                阅读对个人成长很重要。通过阅读，我们可以获取知识，开阔视野。
                </article>
                </expand_article>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("article") || StringUtils.isBlank(inputJson.get("article").getAsString())) {
                log.error("expand_article 操作缺少必填参数 article");
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

            log.info("开始扩写文章，原文长度: {}", article.length());

            // 调用服务扩写文章
            Flux<String> expandedFlux = writerService.expandArticle(article, originalRequest);

            // 收集所有文本块
            StringBuilder expandedBuilder = new StringBuilder();
            expandedFlux.toStream().forEach(expandedBuilder::append);
            String expandedArticle = expandedBuilder.toString();

            // 设置成功响应
            result.addProperty("result", expandedArticle);
            result.addProperty("original_length", article.length());
            result.addProperty("expanded_length", expandedArticle.length());
            result.addProperty("expansion_ratio", 
                    String.format("%.2f", (double) expandedArticle.length() / article.length()));
            result.addProperty("success", true);

            log.info("成功扩写文章，原文长度: {}, 扩写后长度: {}", 
                    article.length(), expandedArticle.length());

            return result;

        } catch (Exception e) {
            log.error("执行 expand_article 操作时发生异常", e);
            result.addProperty("error", "扩写文章失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
