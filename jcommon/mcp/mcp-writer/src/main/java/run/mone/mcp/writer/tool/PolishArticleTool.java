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
 * 润色文章工具
 * <p>
 * 该工具用于对现有文章进行润色，提升文章的文笔、表达和整体质量。
 * 通过优化用词、改进句式、增强文采，使文章更加优雅和专业。
 * <p>
 * 使用场景：
 * - 提升文章文笔和表达
 * - 优化语言风格
 * - 增强文章专业性
 * - 改进文章流畅度
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class PolishArticleTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "polish_article";

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
                对现有文章进行润色，提升文章的文笔、表达和整体质量。
                通过优化用词、改进句式、增强文采，使文章更加优雅专业。
                
                **润色重点：**
                - 优化用词：选用更精准、优雅的词汇
                - 改进句式：使句子更加流畅自然
                - 增强文采：提升文章的艺术性和感染力
                - 统一风格：保持文章整体风格的一致性
                - 消除冗余：删除啰嗦和重复的表达
                
                **适用场景：**
                - 初稿完成后的精修
                - 提升文章专业水平
                - 优化文章表达效果
                - 准备发布前的最后润色
                
                **重要提示：**
                - 润色会保留文章的核心内容和结构
                - 主要改进语言表达和文笔
                - 不改变文章的主要观点和论证逻辑
                """;
    }

    @Override
    public String parameters() {
        return """
                - article: (必填) 需要润色的文章内容
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
                <polish_article>
                <article>需要润色的文章内容</article>
                %s
                </polish_article>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 润色一篇技术文章
                <polish_article>
                <article>
                人工智能是很重要的技术。它可以做很多事情，比如识别图片、理解语言等。
                现在很多公司都在用人工智能，它能帮助公司提高效率。
                </article>
                </polish_article>
                
                示例 2: 润色一篇散文
                <polish_article>
                <article>
                秋天到了，树叶变黄了，风吹过来很凉爽。我想起了小时候的事情，
                那时候我经常在树下玩耍，感觉很快乐。
                </article>
                </polish_article>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("article") || StringUtils.isBlank(inputJson.get("article").getAsString())) {
                log.error("polish_article 操作缺少必填参数 article");
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

            log.info("开始润色文章，文章长度: {}", article.length());

            // 调用服务润色文章
            Flux<String> polishedFlux = writerService.polishArticle(article);

            // 收集所有文本块
            StringBuilder polishedBuilder = new StringBuilder();
            polishedFlux.toStream().forEach(polishedBuilder::append);
            String polishedArticle = polishedBuilder.toString();

            // 设置成功响应
            result.addProperty("result", polishedArticle);
            result.addProperty("original_length", article.length());
            result.addProperty("polished_length", polishedArticle.length());
            result.addProperty("success", true);

            log.info("成功润色文章，原文长度: {}, 润色后长度: {}", article.length(), polishedArticle.length());

            return result;

        } catch (Exception e) {
            log.error("执行 polish_article 操作时发生异常", e);
            result.addProperty("error", "润色文章失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
