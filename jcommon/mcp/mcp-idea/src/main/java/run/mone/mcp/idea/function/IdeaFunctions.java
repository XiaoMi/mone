
package run.mone.mcp.idea.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.http.HttpClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class IdeaFunctions {

//    public static String ideaPort = "6666";


    @Data
    public static class IdeaOperationFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        public IdeaOperationFunction(String port) {
            this.ideaPort = port;
        }

        private String name = "ideaOperation";

        private String desc = "IDEA operations including closing all editors and reading current editor content";
        private String ideaPort;

        private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "operation": {
                            "type": "string",
                            "enum": ["closeAllEditors", "getCurrentEditorContent"],
                            "description":"The operation to perform on IDEA"
                        }
                    },
                    "required": ["operation"]
                }
                """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
            String operation = (String) arguments.get("operation");

            log.info("operation: {}", operation);

            try {
                String result = switch (operation) {
                    case "closeAllEditors" -> closeAllEditors();
                    case "getCurrentEditorContent" -> getCurrentEditorContent();
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };

                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
            } catch (Exception e) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
            }
        }

        @SneakyThrows
        public String closeAllEditors() {
            JsonObject req = new JsonObject();
            req.addProperty("cmd", "close_all_tab");
            new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
            return "All editors closed";
        }

        @SneakyThrows
        public String getCurrentEditorContent() {
            JsonObject req = new JsonObject();
            req.addProperty("cmd", "get_current_editor_content");
            JsonObject res = new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
            return res.toString();
        }
    }


    @Data
    public static class TestGenerationFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        public TestGenerationFunction(String port) {
            this.ideaPort = port;
        }

        private String name = "testGeneration";

        private String desc = "为项目下一个类生成单元测试类和测试方法";
        private String ideaPort;

        private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "projectName": {
                            "type": "string",
                            "description":"需要生成测试的项目"
                        },
                        "targetPackage": {
                            "type": "string",
                            "description":"测试内容生成到指定的包路径下"
                        }
                    },
                    "required": ["projectName","targetPackage"]
                }
                """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
            String projectName = (String) arguments.get("projectName");
            String targetPackage = (String) arguments.get("targetPackage");
            log.info("projectName: {}", projectName);
            log.info("targetPackage: {}", targetPackage);

            try {
                JsonObject req = new JsonObject();
                req.addProperty("cmd", "write_code");
                req.addProperty("cmdName", "createUnitTestClassAndMethod");
                req.addProperty("testPackageName", targetPackage);
                req.addProperty("projectName", projectName);
                req.addProperty("athenaPluginHost", "127.0.0.1:"+ideaPort);
                JsonObject res = callAthena(ideaPort, req);

                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), false);
            } catch (Exception e) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
            }
        }


    }

    @SneakyThrows
    public static JsonObject callAthena(String ideaPort, JsonObject req) {
        return new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
    }
}
