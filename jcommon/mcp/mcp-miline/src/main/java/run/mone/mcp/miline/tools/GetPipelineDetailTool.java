package run.mone.mcp.miline.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 查询流水线详情工具
 * <p>
 * 此工具用于根据流水线ID查询流水线的详细信息，包括：
 * - 流水线基本信息
 * - 流水线配置
 * - 流水线状态
 * <p>
 * 使用场景：
 * - 查看流水线的当前配置
 * - 检查流水线的运行状态
 * - 获取流水线的详细信息用于分析
 *
 * @author generated
 * @date 2025-11-13
 */
@Slf4j
public class GetPipelineDetailTool implements ITool {

    public static final String name = "get_pipeline_detail";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_PIPELINE_DETAIL_URL = BASE_URL + "/pipeline/detail";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GetPipelineDetailTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

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
                根据项目ID和流水线ID查询流水线的详细信息。

                **使用场景：**
                - 查看流水线的当前配置
                - 检查流水线的运行状态
                - 获取流水线的详细信息用于分析或调试
                - 了解流水线的步骤和参数配置

                **功能特性：**
                - 返回完整的流水线配置信息
                - 包含流水线的所有元数据
                - 支持快速查询和响应

                **重要说明：**
                - 需要提供有效的流水线ID
                - 返回的信息为JSON格式
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - pipelineId: (必填) 流水线ID
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                Checklist here (optional)
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <get_pipeline_detail>
                <projectId>项目ID</projectId>
                <pipelineId>流水线ID</pipelineId>
                %s
                </get_pipeline_detail>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询流水线详情
                <get_pipeline_detail>
                <projectId>123465</projectId>
                <pipelineId>456789</pipelineId>
                </get_pipeline_detail>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                log.error("get_pipeline_detail操作缺少必填参数projectId");
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
            if (!inputJson.has("pipelineId") || StringUtils.isBlank(inputJson.get("pipelineId").getAsString())) {
                log.error("get_pipeline_detail操作缺少必填参数pipelineId");
                result.addProperty("error", "缺少必填参数'pipelineId'");
                return result;
            }

            Long projectId = Long.parseLong(inputJson.get("projectId").getAsString());
            Long pipelineId = Long.parseLong(inputJson.get("pipelineId").getAsString());

            log.info("开始查询流水线详情，projectId: {}, pipelineId: {}", projectId, pipelineId);

            // 查询流水线详情
            JsonObject pipelineDetail = getPipelineDetail(projectId, pipelineId);

            if (pipelineDetail != null) {
                // 直接返回流水线详情的JsonObject
                return pipelineDetail;
            } else {
                result.addProperty("error", "未找到流水线详情");
                return result;
            }

        } catch (NumberFormatException e) {
            log.error("ID格式不正确", e);
            result.addProperty("error", "ID必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行get_pipeline_detail操作时发生异常", e);
            result.addProperty("error", "查询流水线详情失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 查询流水线详情
     *
     * @param projectId  项目ID
     * @param pipelineId 流水线ID
     * @return 流水线详情JsonObject
     * @throws Exception 查询异常
     */
    private JsonObject getPipelineDetail(Long projectId, Long pipelineId) throws Exception {

        // 构建请求体
        List<Object> requestBody = List.of(projectId, pipelineId);

        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("getPipelineDetail request: {}", requestBodyStr);

        RequestBody body = RequestBody.create(
            requestBodyStr,
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(GET_PIPELINE_DETAIL_URL)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            log.info("getPipelineDetail response: {}", responseBody);

            // 解析API响应
            ApiResponse<Map<String, Object>> apiResponse = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructParametricType(
                    ApiResponse.class,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
                )
            );

            if (apiResponse.getCode() != 0) {
                throw new Exception("API error: " + apiResponse.getMessage());
            }

            // 将Map转换为JsonObject
            JsonObject result = new JsonObject();
            Map<String, Object> data = apiResponse.getData();

            if (data != null) {
                // 转换为JSON字符串再解析为JsonObject
                String dataJson = objectMapper.writeValueAsString(data);
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                result = parser.parse(dataJson).getAsJsonObject();

                // 添加成功标识
                result.addProperty("success", true);
                result.addProperty("message", "成功查询流水线详情");
            } else {
                result.addProperty("success", false);
                result.addProperty("error", "流水线详情数据为空");
            }

            return result;
        }
    }

    /**
     * API响应封装
     */
    @Data
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
    }
}
