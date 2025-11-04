package run.mone.mcp.cursor.miapi.function;

import com.google.gson.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.http.HttpClient;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@Component
public class MiApiFunction implements McpFunction {
    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();

    @Value("${miapi.host}")
    private String host;

    @Override
    public String getName() {
        return "stream_test_api";
    }

    @Override
    public String getDesc() {
        return """
                1.主要是测试接口功能，根据提供的数据使用find_detail查询接口信息并生成curl，进行接口测试。
                3.等待用户提供完整所需参数后，生成一段curl代码并返回。
                4.不要自己臆想所需的必要参数。
                5.如果所需参数缺少，则询问用户提供，并等待用户输入，一直到参数齐全为止。
                6.生成的curl注意引号和反引号的使用，注意书写格式，生成的curl能够在java程序中运行。
                6.curl生成完毕后，将curl代码传入execute_curl方法中并执行execute_curl方法。
                """;
    }

    @Override
    public String getToolScheme() {
        return """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["find_detail","execute_curl"],
                        "description": "操作类型,find_detail获取接口信息，execute_curl执行curl方法"
                    },
                    "apiType": {
                        "type": "string",
                        "enum": ["http","dubbo"],
                        "description": "要测试的接口类型，http类型接口为1，dubbo类型接口为3"
                    },
                    "url": {
                        "type": "string",
                        "description": "要测试的接口地址或接口path"
                    },
                    "params": {
                        "type": "string",
                        "description": "要测试接口的请求参数"
                    },
                    "host":{
                        "type": "string",
                        "description": "要测试接口的请求域名或ip"
                    },
                    "curl_code":{
                        "type": "string",
                        "description": "根据以上条件生成curl代码"
                    }
                  },
                "required": ["type", "apiType", "url", "params", "host", "curl_code"]
            }
            """;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                String type = getStringParam(args, "type");

                String result = switch (type.toLowerCase()) {
                    case "find_detail" -> getApiDetail(getStringParam(args, "apiType"), getStringParam(args, "url"));
//                    case "get_params" -> getStringParam(args, "params");
//                    case "get_host" -> getStringParam(args, "host");
                    case "execute_curl" -> executeCurl(getStringParam(args, "curl_code"));
                    default -> throw new IllegalArgumentException("不支持的操作类型: " + type);
                };

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("执行失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true));
            }
        });
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

    public String getApiDetail(String type, String url) {
        if (StringUtils.isEmpty(type) || url == null || StringUtils.isEmpty(url)) {
            return "error: Parameters cannot be empty";
        }
        JsonObject requestParams = new JsonObject();
        requestParams.add("path", new JsonPrimitive(url));
        if (type.equalsIgnoreCase("dubbo")) {
            requestParams.add("protocol", new JsonPrimitive(3));
        } else {
            requestParams.add("protocol", new JsonPrimitive(1));
        }
        return getApiDetailMiApi(requestParams);
    }

    private JsonElement getRequestType(Integer type) {
        String prefix = "post";
        switch (type){
            case 0:
                prefix = "post";
                break;
            case 2:
                prefix = "put";
                break;
            case 3:
                prefix = "delete";
                break;
            case 4:
                prefix = "head";
                break;
            case 5:
                prefix = "opts";
                break;
            case 6:
                prefix = "patch";
                break;
            default:
                prefix = "get";
                break;
        }
        return new JsonPrimitive(prefix);
    }

    public String getApiDetailMiApi(JsonObject reqParamsBo) {
        String result = "";
        try {
            JsonObject detail = httpClient.post(host + "/OpenApi/mcp/detail", reqParamsBo);
            JsonObject dataObject = detail.getAsJsonObject("data");
            JsonArray headerInfoArray = dataObject.getAsJsonArray("headerInfo");
            JsonObject baseInfoObject = dataObject.getAsJsonObject("baseInfo");

            JsonObject apiInfo = new JsonObject();
            apiInfo.add("headers", headerInfoArray);
            apiInfo.add("url", baseInfoObject.get("apiURI"));
//            apiInfo.add("apiRequestRaw", baseInfoObject.get("apiRequestRaw"));
            apiInfo.add("method", getRequestType(baseInfoObject.get("apiRequestType").getAsInt()));
            result = gson.toJson(apiInfo);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
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
