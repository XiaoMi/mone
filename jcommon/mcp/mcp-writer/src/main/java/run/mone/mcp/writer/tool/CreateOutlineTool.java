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
 * 创建文章大纲工具
 * <p>
 * 该工具用于根据主题创建详细的文章大纲。
 * 帮助作者在写作前规划好文章结构，明确各部分的要点。
 * <p>
 * 使用场景：
 * - 写作前的规划和准备
 * - 理清文章思路和结构
 * - 团队协作时的内容框架
 * - 大型文章的章节规划
 *
 * @author writer-agent
 * @date 2025/1/16
 */
@Slf4j
@Component
public class CreateOutlineTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "create_outline";

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
                根据给定主题创建详细的文章大纲。
                帮助作者在写作前规划好文章结构，明确各部分的核心要点。
                
                **大纲内容：**
                - 主要章节：文章的主体部分划分
                - 章节要点：各章节需要涵盖的关键内容
                - 逻辑结构：章节之间的逻辑关系
                - 论证思路：如何展开论述
                - 结论方向：文章的总结要点
                
                **大纲特点：**
                - 结构清晰：层次分明，逻辑严密
                - 全面完整：覆盖主题的各个方面
                - 重点突出：明确核心论点和关键内容
                - 可操作：便于后续写作展开
                
                **适用场景：**
                - 长文章写作前的规划
                - 技术文档的架构设计
                - 论文写作的框架搭建
                - 教程和指南的目录规划
                - 团队协作的内容框架
                """;
    }

    @Override
    public String parameters() {
        return """
                - topic: (必填) 文章主题，应清晰明确
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
                <create_outline>
                <topic>文章主题</topic>
                %s
                </create_outline>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 为技术文章创建大纲
                <create_outline>
                <topic>微服务架构设计与实践</topic>
                </create_outline>
                
                示例 2: 为议论文创建大纲
                <create_outline>
                <topic>人工智能对就业市场的影响</topic>
                </create_outline>
                
                示例 3: 为教程创建大纲
                <create_outline>
                <topic>Python机器学习入门教程</topic>
                </create_outline>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("topic") || StringUtils.isBlank(inputJson.get("topic").getAsString())) {
                log.error("create_outline 操作缺少必填参数 topic");
                result.addProperty("error", "缺少必填参数 'topic'");
                return result;
            }

            // 获取参数
            String topic = inputJson.get("topic").getAsString().trim();

            if (topic.isEmpty()) {
                log.warn("topic 参数为空");
                result.addProperty("error", "参数错误：topic不能为空");
                return result;
            }

            log.info("开始创建文章大纲，主题: {}", topic);

            // 调用服务创建大纲
            Flux<String> outlineFlux = writerService.createOutline(topic);

            // 收集所有文本块
            StringBuilder outlineBuilder = new StringBuilder();
            outlineFlux.toStream().forEach(outlineBuilder::append);
            String outline = outlineBuilder.toString();

            // 设置成功响应
            result.addProperty("result", outline);
            result.addProperty("topic", topic);
            result.addProperty("outline_length", outline.length());
            result.addProperty("success", true);

            log.info("成功创建文章大纲，主题: {}, 大纲长度: {}", topic, outline.length());

            return result;

        } catch (Exception e) {
            log.error("执行 create_outline 操作时发生异常", e);
            result.addProperty("error", "创建大纲失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
