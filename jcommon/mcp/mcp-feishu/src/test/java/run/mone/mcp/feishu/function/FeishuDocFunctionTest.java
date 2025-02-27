package run.mone.mcp.feishu.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lark.oapi.service.docx.v1.enums.BlockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.feishu.model.*;
import run.mone.mcp.feishu.service.FeishuDocService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
class FeishuDocFunctionTest {

    @Mock
    private FeishuDocService docService;

    private FeishuDocFunction feishuDocFunction;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        docService = new FeishuDocService();
        feishuDocFunction = new FeishuDocFunction(docService, objectMapper);
    }


    @Test
    void testMcp() {
        Gson gson = new Gson();
        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "createDocument");
        args.put("title", "mcp333");

        // 创建文件
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);
        McpSchema.TextContent first = (McpSchema.TextContent) result.content().get(0);

        DocContent docContent = gson.fromJson(first.text(), DocContent.class);

        String documentId = docContent.getDocumentId();

        // 准备请求参数
        List<DocBlock.Element> elements = new ArrayList<>();
        
        // 添加标题
        DocBlock.Element titleElement = new DocBlock.Element()
                .setType(3)  // 一级标题
                .setContent("倒拔石榴树的小故事");
        elements.add(titleElement);
        
        // 添加正文内容
        DocBlock.Element contentElement = new DocBlock.Element()
                .setType(2)  // 文本块
                .setContent("在一个阳光明媚的午后，小明来到了爷爷的果园。一棵硕果累累的石榴树吸引了他的目光。他跃跃欲试，想要摘取那鲜红的果实。可是，树太高了，他够不着。\n\n" +
                        "突发奇想的小明决定使出倒拔石榴树的绝招。他双手紧握树干，用尽全身力气向上拔。汗水湿透了衣衫，可树纹丝不动。正在这时，爷爷笑眯眯地走来，轻轻摇了摇树枝，几颗饱满的石榴便落入小明怀中。\n\n" +
                        "爷爷慈祥地说：孩子，有时候，聪明比蛮力更重要。小明恍然大悟，脸上泛起了幸福的笑容。这个下午，祖孙俩一边品尝甜美的石榴，一边畅聊人生的智慧。");
        elements.add(contentElement);

        Map<String, Object> addBlock = new HashMap<>();
        addBlock.put("operation", "addBlock");
        addBlock.put("block", Map.of(
                "docId", documentId, 
                "elements", elements
        ));
        
        // 执行测试
        McpSchema.CallToolResult addBlockRes = feishuDocFunction.apply(addBlock);

        // 获取文件信息
        Map<String, Object> getFilesArg = new HashMap<>();
        getFilesArg.put("operation", "getFileInfo");
        getFilesArg.put("documentId", documentId);
        McpSchema.CallToolResult files = feishuDocFunction.apply(getFilesArg);
        McpSchema.TextContent content = (McpSchema.TextContent) files.content().get(0);
        List<Files> filesList = gson.fromJson(content.text(), new TypeToken<List<Files>>() {
        });
        List<Files> list = filesList.stream().filter(file -> file.getToken().equals(documentId)).toList();

        System.out.println(list);

    }

    @Test
    void testCreateDocument() throws Exception {
        // 准备请求参数
        Map<String, Object> createDoc = new HashMap<>();
        createDoc.put("operation", "createDocument");
        createDoc.put("title", "测试文档1");

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(createDoc);

    }

    @Test
    void testAddBlock() throws Exception {
        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "addBlock");
        args.put("documentId", "doc_123");
        args.put("block", Map.of(
                "blockType", "text",
                "content", Map.of("text", "测试内容")
        ));

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isError());
    }

    @Test
    void testUpdateBlock() throws Exception {
        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "updateBlock");
        args.put("documentId", "doc_123");
        args.put("block", Map.of(
                "blockId", "block_123",
                "blockType", "text",
                "content", Map.of("text", "更新的内容")
        ));

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isError());
    }

    @Test
    void testSetPermission() throws Exception {
        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "setPermission");
        args.put("permission", Map.of(
                "documentId", "doc_123",
                "userId", "user_123",
                "userType", "user",
                "perm", "edit"
        ));

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isError());
    }

    @Test
    void testGetDocument() throws Exception {
        // 准备测试数据
        DocContent mockDoc = new DocContent()
                .setDocumentId("doc_123")
                .setTitle("测试文档")
                .setCreateTime(1234567890L)
                .setUpdateTime(1234567890L)
                .setCreator("user_123")
                .setOwner("user_123");

        // 设置mock行为
        when(docService.getDocument(anyString())).thenReturn(mockDoc);

        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "getDocument");
        args.put("documentId", "doc_123");

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isError());
    }

    @Test
    void testInvalidOperation() {
        // 准备请求参数
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "invalidOperation");

        // 执行测试
        McpSchema.CallToolResult result = feishuDocFunction.apply(args);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isError());
    }
} 