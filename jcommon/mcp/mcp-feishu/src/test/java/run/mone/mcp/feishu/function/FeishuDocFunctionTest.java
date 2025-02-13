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
        Map<Integer, String> elements = Map.of(2, "测试内容");

        Map<String, Object> addBlock = new HashMap<>();
        addBlock.put("operation", "addBlock");
        addBlock.put("block", Map.of(
                "docId", documentId, "elements", elements
        ));
        // 执行测试
        McpSchema.CallToolResult addBlockRes = feishuDocFunction.apply(addBlock);

        Map<String, Object> getFilesArg = new HashMap<>();
        getFilesArg.put("operation", "getFiles");
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