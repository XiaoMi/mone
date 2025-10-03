package run.mone.hive.service;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.bo.AgentMarkdownDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Markdown文档处理服务
 * 支持读取和写入指定格式的markdown文档
 * <p>
 * 支持的markdown格式包含以下字段：
 * - name: 名称
 * - profile: 简介
 * - goal: 目标
 * - constraints: 约束条件
 * - workflow: 工作流程
 * - agentPrompt: 代理提示
 */
@Slf4j
public class MarkdownService {

    private final Parser parser;

    // 支持的字段名称映射（支持中英文）
    private static final Map<String, String> FIELD_MAPPINGS = new HashMap<>();

    static {
        FIELD_MAPPINGS.put("name", "name");
        FIELD_MAPPINGS.put("名称", "name");
        FIELD_MAPPINGS.put("profile", "profile");
        FIELD_MAPPINGS.put("简介", "profile");
        FIELD_MAPPINGS.put("goal", "goal");
        FIELD_MAPPINGS.put("目标", "goal");
        FIELD_MAPPINGS.put("constraints", "constraints");
        FIELD_MAPPINGS.put("约束条件", "constraints");
        FIELD_MAPPINGS.put("约束", "constraints");
        FIELD_MAPPINGS.put("workflow", "workflow");
        FIELD_MAPPINGS.put("工作流程", "workflow");
        FIELD_MAPPINGS.put("工作流", "workflow");
        FIELD_MAPPINGS.put("agentprompt", "agentPrompt");
        FIELD_MAPPINGS.put("agent prompt", "agentPrompt");
        FIELD_MAPPINGS.put("代理提示", "agentPrompt");
        FIELD_MAPPINGS.put("提示", "agentPrompt");
    }

    public MarkdownService() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
    }

    /**
     * 从文件路径读取markdown文档
     *
     * @param filePath 文件路径
     * @return MarkdownDocument对象
     * @throws IOException 读取文件失败时抛出
     */
    public AgentMarkdownDocument readFromFile(String filePath) throws IOException {
        log.info("开始读取markdown文件: {}", filePath);

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("文件不存在: " + filePath);
        }

        String content = Files.readString(path);
        return parseMarkdown(content);
    }

    /**
     * 从字符串内容解析markdown文档
     *
     * @param content markdown内容
     * @return MarkdownDocument对象
     */
    public AgentMarkdownDocument parseMarkdown(String content) {
        log.debug("开始解析markdown内容，长度: {}", content != null ? content.length() : 0);

        if (content == null || content.trim().isEmpty()) {
            log.warn("markdown内容为空");
            return AgentMarkdownDocument.builder().build();
        }

        Document document = parser.parse(content);
        return extractFields(document, content);
    }

    /**
     * 将MarkdownDocument写入文件
     *
     * @param document 要写入的文档
     * @param filePath 目标文件路径
     * @throws IOException 写入文件失败时抛出
     */
    public void writeToFile(AgentMarkdownDocument document, String filePath) throws IOException {
        log.info("开始写入markdown文件: {}", filePath);

        if (document == null) {
            throw new IllegalArgumentException("文档不能为空");
        }

        String content = generateMarkdown(document);
        Path path = Paths.get(filePath);

        // 确保父目录存在
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("成功写入markdown文件: {}", filePath);
    }

    /**
     * 将MarkdownDocument转换为markdown字符串
     *
     * @param document 要转换的文档
     * @return markdown格式的字符串
     */
    public String generateMarkdown(AgentMarkdownDocument document) {
        if (document == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // 添加标题
        if (document.getName() != null && !document.getName().trim().isEmpty()) {
            sb.append("# ").append(document.getName()).append("\n\n");
        }

        // 添加各个字段
        appendSection(sb, "## Profile", document.getProfile());
        appendSection(sb, "## Goal", document.getGoal());
        appendSection(sb, "## Constraints", document.getConstraints());
        appendSection(sb, "## Workflow", document.getWorkflow());
        appendSection(sb, "## Agent Prompt", document.getAgentPrompt());

        return sb.toString();
    }

    /**
     * 从解析的文档中提取字段
     */
    private AgentMarkdownDocument extractFields(Document document, String originalContent) {
        AgentMarkdownDocument.AgentMarkdownDocumentBuilder builder = AgentMarkdownDocument.builder();

        // 用于存储当前处理的字段和内容
        String currentField = null;
        StringBuilder currentContent = new StringBuilder();

        // 分行处理，这样可以更好地处理内容
        String[] lines = originalContent.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            // 检查是否是标题行
            if (trimmedLine.startsWith("#")) {
                // 保存之前的字段内容
                if (currentField != null && currentContent.length() > 0) {
                    setFieldValue(builder, currentField, currentContent.toString().trim());
                }

                // 解析新的标题
                String headerText = extractHeaderText(trimmedLine);
                currentField = mapFieldName(headerText);
                currentContent = new StringBuilder();

                // 如果是一级标题，可能是name字段
                if (trimmedLine.startsWith("# ") && currentField == null) {
                    builder.name(headerText);
                }
            } else if (currentField != null && !trimmedLine.isEmpty()) {
                // 添加内容到当前字段
                if (currentContent.length() > 0) {
                    currentContent.append("\n");
                }
                currentContent.append(line);
            }
        }

        // 处理最后一个字段
        if (currentField != null && currentContent.length() > 0) {
            setFieldValue(builder, currentField, currentContent.toString().trim());
        }

        return builder.build();
    }

    /**
     * 提取标题文本
     */
    private String extractHeaderText(String headerLine) {
        return headerLine.replaceFirst("^#+\\s*", "").trim();
    }

    /**
     * 映射字段名称
     */
    private String mapFieldName(String headerText) {
        if (headerText == null) {
            return null;
        }

        String lowerText = headerText.toLowerCase().trim();
        return FIELD_MAPPINGS.get(lowerText);
    }

    /**
     * 设置字段值
     */
    private void setFieldValue(AgentMarkdownDocument.AgentMarkdownDocumentBuilder builder, String fieldName, String value) {
        if (fieldName == null || value == null) {
            return;
        }

        switch (fieldName) {
            case "name":
                builder.name(value);
                break;
            case "profile":
                builder.profile(value);
                break;
            case "goal":
                builder.goal(value);
                break;
            case "constraints":
                builder.constraints(value);
                break;
            case "workflow":
                builder.workflow(value);
                break;
            case "agentPrompt":
                builder.agentPrompt(value);
                break;
            default:
                log.debug("未知字段: {}", fieldName);
        }
    }

    /**
     * 添加章节内容
     */
    private void appendSection(StringBuilder sb, String title, String content) {
        if (content != null && !content.trim().isEmpty()) {
            sb.append(title).append("\n\n");
            sb.append(content).append("\n\n");
        }
    }

    /**
     * 验证markdown文档格式
     *
     * @param document 要验证的文档
     * @return 验证结果，包含错误信息
     */
    public ValidationResult validateDocument(AgentMarkdownDocument document) {
        ValidationResult result = new ValidationResult();

        if (document == null) {
            result.addError("文档不能为空");
            return result;
        }

        if (document.getName() == null || document.getName().trim().isEmpty()) {
            result.addError("名称字段不能为空");
        }

        if (document.getProfile() == null || document.getProfile().trim().isEmpty()) {
            result.addWarning("建议填写简介字段");
        }

        if (document.getGoal() == null || document.getGoal().trim().isEmpty()) {
            result.addWarning("建议填写目标字段");
        }

        return result;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final java.util.List<String> errors = new java.util.ArrayList<>();
        private final java.util.List<String> warnings = new java.util.ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public java.util.List<String> getErrors() {
            return new java.util.ArrayList<>(errors);
        }

        public java.util.List<String> getWarnings() {
            return new java.util.ArrayList<>(warnings);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!errors.isEmpty()) {
                sb.append("错误: ").append(String.join(", ", errors));
            }
            if (!warnings.isEmpty()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append("警告: ").append(String.join(", ", warnings));
            }
            return sb.toString();
        }
    }
}
