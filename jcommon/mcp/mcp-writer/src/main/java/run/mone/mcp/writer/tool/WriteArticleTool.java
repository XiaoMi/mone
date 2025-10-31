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

import java.util.HashMap;
import java.util.Map;

/**
 * 写新文章工具
 * <p>
 * 该工具用于根据主题创建一篇全新的文章。支持多种文体格式，
 * 包括散文、诗歌、小说片段、技术文档、作文、周报等。
 * <p>
 * 使用场景：
 * - 创建博客文章
 * - 写作周报和总结
 * - 生成创意内容
 * - 撰写技术文档
 * - 创作小说片段
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class WriteArticleTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "write_article";

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
                根据给定主题创建一篇全新的文章。该工具支持多种文体和写作风格，
                能够根据用户需求生成专业、优质的文章内容。
                
                **支持的文体：**
                - 散文：优美流畅的叙事性文章
                - 诗歌：富有韵律和意境的诗歌创作
                - 小说片段：生动有趣的故事情节
                - 技术文档：清晰准确的技术说明
                - 作文：规范的议论文、记叙文等
                - 周报：专业的工作总结报告
                
                **写作特点：**
                - 内容丰富详实，逻辑清晰
                - 语言优美流畅，表达准确
                - 结构完整，层次分明
                - 根据主题自动选择合适的写作风格
                - 支持长文本生成
                
                **重要提示：**
                - topic参数为必填，应明确具体
                - 可以通过originalRequest提供更详细的需求说明
                - 生成的文章长度和深度会根据主题复杂度自动调整
                """;
    }

    @Override
    public String parameters() {
        return """
                - topic: (必填) 文章主题，应清晰明确
                - originalRequest: (可选) 用户的原始需求说明，可以包含对文体、风格、长度等的具体要求
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
                <write_article>
                <topic>文章主题</topic>
                <originalRequest>详细需求说明（可选）</originalRequest>
                %s
                </write_article>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 写一篇关于人工智能的技术文章
                <write_article>
                <topic>人工智能在医疗领域的应用</topic>
                <originalRequest>需要一篇技术文档，介绍AI在医疗诊断、药物研发等方面的应用案例</originalRequest>
                </write_article>
                
                示例 2: 写一篇散文
                <write_article>
                <topic>秋天的回忆</topic>
                <originalRequest>写一篇优美的散文，表达对秋天的感受和童年回忆</originalRequest>
                </write_article>
                
                示例 3: 写工作周报
                <write_article>
                <topic>本周工作总结</topic>
                <originalRequest>写一份专业的周报，包括本周完成的任务、遇到的问题和下周计划</originalRequest>
                </write_article>
                
                示例 4: 写小说片段
                <write_article>
                <topic>未来世界的冒险</topic>
                <originalRequest>写一段科幻小说的开头，描述主角在未来城市的第一天</originalRequest>
                </write_article>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("topic") || StringUtils.isBlank(inputJson.get("topic").getAsString())) {
                log.error("write_article 操作缺少必填参数 topic");
                result.addProperty("error", "缺少必填参数 'topic'");
                return result;
            }

            // 获取参数
            String topic = inputJson.get("topic").getAsString().trim();
            String originalRequest = inputJson.has("originalRequest") 
                    ? inputJson.get("originalRequest").getAsString().trim() 
                    : null;

            if (topic.isEmpty()) {
                log.warn("topic 参数为空");
                result.addProperty("error", "参数错误：topic不能为空");
                return result;
            }

            log.info("开始写作文章，主题: {}", topic);

            // 准备参数
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("originalRequest", originalRequest);
            if (role != null) {
                arguments.put("role", role);
            }

            // 调用服务写文章
            Flux<String> articleFlux = writerService.writeNewArticle(topic, arguments);

            // 收集所有文本块
            StringBuilder articleBuilder = new StringBuilder();
            articleFlux.toStream().forEach(articleBuilder::append);
            String article = articleBuilder.toString();

            // 设置成功响应
            result.addProperty("result", article);
            result.addProperty("topic", topic);
            result.addProperty("success", true);

            log.info("成功生成文章，主题: {}, 文章长度: {}", topic, article.length());

            return result;

        } catch (Exception e) {
            log.error("执行 write_article 操作时发生异常", e);
            result.addProperty("error", "写作文章失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
