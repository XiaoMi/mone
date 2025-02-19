
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
                            "enum": ["closeAllEditors","getCurrentEditorContent","getCurrentEditorClassName"],
                            "description":"The operation to perform on IDEA"
                        },
                        "projectName": {
                            "type": "string",
                            "description":"需要操作的项目，你不应该假设项目名称，如果不知道填什么，请询问用户，否则会有不好的事情发生!"
                        }
                    },
                    "required": ["operation","projectName"]
                }
                """;

        @Override
        public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
            String operation = (String) arguments.get("operation");

            log.info("operation: {}", operation);

            try {
                String result = switch (operation) {
                    case "closeAllEditors" -> closeAllEditors((String) arguments.get("projectName"));
                    case "getCurrentEditorContent" -> getCurrentEditorContent((String) arguments.get("projectName"));
                    case "getCurrentEditorClassName" -> getCurrentEditorClassName((String) arguments.get("projectName"));
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };

                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
            } catch (Exception e) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
            }
        }

        @SneakyThrows
        public String closeAllEditors(String projectName) {
            JsonObject req = new JsonObject();
            req.addProperty("from", "idea_mcp");
            req.addProperty("cmd", "close_all_tab");
            req.addProperty("projectName", projectName);
            new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
            return "All editors closed";
        }

        @SneakyThrows
        public String getCurrentEditorContent(String projectName) {
            JsonObject req = new JsonObject();
            req.addProperty("from", "idea_mcp");
            req.addProperty("cmd", "get_current_editor_content");
            req.addProperty("projectName", projectName);
            JsonObject res = new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
            return res.toString();
        }

        @SneakyThrows
        public String getCurrentEditorClassName(String projectName) {
            JsonObject req = new JsonObject();
            req.addProperty("from", "idea_mcp");
            req.addProperty("cmd", "get_current_editor_class_name");
            req.addProperty("projectName", projectName);
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
                        "operation": {
                            "type": "string",
                            "enum": ["create_unittest_class", "create_unittest_class_method_code"],
                            "description":"create_unittest_class先把测试类创建出来，调用完create_unittest_class之后，隔3s再调用create_unittest_class_method_code,create_unittest_class_method_code是把测试类中的代码生成出来"
                        },
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
            String operation = (String) arguments.get("operation");

            try {
                JsonObject res = switch (operation.toLowerCase()) {
                    case "create_unittest_class" ->
                            createUnitTestClass((String) arguments.get("targetPackage"), (String) arguments.get("projectName"), ideaPort);
                    case "create_unittest_class_method_code" ->
                            createUnitTestClassMethodCode((String) arguments.get("targetPackage"), (String) arguments.get("projectName"), ideaPort);
                    default -> throw new IllegalArgumentException("Unsupported operation type: " + operation);
                };
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), false);
            } catch (Exception e) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
            }
        }

        public JsonObject createUnitTestClass(String targetPackage, String projectName, String ideaPort) {
            JsonObject req = new JsonObject();
            req.addProperty("cmd", "write_code");
            req.addProperty("cmdName", "createUnitTestClassSimple");
            req.addProperty("testPackageName", targetPackage);
            req.addProperty("projectName", projectName);
            req.addProperty("athenaPluginHost", "127.0.0.1:" + ideaPort);
            JsonObject res = callAthena(ideaPort, req);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return res;
        }

        public JsonObject createUnitTestClassMethodCode(String targetPackage, String projectName, String ideaPort) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JsonObject req = new JsonObject();
            req.addProperty("cmd", "write_code");
            req.addProperty("cmdName", "createUnitTestMethodCodeSimple");
            req.addProperty("testPackageName", targetPackage);
            req.addProperty("projectName", projectName);
            req.addProperty("athenaPluginHost", "127.0.0.1:" + ideaPort);
            JsonObject res = callAthena(ideaPort, req);
            return res;
        }
    }

    @SneakyThrows
    public static JsonObject callAthena(String ideaPort, JsonObject req) {
        return new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
    }
}
