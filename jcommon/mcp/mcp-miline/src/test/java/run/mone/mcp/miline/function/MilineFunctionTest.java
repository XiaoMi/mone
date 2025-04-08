package run.mone.mcp.miline.function;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MilineFunctionTest {

    private MilineFunction milineFunction;
    private static final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        milineFunction = new MilineFunction();
    }

    @Test
    void testAddProjectMember() {
        // 准备测试数据
        Map<String, Object> args = new HashMap<>();
        args.put("command", "addMember");
        args.put("projectId", 90707);
        args.put("username", "gaoyulin");

        // 执行测试
        McpSchema.CallToolResult result = milineFunction.apply(args);

        // 打印结果
        System.out.println("Add Project Member Result: " + gson.toJson(result));

    }
} 