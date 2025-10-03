package run.mone.hive.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.bo.AgentMarkdownDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MarkdownService单元测试
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@DisplayName("MarkdownService Tests")
class MarkdownServiceTest {

    private MarkdownService markdownService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        markdownService = new MarkdownService();
    }

    @Test
    @DisplayName("测试解析完整的markdown文档")
    void testParseCompleteMarkdown() {
        String markdownContent = """
                # AI助手
                
                ## Profile
                这是一个智能AI助手，专门用于帮助用户解决各种问题。
                
                ## Goal
                提供准确、有用的信息和建议，帮助用户提高工作效率。
                
                ## Constraints
                - 必须提供准确的信息
                - 不能提供有害内容
                - 保持友好和专业的态度
                
                ## Workflow
                1. 理解用户需求
                2. 分析问题
                3. 提供解决方案
                4. 跟进反馈
                
                ## Agent Prompt
                - 你是一个专业的AI助手，请始终保持友好和专业的态度。
                """;

        AgentMarkdownDocument document = markdownService.parseMarkdown(markdownContent);

        assertNotNull(document);
        assertEquals("AI助手", document.getName());
        assertEquals("这是一个智能AI助手，专门用于帮助用户解决各种问题。", document.getProfile());
        assertEquals("提供准确、有用的信息和建议，帮助用户提高工作效率。", document.getGoal());
        assertTrue(document.getConstraints().contains("必须提供准确的信息"));
        assertTrue(document.getWorkflow().contains("理解用户需求"));
        assertEquals("你是一个专业的AI助手，请始终保持友好和专业的态度。", document.getAgentPrompt());
    }

    @Test
    @DisplayName("测试解析中文字段名的markdown文档")
    void testParseChineseFieldNames() {
        String markdownContent = """
                # 数据分析师
                
                ## 简介
                专业的数据分析师角色
                
                ## 目标
                帮助用户进行数据分析
                
                ## 约束条件
                只处理合法数据
                
                ## 工作流程
                收集->分析->报告
                
                ## 代理提示
                你是数据分析专家
                """;

        AgentMarkdownDocument document = markdownService.parseMarkdown(markdownContent);

        assertNotNull(document);
        assertEquals("数据分析师", document.getName());
        assertEquals("专业的数据分析师角色", document.getProfile());
        assertEquals("帮助用户进行数据分析", document.getGoal());
        assertEquals("只处理合法数据", document.getConstraints());
        assertEquals("收集->分析->报告", document.getWorkflow());
        assertEquals("你是数据分析专家", document.getAgentPrompt());
    }

    @Test
    @DisplayName("测试解析空内容")
    void testParseEmptyContent() {
        AgentMarkdownDocument document = markdownService.parseMarkdown("");
        assertNotNull(document);
        assertNull(document.getName());
        assertNull(document.getProfile());
    }

    @Test
    @DisplayName("测试解析null内容")
    void testParseNullContent() {
        AgentMarkdownDocument document = markdownService.parseMarkdown(null);
        assertNotNull(document);
        assertNull(document.getName());
    }

    @Test
    @DisplayName("测试生成markdown内容")
    void testGenerateMarkdown() {
        AgentMarkdownDocument document = AgentMarkdownDocument.builder()
                .name("测试助手")
                .profile("这是一个测试助手")
                .goal("用于测试目的")
                .constraints("仅用于测试")
                .workflow("测试流程")
                .agentPrompt("测试提示")
                .build();

        String markdown = markdownService.generateMarkdown(document);

        assertNotNull(markdown);
        assertTrue(markdown.contains("# 测试助手"));
        assertTrue(markdown.contains("## Profile"));
        assertTrue(markdown.contains("这是一个测试助手"));
        assertTrue(markdown.contains("## Goal"));
        assertTrue(markdown.contains("用于测试目的"));
        assertTrue(markdown.contains("## Constraints"));
        assertTrue(markdown.contains("仅用于测试"));
        assertTrue(markdown.contains("## Workflow"));
        assertTrue(markdown.contains("测试流程"));
        assertTrue(markdown.contains("## Agent Prompt"));
        assertTrue(markdown.contains("测试提示"));
    }

    @Test
    @DisplayName("测试生成空文档的markdown")
    void testGenerateEmptyMarkdown() {
        AgentMarkdownDocument document = AgentMarkdownDocument.builder().build();
        String markdown = markdownService.generateMarkdown(document);
        assertNotNull(markdown);
        assertTrue(markdown.isEmpty() || markdown.trim().isEmpty());
    }

    @Test
    @DisplayName("测试生成null文档的markdown")
    void testGenerateNullMarkdown() {
        String markdown = markdownService.generateMarkdown(null);
        assertEquals("", markdown);
    }

    @Test
    @DisplayName("测试文件读写操作")
    void testFileReadWrite() throws IOException {
        // 创建测试文档
        AgentMarkdownDocument originalDocument = AgentMarkdownDocument.builder()
                .name("文件测试助手")
                .profile("用于测试文件读写功能")
                .goal("验证文件操作正确性")
                .constraints("必须保持数据完整性")
                .workflow("写入->读取->验证")
                .agentPrompt("请确保文件操作正确")
                .build();

        // 写入文件
        Path testFile = tempDir.resolve("test.md");
        markdownService.writeToFile(originalDocument, testFile.toString());

        // 验证文件存在
        assertTrue(Files.exists(testFile));

        // 读取文件
        AgentMarkdownDocument readDocument = markdownService.readFromFile(testFile.toString());

        // 验证内容一致
        assertNotNull(readDocument);
        assertEquals(originalDocument.getName(), readDocument.getName());
        assertEquals(originalDocument.getProfile(), readDocument.getProfile());
        assertEquals(originalDocument.getGoal(), readDocument.getGoal());
        assertEquals(originalDocument.getConstraints(), readDocument.getConstraints());
        assertEquals(originalDocument.getWorkflow(), readDocument.getWorkflow());
        assertEquals(originalDocument.getAgentPrompt(), readDocument.getAgentPrompt());
    }

    @Test
    @DisplayName("测试读取不存在的文件")
    void testReadNonExistentFile() {
        Path nonExistentFile = tempDir.resolve("nonexistent.md");
        
        IOException exception = assertThrows(IOException.class, () -> {
            markdownService.readFromFile(nonExistentFile.toString());
        });
        
        assertTrue(exception.getMessage().contains("文件不存在"));
    }

    @Test
    @DisplayName("测试写入null文档")
    void testWriteNullDocument() {
        Path testFile = tempDir.resolve("null_test.md");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            markdownService.writeToFile(null, testFile.toString());
        });
        
        assertEquals("文档不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试文档验证功能")
    void testDocumentValidation() {
        // 测试有效文档
        AgentMarkdownDocument validDocument = AgentMarkdownDocument.builder()
                .name("有效文档")
                .profile("有效的简介")
                .goal("有效的目标")
                .build();

        MarkdownService.ValidationResult validResult = markdownService.validateDocument(validDocument);
        assertTrue(validResult.isValid());
        assertTrue(validResult.getErrors().isEmpty());

        // 测试无效文档（缺少必需字段）
        AgentMarkdownDocument invalidDocument = AgentMarkdownDocument.builder()
                .profile("只有简介")
                .build();

        MarkdownService.ValidationResult invalidResult = markdownService.validateDocument(invalidDocument);
        assertFalse(invalidResult.isValid());
        assertFalse(invalidResult.getErrors().isEmpty());
        assertTrue(invalidResult.getErrors().get(0).contains("名称字段不能为空"));

        // 测试null文档
        MarkdownService.ValidationResult nullResult = markdownService.validateDocument(null);
        assertFalse(nullResult.isValid());
        assertTrue(nullResult.getErrors().get(0).contains("文档不能为空"));
    }

    @Test
    @DisplayName("测试MarkdownDocument的辅助方法")
    void testMarkdownDocumentHelperMethods() {
        // 测试有效文档
        AgentMarkdownDocument validDocument = AgentMarkdownDocument.builder()
                .name("测试文档")
                .profile("测试简介")
                .goal("测试目标")
                .build();

        assertTrue(validDocument.isValid());
        assertTrue(validDocument.getSummary().contains("测试文档"));
        assertTrue(validDocument.getSummary().contains("测试简介"));

        // 测试无效文档
        AgentMarkdownDocument invalidDocument = AgentMarkdownDocument.builder().build();
        assertFalse(invalidDocument.isValid());
        assertTrue(invalidDocument.getSummary().contains("未设置"));
    }

    @Test
    @DisplayName("测试复杂的markdown解析")
    void testComplexMarkdownParsing() {
        String complexMarkdown = """
                # 复杂助手
                
                ## Profile
                这是一个复杂的助手，包含多行内容。
                它可以处理各种复杂的任务。
                
                支持多种功能：
                - 文本处理
                - 数据分析
                - 代码生成
                
                ## Goal
                主要目标是：
                1. 提供高质量服务
                2. 确保用户满意
                3. 持续改进功能
                
                ## Constraints
                约束条件包括：
                
                **技术约束：**
                - 响应时间 < 5秒
                - 准确率 > 95%
                
                **业务约束：**
                - 符合法律法规
                - 保护用户隐私
                
                ## Workflow
                详细工作流程：
                
                ```
                输入 -> 预处理 -> 分析 -> 处理 -> 输出
                ```
                
                每个步骤都有详细的处理逻辑。
                
                ## Agent Prompt
                系统提示：
                
                你是一个专业的AI助手，具备以下特点：
                - 专业性强
                - 响应迅速
                - 准确可靠
                
                请始终保持这些特点。
                """;

        AgentMarkdownDocument document = markdownService.parseMarkdown(complexMarkdown);

        assertNotNull(document);
        assertEquals("复杂助手", document.getName());
        
        // 验证多行内容被正确解析
        assertTrue(document.getProfile().contains("这是一个复杂的助手"));
        assertTrue(document.getProfile().contains("文本处理"));
        assertTrue(document.getProfile().contains("数据分析"));
        
        assertTrue(document.getGoal().contains("提供高质量服务"));
        assertTrue(document.getGoal().contains("确保用户满意"));
        
        assertTrue(document.getConstraints().contains("技术约束"));
        assertTrue(document.getConstraints().contains("响应时间"));
        
        assertTrue(document.getWorkflow().contains("输入 -> 预处理"));
        assertTrue(document.getWorkflow().contains("每个步骤都有详细"));
        
        assertTrue(document.getAgentPrompt().contains("专业的AI助手"));
        assertTrue(document.getAgentPrompt().contains("专业性强"));
    }

    @Test
    @DisplayName("测试往返转换（解析后再生成）")
    void testRoundTripConversion() throws IOException {
        // 原始文档
        AgentMarkdownDocument originalDocument = AgentMarkdownDocument.builder()
                .name("往返测试")
                .profile("测试往返转换功能")
                .goal("确保数据完整性")
                .constraints("保持原始格式")
                .workflow("解析->生成->验证")
                .agentPrompt("测试系统")
                .build();

        // 生成markdown
        String markdown = markdownService.generateMarkdown(originalDocument);

        // 解析markdown
        AgentMarkdownDocument parsedDocument = markdownService.parseMarkdown(markdown);

        // 验证数据一致性
        assertEquals(originalDocument.getName(), parsedDocument.getName());
        assertEquals(originalDocument.getProfile(), parsedDocument.getProfile());
        assertEquals(originalDocument.getGoal(), parsedDocument.getGoal());
        assertEquals(originalDocument.getConstraints(), parsedDocument.getConstraints());
        assertEquals(originalDocument.getWorkflow(), parsedDocument.getWorkflow());
        assertEquals(originalDocument.getAgentPrompt(), parsedDocument.getAgentPrompt());
    }
}
