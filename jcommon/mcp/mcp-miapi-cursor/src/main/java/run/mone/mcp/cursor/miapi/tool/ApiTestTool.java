package run.mone.mcp.cursor.miapi.tool;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.http.HttpClient;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ApiTestTool implements ITool {

    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();
    @Value("${miapi.host}")
    private String host;

    @Override
    public String getName() {
        return "test_api";
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
                1.只进行测试接口功能，不做其他回答。
                2.首先根据提供的接口查询接口信息并生成curl，进行接口测试。
                3.等待用户提供完整所需请求参数和host后，生成一段curl代码并返回。
                4.不要自己臆想所需的必要参数。
                5.如果所需参数缺少，则询问用户提供，并等待用户输入，一直到参数齐全为止。
                6.生成的curl注意引号和反引号的使用，注意书写格式，生成的curl能够在java程序中运行。
                """;
    }

    @Override
    public String parameters() {
        return """
                - apiType: 要测试的接口类型，http类型接口为1(默认值)，dubbo类型接口为3.
                - url: 要测试的接口地址或接口path.
                - params: 要测试接口的请求参数.
                - host: 要测试接口的请求域名或ip.
                - curl_code:根据以上条件生成curl代码
                """;
    }

    @Override
    public String usage() {
        return """
               (Attention: If you are using this tool, you must return the test result within the json):
                   
               Example 1: Get system overview
               ```json
                test result
               ```
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String curl_code = inputJson.has("curl_code") ? inputJson.get("curl_code").getAsString() : null;
            result.addProperty("result",executeCurl(curl_code));
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取接口信息失败: " + e.getMessage());
            return result;
        }
    }

    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
        );
    }

    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : "";
    }

    private String executeCurl(String curlCommand) {
        if (StringUtils.isEmpty(curlCommand)) {
            return "请输入curl_code";
        }
        StringBuilder result = new StringBuilder();
        try {
            List<String> command = parseCurlCommand(curlCommand);

            // 创建ProcessBuilder对象
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 读取进程的输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                result.append("Curl command exited with code ").append(exitCode);
            }

        } catch (Exception e) {
            result.append("Error executing curl command: ").append(e.getMessage());
        }

        return result.toString();
    }

    private List<String> parseCurlCommand(String curlCommand) {
        List<String> command = new ArrayList<>();

        Pattern pattern = Pattern.compile("(?<=^|\\s)'([^']*)'|\"([^\"]*)\"|([^\\s]+)");
        Matcher matcher = pattern.matcher(curlCommand);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                command.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                command.add(matcher.group(2));
            } else {
                command.add(matcher.group(3));
            }
        }

        return command;
    }
}
