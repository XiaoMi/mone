package run.mone.mcp.milinenew.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 扩缩容工单工具类
 * 支持单个和批量扩缩容操作，自动查询流水线信息，支持工单拆分和审批人校验
 */
@Slf4j
@Component
public class ScaleOrderTool implements ITool {

    @Value("${git.email.suffix}")
    private String gitUserName;

    @Value("${scale.order.max.machines.per.order:50}")
    private int maxMachinesPerOrder;

    @Value("${scale.order.routine.review.threshold:15}")
    private int routineReviewThreshold;

    @Value("${scale.order.batch.thread.pool.size:10}")
    private int batchThreadPoolSize;

    @Value("${scale.order.batch.timeout.seconds:30}")
    private int batchTimeoutSeconds;

    @Value("${req_base_url:}")
    private String reqBaseUrl;

    public static final String name = "scale_order";
    
    private String queryDeployCurrentUrl;
    private String getUsersUrl;
    private String createScaleOrderUrl;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private ExecutorService executorService;

    private static final int UP_ROUTINE = 1;
    private static final int UP_SALE = 2;
    private static final int UP_EMERGE = 3;
    private static final int DOWN = 4;

    private static final int LEADER_REVIEW = 2;
    private static final int MONE_REVIEW = 3;
    private static final int SCALE_UP = 4;
    private static final int SCALE_DOWN = 5;
    private static final int MACHINE_DELIVERY = 7;

    private static final int REVIEW_WAIT = 1;
    private static final int SCALE_WAIT = 4;

    public ScaleOrderTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        this.executorService = Executors.newFixedThreadPool(batchThreadPoolSize);
        
        String baseUrl = reqBaseUrl;
        if (StringUtils.isEmpty(baseUrl)) {
            baseUrl = System.getenv("req_base_url");
        }
        
        if (StringUtils.isEmpty(baseUrl)) {
            log.warn("req_base_url 未配置");
            return;
        }
        
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        this.queryDeployCurrentUrl = baseUrl + "/queryDeployCurrent";
        this.getUsersUrl = baseUrl + "/scaleOrder/getUsers";
        this.createScaleOrderUrl = baseUrl + "/scaleOrder";
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
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
                创建Miline扩缩容工单的工具，为指定流水线创建扩缩容工单。
                
                **使用场景：**
                - 需要扩容或缩容流水线的实例数
                - 自动查询当前实例数，无需手动输入
                - 超过配置的最大机器数（默认50台）时自动拆分成多个工单
                - 支持批量操作多个流水线
                
                **执行结果处理：**
                - 成功时：必须使用 attempt_completion 工具呈现最终结果（显示绿色背景），不要重复调用此工具
                - 部分失败：从 results 数组中提取失败的流水线（success=false），重新调用此工具只处理失败的流水线
                
                **配置说明：**
                - 可通过 scale.order.max.machines.per.order 配置每个工单最大机器数（默认50台）
                - 可通过 scale.order.routine.review.threshold 配置日常扩容审批阈值（默认15台）
                """;
    }

    @Override
    public String parameters() {
        return """
                **单个操作：**
                - projectId: (必填) 项目ID
                - pipelineId: (必填) 流水线ID
                - scaleCount: (必填) 扩缩容数量，正数表示扩容，负数表示缩容
                - type: (选填) 工单类型，1=日常扩容, 2=大促扩容, 3=紧急扩容, 4=缩容，默认根据数量自动判断
                - reviewers: (条件必填) 审批人列表，根据工单类型和目标实例数判断是否需要
                  **注意：目标实例数 = 当前实例数 + 扩容数量，判断依据是目标实例数，不是本次扩容数量**
                
                **批量操作：**
                - type: (必填) 工单类型，1=日常扩容, 2=大促扩容, 3=紧急扩容, 4=缩容，所有流水线统一使用此类型
                - reviewers: (条件必填) 审批人列表，所有流水线共享，根据工单类型和目标实例数判断是否需要：
                  - 大促扩容（type=2）：必须3个审批人（业务leader、架构组、SRE）
                  - 紧急扩容（type=3）：必须2个审批人（业务leader、架构组）
                  - 日常扩容（type=1）：**目标实例数 > 15** 时必须2个审批人（业务leader、架构组），<= 15 时无需审批
                    **注意：目标实例数 = 当前实例数 + 扩容数量，不是本次扩容数量**
                  - 缩容（type=4）：无需审批人
                - pipelines: (必填) 流水线列表，每个元素包含：
                  - projectId: (必填) 项目ID
                  - pipelineId: (必填) 流水线ID
                  - scaleCount: (必填) 扩缩容数量，正数表示扩容，负数表示缩容
                - remark: (选填) 备注，所有工单共享
                
                **审批规则（重要）：**
                - **判断依据是目标实例数（当前实例数 + 扩容数量），而不是本次扩容数量**
                - 例如：当前12台，扩容5台，目标17台。虽然本次扩容5台<=15，但目标17台>15，所以需要审批
                - 日常扩容：目标实例数 <= 15 时无需审批，> 15 时需要2个审批人（业务leader + 架构组）
                - 紧急扩容：必须2个审批人（业务leader + 架构组）
                - 大促扩容：必须3个审批人（业务leader + 架构组 + SRE）
                - 缩容：无需审批
                - 审批人必须从系统允许的审批人列表中选择，系统会自动校验
                - **如果目标实例数 <= 15，即使提供了审批人，系统也会忽略，因为不需要审批**
                """;
    }

    @Override
    public String usage() {
        return """
                **单个操作示例（日常扩容，目标实例数<=15，无需审批）：**
                假设当前实例数为5台，扩容10台，目标实例数=15台，无需审批
                <scale_order>
                <projectId>600941</projectId>
                <pipelineId>12345</pipelineId>
                <scaleCount>10</scaleCount>
                </scale_order>
                
                **单个操作示例（日常扩容，目标实例数>15，需要审批）：**
                假设当前实例数为12台，扩容5台，目标实例数=17台>15，需要审批
                <scale_order>
                <projectId>600941</projectId>
                <pipelineId>12345</pipelineId>
                <scaleCount>5</scaleCount>
                <reviewers>
                    <reviewer>
                        <type>2</type>
                        <username>leader_username</username>
                    </reviewer>
                    <reviewer>
                        <type>3</type>
                        <username>mone_username</username>
                    </reviewer>
                </reviewers>
                </scale_order>
                
                **重要提醒：**
                - 判断是否需要审批的依据是"目标实例数"（当前实例数 + 扩容数量），不是"本次扩容数量"
                - 如果目标实例数 <= 15，即使提供了审批人，系统也会忽略，因为不需要审批
                - 如果目标实例数 > 15，必须提供审批人，否则会报错
                
                **单个操作示例（大促扩容，必须审批）：**
                <scale_order>
                <projectId>600941</projectId>
                <pipelineId>12345</pipelineId>
                <scaleCount>100</scaleCount>
                <type>2</type>
                <reviewers>
                    <reviewer>
                        <type>2</type>
                        <username>leader_username</username>
                    </reviewer>
                    <reviewer>
                        <type>3</type>
                        <username>mone_username</username>
                    </reviewer>
                    <reviewer>
                        <type>7</type>
                        <username>sre_username</username>
                    </reviewer>
                </reviewers>
                </scale_order>
                
                **批量操作示例（大促扩容）：**
                <scale_order>
                <type>2</type>
                <reviewers>
                    <reviewer>
                        <type>2</type>
                        <username>leader_username</username>
                    </reviewer>
                    <reviewer>
                        <type>3</type>
                        <username>mone_username</username>
                    </reviewer>
                    <reviewer>
                        <type>7</type>
                        <username>sre_username</username>
                    </reviewer>
                </reviewers>
                <pipelines>
                    <pipeline>
                        <projectId>600941</projectId>
                        <pipelineId>12345</pipelineId>
                        <scaleCount>100</scaleCount>
                    </pipeline>
                    <pipeline>
                        <projectId>600941</projectId>
                        <pipelineId>12346</pipelineId>
                        <scaleCount>50</scaleCount>
                    </pipeline>
                </pipelines>
                </scale_order>
                
                **批量操作示例（缩容，无需审批人）：**
                <scale_order>
                <type>4</type>
                <pipelines>
                    <pipeline>
                        <projectId>600941</projectId>
                        <pipelineId>12345</pipelineId>
                        <scaleCount>-30</scaleCount>
                    </pipeline>
                    <pipeline>
                        <projectId>600941</projectId>
                        <pipelineId>12346</pipelineId>
                        <scaleCount>-20</scaleCount>
                    </pipeline>
                </pipelines>
                </scale_order>
                
                **批量操作失败后的重试示例：**
                如果批量操作中部分流水线失败，可以从返回结果的 results 数组中提取失败的流水线：
                1. 查找 results 数组中 success=false 的项
                2. 提取这些项的 projectId、pipelineId、scaleCount
                3. 重新构建请求，只包含失败的流水线：
                <scale_order>
                <type>1</type>
                <reviewers>
                    <reviewer>
                        <type>2</type>
                        <username>zhangyingwei</username>
                    </reviewer>
                    <reviewer>
                        <type>3</type>
                        <username>zhangyingwei</username>
                    </reviewer>
                </reviewers>
                <pipelines>
                    <pipeline>
                        <projectId>600941</projectId>
                        <pipelineId>12346</pipelineId>
                        <scaleCount>50</scaleCount>
                    </pipeline>
                </pipelines>
                </scale_order>
                注意：只包含失败的流水线，成功的流水线不需要重复处理。
                """;
    }

    @Override
    public String example() {
        return """
                示例1: 扩容50台（日常扩容，需要审批）
                <scale_order>
                <projectId>600941</projectId>
                <pipelineId>12345</pipelineId>
                <scaleCount>50</scaleCount>
                <reviewers>
                    <reviewer>
                        <type>2</type>
                        <username>zhangsan</username>
                    </reviewer>
                    <reviewer>
                        <type>3</type>
                        <username>lisi</username>
                    </reviewer>
                </reviewers>
                </scale_order>
                
                示例2: 缩容30台（无需审批）
                <scale_order>
                <projectId>600941</projectId>
                <pipelineId>12345</pipelineId>
                <scaleCount>-30</scaleCount>
                </scale_order>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (StringUtils.isEmpty(reqBaseUrl) && StringUtils.isEmpty(System.getenv("req_base_url"))) {
                result.addProperty("error", "配置错误: req_base_url 未设置");
                log.error("req_base_url 未设置，无法执行扩缩容操作");
                return result;
            }

            boolean isBatch = false;
            if (inputJson.has("pipelines")) {
                JsonElement pipelinesElement = inputJson.get("pipelines");
                if (pipelinesElement.isJsonArray()) {
                    isBatch = true;
                } else if (pipelinesElement.isJsonPrimitive() && pipelinesElement.getAsJsonPrimitive().isString()) {
                    JsonArray pipelinesArray = parsePipelinesFromString(pipelinesElement.getAsString());
                    if (pipelinesArray != null && pipelinesArray.size() > 0) {
                        inputJson.add("pipelines", pipelinesArray);
                        isBatch = true;
                    }
                }
            }
            
            JsonObject executionResult;
            if (isBatch) {
                executionResult = executeBatch(inputJson);
            } else {
                executionResult = executeSingle(inputJson);
            }
            
            return executionResult;
        } catch (Exception e) {
            log.error("执行扩缩容操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
    }

    private JsonObject executeSingle(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String projectId = getStringValue(inputJson, "projectId", null);
            String pipelineId = getStringValue(inputJson, "pipelineId", null);
            if (StringUtils.isBlank(projectId)) {
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
            if (StringUtils.isBlank(pipelineId)) {
                result.addProperty("error", "缺少必填参数'pipelineId'");
                return result;
            }
            if (!inputJson.has("scaleCount")) {
                result.addProperty("error", "缺少必填参数'scaleCount'");
                return result;
            }

            int scaleCount = inputJson.get("scaleCount").getAsInt();
            if (scaleCount == 0) {
                result.addProperty("error", "扩缩容数量不能为0");
                return result;
            }

            PipelineDeployDto deployDto = queryDeployCurrent(projectId, pipelineId);
            if (deployDto == null) {
                result.addProperty("error", String.format("查询流水线部署信息失败: projectId=%s, pipelineId=%s", projectId, pipelineId));
                return result;
            }

            if (deployDto.getDeployMachines() == null || deployDto.getDeployMachines().isEmpty()) {
                result.addProperty("error", "无法获取流水线实例数：deployMachines为空");
                return result;
            }

            int replicateBefore = deployDto.getDeployMachines().size();
            String env = deployDto.getEnv();
            int replicateAfter = calculateReplicateAfter(replicateBefore, scaleCount);
            int orderType = inputJson.has("type")
                    ? inputJson.get("type").getAsInt()
                    : determineOrderType(scaleCount, replicateAfter);

            List<ReviewerInfo> reviewers = parseReviewers(inputJson);
            try {
                validateAllReviewers(Long.parseLong(projectId), orderType, replicateAfter, reviewers, env);
            } catch (IllegalArgumentException e) {
                result.addProperty("error", e.getMessage());
                return result;
            }

            String remark = getStringValue(inputJson, "remark", "");
            int totalScale = Math.abs(replicateAfter - replicateBefore);
            if (totalScale > maxMachinesPerOrder) {
                return createMultipleOrders(projectId, pipelineId, env, orderType,
                        replicateBefore, replicateAfter, scaleCount > 0, totalScale,
                        reviewers, remark, replicateBefore, replicateAfter, scaleCount);
            } else {
                return createSingleOrder(projectId, pipelineId, env, orderType,
                        replicateBefore, replicateAfter, reviewers, remark);
            }
        } catch (Exception e) {
            log.error("执行单个扩缩容操作时发生异常", e);
            JsonObject errorResult = new JsonObject();
            errorResult.addProperty("error", "执行操作失败: " + e.getMessage());
            return errorResult;
        }
    }

    private JsonObject executeBatch(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (!inputJson.has("type")) {
                result.addProperty("error", "批量操作必须指定工单类型（type）");
                return result;
            }

            int batchOrderType = inputJson.get("type").getAsInt();
            if (!isValidOrderType(batchOrderType)) {
                result.addProperty("error", String.format("无效的工单类型: %d，必须是 1(日常扩容)、2(大促扩容)、3(紧急扩容)、4(缩容)", batchOrderType));
                return result;
            }

            List<ReviewerInfo> sharedReviewers = inputJson.has("reviewers") ? parseReviewers(inputJson) : null;
            if ((batchOrderType == UP_SALE || batchOrderType == UP_EMERGE) 
                    && (sharedReviewers == null || sharedReviewers.isEmpty())) {
                result.addProperty("error", String.format("工单类型 %d 必须指定审批人", batchOrderType));
                return result;
            }

            JsonArray pipelines = inputJson.getAsJsonArray("pipelines");
            if (pipelines == null || pipelines.size() == 0) {
                result.addProperty("error", "批量操作必须提供至少一个流水线");
                return result;
            }

            for (JsonElement element : pipelines) {
                JsonObject pipelineJson = element.getAsJsonObject();
                if (!pipelineJson.has("scaleCount")) {
                    result.addProperty("error", "流水线缺少必填参数'scaleCount'");
                    return result;
                }
                int scaleCount = pipelineJson.get("scaleCount").getAsInt();
                String pipelineId = getStringValue(pipelineJson, "pipelineId", "未知");
                
                if (batchOrderType == DOWN && scaleCount > 0) {
                    result.addProperty("error", String.format("工单类型为缩容（type=4），但流水线 %s 的 scaleCount 为正数", pipelineId));
                    return result;
                }
                if (batchOrderType != DOWN && scaleCount < 0) {
                    result.addProperty("error", String.format("工单类型为扩容（type=%d），但流水线 %s 的 scaleCount 为负数", batchOrderType, pipelineId));
                    return result;
                }
            }

            String sharedRemark = getStringValue(inputJson, "remark", "");
            List<CompletableFuture<ScaleResult>> futures = new ArrayList<>();

            for (JsonElement element : pipelines) {
                JsonObject pipelineJson = element.getAsJsonObject();
                pipelineJson.addProperty("type", batchOrderType);

                if (!pipelineJson.has("reviewers") && sharedReviewers != null) {
                    JsonArray reviewersArray = new JsonArray();
                    for (ReviewerInfo reviewer : sharedReviewers) {
                        JsonObject reviewerJson = new JsonObject();
                        reviewerJson.addProperty("type", reviewer.getType());
                        reviewerJson.addProperty("username", reviewer.getUsername());
                        reviewersArray.add(reviewerJson);
                    }
                    pipelineJson.add("reviewers", reviewersArray);
                }

                if (!pipelineJson.has("remark") && StringUtils.isNotEmpty(sharedRemark)) {
                    pipelineJson.addProperty("remark", sharedRemark);
                }

                JsonObject finalPipelineJson = pipelineJson;
                CompletableFuture<ScaleResult> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return processSinglePipeline(finalPipelineJson, batchOrderType, sharedReviewers);
                    } catch (Exception e) {
                        log.error("处理流水线扩缩容失败", e);
                        return createFailResult(finalPipelineJson, e.getMessage());
                    }
                }, executorService);

                futures.add(future);
            }

            return collectBatchResults(futures, pipelines);

        } catch (Exception e) {
            log.error("执行批量扩缩容操作时发生异常", e);
            result.addProperty("error", "执行批量操作失败: " + e.getMessage());
            return result;
        }
    }

    private ScaleResult processSinglePipeline(JsonObject pipelineJson, int batchOrderType, List<ReviewerInfo> sharedReviewers) {
        String projectId = pipelineJson.get("projectId").getAsString();
        String pipelineId = pipelineJson.get("pipelineId").getAsString();
        int scaleCount = pipelineJson.get("scaleCount").getAsInt();

        try {
            PipelineDeployDto deployDto = queryDeployCurrent(projectId, pipelineId);
            if (deployDto == null) {
                return ScaleResult.fail(projectId, pipelineId, 
                        String.format("查询流水线部署信息失败: projectId=%s, pipelineId=%s", projectId, pipelineId));
            }

            if (deployDto.getDeployMachines() == null || deployDto.getDeployMachines().isEmpty()) {
                return ScaleResult.fail(projectId, pipelineId, "无法获取流水线实例数：deployMachines为空");
            }

            int replicateBefore = deployDto.getDeployMachines().size();
            String env = deployDto.getEnv();
            int replicateAfter = calculateReplicateAfter(replicateBefore, scaleCount);
            List<ReviewerInfo> reviewers = parseReviewers(pipelineJson);
            if (reviewers == null || reviewers.isEmpty()) {
                reviewers = sharedReviewers;
            }

            if (batchOrderType == UP_ROUTINE && replicateAfter > routineReviewThreshold) {
                validateAllReviewers(Long.parseLong(projectId), batchOrderType, replicateAfter, reviewers, env);
            } else if (batchOrderType != UP_ROUTINE) {
                validateAllReviewers(Long.parseLong(projectId), batchOrderType, replicateAfter, reviewers, env);
            }

            int totalScale = Math.abs(replicateAfter - replicateBefore);
            String remark = getStringValue(pipelineJson, "remark", "");

            if (totalScale > maxMachinesPerOrder) {
                return createMultipleOrdersResult(projectId, pipelineId, env, batchOrderType,
                        replicateBefore, replicateAfter, scaleCount > 0, totalScale, reviewers, remark,
                        replicateBefore, replicateAfter, scaleCount);
            } else {
                int orderId = createSingleOrderInternal(projectId, pipelineId, env, batchOrderType,
                        replicateBefore, replicateAfter, reviewers, remark);
                return ScaleResult.success(projectId, pipelineId, 1, List.of(orderId),
                        "成功创建扩缩容工单", replicateBefore, replicateAfter, scaleCount);
            }
        } catch (Exception e) {
            log.error("处理流水线扩缩容失败: projectId={}, pipelineId={}", projectId, pipelineId, e);
            return ScaleResult.fail(projectId, pipelineId, e.getMessage());
        }
    }

    private Request.Builder buildRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "OkHttp");
    }

    private <T> T executePostRequest(String url, Object requestBody, Class<T> responseType) 
            throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        return executePostRequest(url, requestBody, 
                objectMapper.getTypeFactory().constructType(responseType));
    }

    private <T> T executePostRequest(String url, Object requestBody, 
                                     com.fasterxml.jackson.databind.JavaType responseType) 
            throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = buildRequestBuilder(url).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(String.format("HTTP请求失败: %s, status code: %d", url, response.code()));
            }

            String responseBody = response.body().string();
            ApiResponse<T> apiResponse = objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, responseType)
            );

            if (apiResponse.getCode() != 0) {
                throw new IOException(String.format("API错误: %s, message: %s", url, apiResponse.getMessage()));
            }

            return apiResponse.getData();
        }
    }

    private PipelineDeployDto queryDeployCurrent(String projectId, String pipelineId) {
        try {
            if (StringUtils.isEmpty(queryDeployCurrentUrl)) {
                return null;
            }
            List<Object> requestBody = List.of(projectId, pipelineId, "0");
            return executePostRequest(queryDeployCurrentUrl, requestBody, PipelineDeployDto.class);
        } catch (Exception e) {
            log.error("查询流水线部署信息异常: projectId={}, pipelineId={}", projectId, pipelineId, e);
            return null;
        }
    }

    private void validateReviewerInAllowedList(long projectId, int reviewerType, String username, String env) {
        try {
            if (StringUtils.isEmpty(getUsersUrl)) {
                throw new RuntimeException("getUsersUrl 未初始化");
            }
            
            List<Object> requestBody = List.of((int)projectId, reviewerType, env);
            List<AccountInfo> allowedUsers = executePostRequest(getUsersUrl, requestBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AccountInfo.class));
            
            if (allowedUsers == null || allowedUsers.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("类型 %d 的审批人列表为空，无法验证审批人", reviewerType));
            }

            boolean found = allowedUsers.stream()
                    .anyMatch(user -> username.equals(user.getUserName()));

            if (!found) {
                String allowedUsernames = allowedUsers.stream()
                        .map(AccountInfo::getUserName)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException(
                        String.format("审批人 '%s' 不在允许的列表中。允许的审批人: %s",
                                username, allowedUsernames));
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("校验审批人失败: projectId={}, type={}, username={}", projectId, reviewerType, username, e);
            throw new RuntimeException("校验审批人失败: " + e.getMessage(), e);
        }
    }

    private void validateAllReviewers(long projectId, int orderType, int replicateAfter,
                                     List<ReviewerInfo> reviewers, String env) {
        boolean needReview = needReview(orderType, replicateAfter);
        if (!needReview) {
            return;
        }

        if (reviewers == null || reviewers.isEmpty()) {
            throw new IllegalArgumentException("需要审批但未提供审批人");
        }

        List<Integer> requiredTypes = new ArrayList<>();
        if (orderType == UP_SALE) {
            requiredTypes.add(LEADER_REVIEW);
            requiredTypes.add(MONE_REVIEW);
            requiredTypes.add(MACHINE_DELIVERY);
        } else if (orderType == UP_EMERGE) {
            requiredTypes.add(LEADER_REVIEW);
            requiredTypes.add(MONE_REVIEW);
        } else if (orderType == UP_ROUTINE && replicateAfter > routineReviewThreshold) {
            requiredTypes.add(LEADER_REVIEW);
            requiredTypes.add(MONE_REVIEW);
        }

        for (Integer requiredType : requiredTypes) {
            ReviewerInfo reviewer = reviewers.stream()
                    .filter(r -> r.getType() == requiredType)
                    .findFirst()
                    .orElse(null);

            if (reviewer == null) {
                String typeName = getReviewerTypeName(requiredType);
                throw new IllegalArgumentException(
                        String.format("缺少 %s（type=%d）审批人", typeName, requiredType));
            }

            if (StringUtils.isBlank(reviewer.getUsername())) {
                throw new IllegalArgumentException("审批人用户名不能为空");
            }

            validateReviewerInAllowedList(projectId, requiredType, reviewer.getUsername(), env);
        }
    }

    private int calculateReplicateAfter(int replicateBefore, int scaleCount) {
        if (scaleCount > 0) {
            return replicateBefore + scaleCount;
        } else {
            return Math.max(1, replicateBefore - Math.abs(scaleCount)); // 缩容时至少保留1台机器
        }
    }

    private boolean needReview(int orderType, int replicateAfter) {
        if (orderType == UP_SALE || orderType == UP_EMERGE) {
            return true;  // 大促扩容和紧急扩容必须审批
        } else if (orderType == UP_ROUTINE) {
            // 日常扩容：目标实例数超过阈值（默认15台）需要审批
            return replicateAfter > routineReviewThreshold;
        }
        return false;  // 缩容和日常扩容（目标实例数<=阈值）不需要审批
    }

    private int determineOrderType(int scaleCount, int replicateAfter) {
        if (scaleCount < 0) {
            return DOWN;  // 缩容
        }

        // 扩容：根据数量判断，使用配置的阈值
        if (scaleCount > maxMachinesPerOrder) {
            return UP_SALE;  // 大促扩容
        } else if (scaleCount > routineReviewThreshold) {
            return UP_EMERGE;  // 紧急扩容
        } else {
            return UP_ROUTINE;  // 日常扩容
        }
    }

    private boolean isValidOrderType(int orderType) {
        return orderType == UP_ROUTINE || orderType == UP_SALE || orderType == UP_EMERGE || orderType == DOWN;
    }

    private String getReviewerTypeName(int type) {
        switch (type) {
            case LEADER_REVIEW:
                return "业务leader审核";
            case MONE_REVIEW:
                return "架构组审核";
            case MACHINE_DELIVERY:
                return "SRE交付审核";
            default:
                return "未知类型(" + type + ")";
        }
    }

    private List<ReviewerInfo> parseReviewers(JsonObject inputJson) {
        if (inputJson.has("reviewer")) {
            JsonElement reviewerElement = inputJson.get("reviewer");
            if (reviewerElement.isJsonPrimitive() && reviewerElement.getAsJsonPrimitive().isString()) {
                List<ReviewerInfo> result = parseReviewersFromString(reviewerElement.getAsString());
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
            if (reviewerElement.isJsonObject()) {
                ReviewerInfo reviewer = parseSingleReviewer(reviewerElement.getAsJsonObject());
                if (reviewer != null) {
                    return List.of(reviewer);
                }
            }
        }
        
        if (inputJson.has("reviewers")) {
            JsonElement reviewersElement = inputJson.get("reviewers");
            if (reviewersElement.isJsonArray()) {
                List<ReviewerInfo> result = parseReviewersFromArray(reviewersElement.getAsJsonArray());
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
            if (reviewersElement.isJsonPrimitive() && reviewersElement.getAsJsonPrimitive().isString()) {
                List<ReviewerInfo> result = parseReviewersFromString(reviewersElement.getAsString());
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
            if (reviewersElement.isJsonObject()) {
                JsonObject reviewersObj = reviewersElement.getAsJsonObject();
                if (reviewersObj.has("reviewer")) {
                    JsonElement reviewerElement = reviewersObj.get("reviewer");
                    if (reviewerElement.isJsonPrimitive() && reviewerElement.getAsJsonPrimitive().isString()) {
                        List<ReviewerInfo> result = parseReviewersFromString(reviewerElement.getAsString());
                        if (result != null && !result.isEmpty()) {
                            return result;
                        }
                    }
                    if (reviewerElement.isJsonObject()) {
                        ReviewerInfo reviewer = parseSingleReviewer(reviewerElement.getAsJsonObject());
                        if (reviewer != null) {
                            return List.of(reviewer);
                        }
                    }
                }
            }
        }
        
        List<ReviewerInfo> reviewers = findReviewersRecursively(inputJson);
        if (reviewers != null && !reviewers.isEmpty()) {
            return reviewers;
        }
        
        return null;
    }
    
    private List<ReviewerInfo> findReviewersRecursively(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            
            // 检查是否有reviewer或reviewers字段
            if (obj.has("reviewer")) {
                JsonElement reviewerElement = obj.get("reviewer");
                if (reviewerElement.isJsonPrimitive() && reviewerElement.getAsJsonPrimitive().isString()) {
                    List<ReviewerInfo> result = parseReviewersFromString(reviewerElement.getAsString());
                    if (result != null && !result.isEmpty()) {
                        return result;
                    }
                } else if (reviewerElement.isJsonObject()) {
                    ReviewerInfo reviewer = parseSingleReviewer(reviewerElement.getAsJsonObject());
                    if (reviewer != null) {
                        return List.of(reviewer);
                    }
                }
            }
            
            if (obj.has("reviewers")) {
                JsonElement reviewersElement = obj.get("reviewers");
                if (reviewersElement.isJsonArray()) {
                    return parseReviewersFromArray(reviewersElement.getAsJsonArray());
                } else if (reviewersElement.isJsonPrimitive() && reviewersElement.getAsJsonPrimitive().isString()) {
                    return parseReviewersFromString(reviewersElement.getAsString());
                } else if (reviewersElement.isJsonObject()) {
                    // 递归查找
                    return findReviewersRecursively(reviewersElement);
                }
            }
            
            // 递归查找所有子对象
            for (String key : obj.keySet()) {
                if (key.toLowerCase().contains("review")) {
                    List<ReviewerInfo> result = findReviewersRecursively(obj.get(key));
                    if (result != null && !result.isEmpty()) {
                        return result;
                    }
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                List<ReviewerInfo> result = findReviewersRecursively(item);
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
        }
        
        return null;
    }

    private List<ReviewerInfo> parseReviewersFromArray(JsonArray reviewersArray) {
        List<ReviewerInfo> reviewers = new ArrayList<>();
        for (JsonElement element : reviewersArray) {
            if (element.isJsonObject()) {
                JsonObject reviewerJson = element.getAsJsonObject();
                ReviewerInfo reviewer = parseSingleReviewer(reviewerJson);
                if (reviewer != null) {
                    reviewers.add(reviewer);
                }
            }
        }
        return reviewers.isEmpty() ? null : reviewers;
    }

    private List<ReviewerInfo> parseReviewersFromString(String reviewersStr) {
        if (StringUtils.isBlank(reviewersStr)) {
            return null;
        }
        
        List<ReviewerInfo> reviewers = new ArrayList<>();
        List<String> contents = extractXmlTagContents(reviewersStr, "reviewer");
        
        if (!contents.isEmpty()) {
            for (String content : contents) {
                ReviewerInfo reviewer = parseReviewerFromXmlString(content);
                if (reviewer != null) {
                    reviewers.add(reviewer);
                }
            }
            if (!reviewers.isEmpty()) {
                return reviewers;
            }
        }
        
        String[] reviewerStrings = reviewersStr.split(Pattern.quote(":·:"));
        for (String reviewerStr : reviewerStrings) {
            reviewerStr = reviewerStr.trim();
            ReviewerInfo reviewer = null;
            
            List<String> innerContents = extractXmlTagContents(reviewerStr, "reviewer");
            if (!innerContents.isEmpty()) {
                reviewer = parseReviewerFromXmlString(innerContents.get(0));
            } else {
                reviewer = parseReviewerFromXmlString(reviewerStr);
            }
            
            if (reviewer != null) {
                reviewers.add(reviewer);
            }
        }
        
        return reviewers.isEmpty() ? null : reviewers;
    }

    private ReviewerInfo parseReviewerFromXmlString(String reviewerStr) {
        if (StringUtils.isBlank(reviewerStr)) {
            return null;
        }
        
        String typeStr = extractXmlTagValue(reviewerStr, "type");
        String username = extractXmlTagValue(reviewerStr, "username");
        
        if (typeStr == null || username == null) {
            return null;
        }
        
        try {
            int type = Integer.parseInt(typeStr);
            if (type > 0 && StringUtils.isNotBlank(username)) {
                ReviewerInfo reviewer = new ReviewerInfo();
                reviewer.setType(type);
                reviewer.setUsername(username);
                return reviewer;
            }
        } catch (NumberFormatException e) {
            // 忽略解析失败
        }
        
        return null;
    }

    private ReviewerInfo parseSingleReviewer(JsonObject reviewerJson) {
        ReviewerInfo reviewer = new ReviewerInfo();
        
        if (reviewerJson.has("type")) {
            reviewer.setType(reviewerJson.get("type").getAsInt());
        }
        
        if (reviewerJson.has("username")) {
            reviewer.setUsername(reviewerJson.get("username").getAsString());
        }
        
        // 验证必要字段
        if (reviewer.getType() > 0 && StringUtils.isNotBlank(reviewer.getUsername())) {
            return reviewer;
        }
        
        return null;
    }

    private JsonObject createSingleOrder(String projectId, String pipelineId, String env, int orderType,
                                         int replicateBefore, int replicateAfter,
                                         List<ReviewerInfo> reviewers, String remark) {
        try {
            int orderId = createSingleOrderInternal(projectId, pipelineId, env, orderType,
                    replicateBefore, replicateAfter, reviewers, remark);

            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("projectId", projectId);
            result.addProperty("pipelineId", pipelineId);
            result.addProperty("orderId", orderId);
            result.addProperty("totalOrders", 1);
            result.addProperty("result", String.format(
                    "成功创建扩缩容工单！\n工单ID: %d\n项目ID: %s\n流水线ID: %s\n当前实例数: %d\n目标实例数: %d\n扩缩容数量: %d台",
                    orderId, projectId, pipelineId, replicateBefore, replicateAfter,
                    Math.abs(replicateAfter - replicateBefore)));

            return result;
        } catch (Exception e) {
            log.error("创建单个工单失败", e);
            JsonObject result = new JsonObject();
            result.addProperty("error", "创建工单失败: " + e.getMessage());
            return result;
        }
    }

    private int createSingleOrderInternal(String projectId, String pipelineId, String env, int orderType,
                                         int replicateBefore, int replicateAfter,
                                         List<ReviewerInfo> reviewers, String remark) throws Exception {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", gitUserName);
        userMap.put("userType", 0);
        
        Map<String, Object> orderDto = buildScaleOrderRequest(projectId, pipelineId, env, orderType,
                replicateBefore, replicateAfter, remark, reviewers, gitUserName);

        List<Object> requestBody = List.of(userMap, orderDto);
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);

        RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.parse("application/json; charset=utf-8")
        );

        if (StringUtils.isEmpty(createScaleOrderUrl)) {
            throw new RuntimeException("createScaleOrderUrl 未初始化");
        }
        Request request = buildRequestBuilder(createScaleOrderUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            ApiResponse<Integer> apiResponse = objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Integer.class)
            );

            if (apiResponse.getCode() != 0) {
                throw new Exception("API error: " + apiResponse.getMessage());
            }

            return apiResponse.getData();
        }
    }

    private JsonObject createMultipleOrders(String projectId, String pipelineId, String env,
                                           int orderType, int replicateBefore, int replicateAfter,
                                           boolean isScaleUp, int totalScale,
                                           List<ReviewerInfo> reviewers, String remark,
                                           int originalReplicateBefore, int originalReplicateAfter, int originalScaleCount) {
        return createMultipleOrdersResult(projectId, pipelineId, env, orderType,
                replicateBefore, replicateAfter, isScaleUp, totalScale, reviewers, remark,
                originalReplicateBefore, originalReplicateAfter, originalScaleCount)
                .toJsonObject();
    }

    private ScaleResult createMultipleOrdersResult(String projectId, String pipelineId, String env,
                                                  int orderType, int replicateBefore, int replicateAfter,
                                                  boolean isScaleUp, int totalScale,
                                                  List<ReviewerInfo> reviewers, String remark,
                                                  int originalReplicateBefore, int originalReplicateAfter, int originalScaleCount) {
        int batches = (totalScale + maxMachinesPerOrder - 1) / maxMachinesPerOrder;
        List<Integer> orderIds = new ArrayList<>();
        StringBuilder resultMsg = new StringBuilder();

        try {
            for (int i = 0; i < batches; i++) {
                int batchSize = Math.min(maxMachinesPerOrder, totalScale - i * maxMachinesPerOrder);
                int batchReplicateBefore = isScaleUp
                        ? replicateBefore + i * maxMachinesPerOrder
                        : replicateBefore - i * maxMachinesPerOrder;

                int batchReplicateAfter = calculateReplicateAfter(batchReplicateBefore, isScaleUp ? batchSize : -batchSize);
                
                if (!isScaleUp && batchReplicateAfter < 1) {
                    throw new IllegalArgumentException(String.format(
                            "缩容后实例数不能小于1台。批次 %d/%d：当前实例数 %d，缩容数量 %d，缩容后 %d",
                            i + 1, batches, batchReplicateBefore, batchSize, batchReplicateAfter));
                }

                String batchRemark = String.format("批次 %d/%d，每批最多%d台", i + 1, batches, maxMachinesPerOrder);
                if (StringUtils.isNotEmpty(remark)) {
                    batchRemark = remark + "；" + batchRemark;
                }

                int orderId = createSingleOrderInternal(projectId, pipelineId, env, orderType,
                        batchReplicateBefore, batchReplicateAfter, reviewers, batchRemark);

                orderIds.add(orderId);
                resultMsg.append(String.format("工单%d: ID=%d, %s%d台 (%d → %d)\n",
                        i + 1, orderId, isScaleUp ? "扩容" : "缩容", batchSize,
                        batchReplicateBefore, batchReplicateAfter));
            }

            return ScaleResult.success(projectId, pipelineId, batches, orderIds,
                    String.format("成功创建%d个扩缩容工单（每批最多%d台）\n%s",
                            batches, maxMachinesPerOrder, resultMsg.toString()),
                    originalReplicateBefore, originalReplicateAfter, originalScaleCount);
        } catch (Exception e) {
            log.error("创建多个工单失败", e);
            return ScaleResult.fail(projectId, pipelineId, "创建工单失败: " + e.getMessage());
        }
    }

    private Map<String, Object> buildScaleOrderRequest(String projectId, String pipelineId, String env,
                                                      int orderType, int replicateBefore, int replicateAfter,
                                                      String remark, List<ReviewerInfo> reviewers, String creator) {
        Map<String, Object> orderDto = new HashMap<>();
        orderDto.put("projectId", Long.parseLong(projectId));
        orderDto.put("pipelineId", Long.parseLong(pipelineId));
        orderDto.put("env", env);
        orderDto.put("type", orderType);
        orderDto.put("creator", creator);
        orderDto.put("remark", StringUtils.defaultString(remark));

        List<Map<String, Object>> progresses = new ArrayList<>();
        boolean needReview = needReview(orderType, replicateAfter);

        if (needReview && reviewers != null && !reviewers.isEmpty()) {
            List<Integer> requiredTypes = new ArrayList<>();
            requiredTypes.add(LEADER_REVIEW);
            requiredTypes.add(MONE_REVIEW);
            if (orderType == UP_SALE) {
                requiredTypes.add(MACHINE_DELIVERY);
            }

            for (Integer requiredType : requiredTypes) {
                reviewers.stream()
                        .filter(r -> r.getType() == requiredType)
                        .findFirst()
                        .ifPresent(reviewer -> {
                            Map<String, Object> reviewProgress = new HashMap<>();
                            reviewProgress.put("type", reviewer.getType());
                            reviewProgress.put("username", reviewer.getUsername());
                            reviewProgress.put("status", REVIEW_WAIT);
                            progresses.add(reviewProgress);
                        });
            }
        }

        if (orderType == UP_SALE || orderType == UP_EMERGE) {
            Map<String, Object> scaleUpProgress = new HashMap<>();
            scaleUpProgress.put("type", SCALE_UP);
            scaleUpProgress.put("status", SCALE_WAIT);
            progresses.add(scaleUpProgress);

            Map<String, Object> scaleDownProgress = new HashMap<>();
            scaleDownProgress.put("type", SCALE_DOWN);
            scaleDownProgress.put("status", SCALE_WAIT);
            progresses.add(scaleDownProgress);
        }

        orderDto.put("progresses", progresses);

        Map<String, Object> content = new HashMap<>();
        content.put("replicateBefore", replicateBefore);
        content.put("replicateAfter", replicateAfter);
        content.put("num", Math.abs(replicateAfter - replicateBefore));
        orderDto.put("content", content);

        return orderDto;
    }

    private JsonObject collectBatchResults(List<CompletableFuture<ScaleResult>> futures, JsonArray pipelines) {
        List<ScaleResult> results = waitForAllFutures(futures, pipelines);
        return buildBatchResultJson(results);
    }

    private List<ScaleResult> waitForAllFutures(List<CompletableFuture<ScaleResult>> futures, JsonArray pipelines) {
        List<ScaleResult> results = new ArrayList<>();
        
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<ScaleResult> future = futures.get(i);
            try {
                results.add(future.get(batchTimeoutSeconds, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                log.error("流水线处理超时", e);
                results.add(createFailResult(pipelines.get(i).getAsJsonObject(), "处理超时"));
            } catch (Exception e) {
                log.error("等待任务完成时出错", e);
                results.add(createFailResult(pipelines.get(i).getAsJsonObject(), "处理失败: " + e.getMessage()));
            }
        }
        
        return results;
    }

    private ScaleResult createFailResult(JsonObject pipelineJson, String error) {
        String projectId = pipelineJson.has("projectId") ? pipelineJson.get("projectId").getAsString() : "未知";
        String pipelineId = pipelineJson.has("pipelineId") ? pipelineJson.get("pipelineId").getAsString() : "未知";
        return ScaleResult.fail(projectId, pipelineId, error);
    }

    private JsonObject buildBatchResultJson(List<ScaleResult> results) {
        JsonObject result = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        int successCount = 0;
        int failCount = 0;
        int totalOrders = 0;

        for (ScaleResult scaleResult : results) {
            if (scaleResult.isSuccess()) {
                successCount++;
                totalOrders += scaleResult.getTotalOrders();
            } else {
                failCount++;
            }
            resultsArray.add(buildSingleResultJson(scaleResult));
        }

        String summary = buildBatchSummary(results, successCount, failCount, totalOrders);

        result.addProperty("totalPipelines", results.size());
        result.addProperty("successCount", successCount);
        result.addProperty("failCount", failCount);
        result.addProperty("totalOrders", totalOrders);
        result.add("results", resultsArray);
        result.addProperty("result", summary);
        result.addProperty("success", failCount == 0);

        return result;
    }

    private JsonObject buildSingleResultJson(ScaleResult scaleResult) {
        JsonObject item = new JsonObject();
        item.addProperty("projectId", scaleResult.getProjectId());
        item.addProperty("pipelineId", scaleResult.getPipelineId());
        item.addProperty("success", scaleResult.isSuccess());

        if (scaleResult.isSuccess()) {
            item.addProperty("totalOrders", scaleResult.getTotalOrders());
            JsonArray orderIdsArray = new JsonArray();
            for (Integer orderId : scaleResult.getOrderIds()) {
                orderIdsArray.add(orderId);
            }
            item.add("orderIds", orderIdsArray);
            item.addProperty("message", scaleResult.getMessage());
        } else {
            item.addProperty("error", scaleResult.getError());
        }

        return item;
    }

    private String buildBatchSummary(List<ScaleResult> results, int successCount, int failCount, int totalOrders) {
        StringBuilder summary = new StringBuilder();
        if (failCount == 0) {
            summary.append(String.format("已成功为 %d 个流水线创建扩缩容工单：\n\n", successCount));
            summary.append("⚠️ **重要：工单已创建完成，请勿重复调用此工具！**\n");
            summary.append("**下一步：请使用 attempt_completion 工具向用户呈现最终结果（这样才能显示绿色背景）。**\n\n");
        } else {
            summary.append(String.format("批量处理完成（成功: %d 个，失败: %d 个）：\n\n", successCount, failCount));
        }

        for (ScaleResult scaleResult : results) {
            String detailInfo = String.format("项目%s，流水线%s", 
                    scaleResult.getProjectId(), scaleResult.getPipelineId());
            
            if (scaleResult.isSuccess()) {
                if (scaleResult.getTotalOrders() > 1) {
                    summary.append(String.format("✓ %s - 已拆分成 %d 个工单:\n",
                            detailInfo, scaleResult.getTotalOrders()));
                    summary.append(String.format("  - 工单ID: %s\n",
                            scaleResult.getOrderIds().stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "))));
                } else {
                    summary.append(String.format("✓ %s:\n", detailInfo));
                    summary.append(String.format("  - 工单ID: %s\n", scaleResult.getOrderIds().get(0)));
                }
                
                if (scaleResult.getScaleCount() != 0) {
                    String scaleType = scaleResult.getScaleCount() > 0 ? "扩容" : "缩容";
                    summary.append(String.format("  - %s%d台机器（当前实例数%d台 → 目标实例数%d台）\n",
                            scaleType, Math.abs(scaleResult.getScaleCount()),
                            scaleResult.getReplicateBefore(), scaleResult.getReplicateAfter()));
                }
            } else {
                summary.append(String.format("✗ %s:\n", detailInfo));
                summary.append(String.format("  - 错误: %s\n", scaleResult.getError()));
            }
            summary.append("\n");
        }

        return summary.toString();
    }

    @Override
    public String formatResult(JsonObject result) {
        if (result.has("error")) {
            return "错误: " + result.get("error").getAsString();
        }

        if (result.has("result")) {
            return result.get("result").getAsString();
        }

        if (result.has("success")) {
            boolean allSuccess = result.get("success").getAsBoolean();
            StringBuilder sb = new StringBuilder();
            
            if (result.has("totalPipelines")) {
                // 批量操作结果
                int totalPipelines = result.get("totalPipelines").getAsInt();
                int successCount = result.get("successCount").getAsInt();
                int failCount = result.get("failCount").getAsInt();
                
                sb.append("批量处理结果：\n");
                sb.append(String.format("- 总流水线数: %d\n", totalPipelines));
                sb.append(String.format("- 成功: %d 个\n", successCount));
                sb.append(String.format("- 失败: %d 个\n", failCount));
                
                if (result.has("totalOrders")) {
                    sb.append(String.format("- 总工单数: %d 个\n\n", result.get("totalOrders").getAsInt()));
                }
                
                // 如果有失败，明确提示 Agent 可以重试失败的流水线
                if (failCount > 0 && result.has("results")) {
                    sb.append("\n⚠️ 部分流水线处理失败，可以重试失败的流水线：\n");
                    sb.append("失败的流水线信息在 results 数组中（success=false 的项），\n");
                    sb.append("可以提取这些流水线的 projectId、pipelineId 和 scaleCount，\n");
                    sb.append("重新调用此工具，只处理失败的流水线。\n");
                    sb.append("\n失败的流水线详情：\n");
                    
                    JsonArray results = result.getAsJsonArray("results");
                    for (int i = 0; i < results.size(); i++) {
                        JsonObject item = results.get(i).getAsJsonObject();
                        if (!item.has("success") || !item.get("success").getAsBoolean()) {
                            sb.append(String.format("- 项目 %s，流水线 %s：%s\n",
                                    item.has("projectId") ? item.get("projectId").getAsString() : "未知",
                                    item.has("pipelineId") ? item.get("pipelineId").getAsString() : "未知",
                                    item.has("error") ? item.get("error").getAsString() : "未知错误"));
                        }
                    }
                }
                
                if (allSuccess) {
                    sb.append("\n✅ 所有流水线处理成功，任务完成！");
                    sb.append("\n\n⚠️ **重要：工单已创建完成，请勿重复调用此工具！必须立即使用 attempt_completion 工具呈现最终结果（显示绿色背景）。**");
                }
            } else if (result.has("orderId")) {
                sb.append(String.format("成功创建工单，ID: %d", result.get("orderId").getAsInt()));
                sb.append("\n\n⚠️ **重要：工单已创建完成，请勿重复调用此工具！必须立即使用 attempt_completion 工具呈现最终结果（显示绿色背景）。**");
            }
            
            return sb.toString();
        }

        return result.toString();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
        private String detailMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PipelineDeployDto {
        private String env;
        private DeploySetting deploySetting;
        private List<DeployMachine> deployMachines;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeployMachine {
        private String ip;
        private String name;
        private String hostname;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeploySetting {
        private long dockerReplicate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ReviewerInfo {
        private int type;
        private String username;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AccountInfo {
        private String name;
        private String userName;
    }

    @Data
    private static class ScaleResult {
        private String projectId;
        private String pipelineId;
        private boolean success;
        private int totalOrders;
        private List<Integer> orderIds;
        private String message;
        private String error;
        private int replicateBefore;
        private int replicateAfter;
        private int scaleCount;

        public static ScaleResult success(String projectId, String pipelineId, int totalOrders,
                                         List<Integer> orderIds, String message, 
                                         int replicateBefore, int replicateAfter, int scaleCount) {
            ScaleResult result = new ScaleResult();
            result.projectId = projectId;
            result.pipelineId = pipelineId;
            result.success = true;
            result.totalOrders = totalOrders;
            result.orderIds = orderIds;
            result.message = message;
            result.replicateBefore = replicateBefore;
            result.replicateAfter = replicateAfter;
            result.scaleCount = scaleCount;
            return result;
        }

        public static ScaleResult fail(String projectId, String pipelineId, String error) {
            ScaleResult result = new ScaleResult();
            result.projectId = projectId;
            result.pipelineId = pipelineId;
            result.success = false;
            result.error = error;
            return result;
        }

        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("success", success);
            json.addProperty("projectId", projectId);
            json.addProperty("pipelineId", pipelineId);
            if (success) {
                json.addProperty("totalOrders", totalOrders);
                JsonArray orderIdsArray = new JsonArray();
                for (Integer orderId : orderIds) {
                    orderIdsArray.add(orderId);
                }
                json.add("orderIds", orderIdsArray);
                json.addProperty("result", message);
            } else {
                json.addProperty("error", error);
            }
            return json;
        }
    }

    private JsonArray parsePipelinesFromString(String pipelinesStr) {
        if (StringUtils.isBlank(pipelinesStr)) {
            return null;
        }
        
        try {
            JsonArray pipelinesArray = new JsonArray();
            List<String> contents = extractXmlTagContents(pipelinesStr, "pipeline");
            
            for (String pipelineContent : contents) {
                JsonObject pipelineJson = new JsonObject();
                
                String projectId = extractXmlTagValue(pipelineContent, "projectId");
                if (projectId != null) {
                    pipelineJson.addProperty("projectId", projectId);
                }
                
                String pipelineId = extractXmlTagValue(pipelineContent, "pipelineId");
                if (pipelineId != null) {
                    pipelineJson.addProperty("pipelineId", pipelineId);
                }
                
                String scaleCountStr = extractXmlTagValue(pipelineContent, "scaleCount");
                if (scaleCountStr != null) {
                    try {
                        pipelineJson.addProperty("scaleCount", Integer.parseInt(scaleCountStr));
                    } catch (NumberFormatException e) {
                        // 忽略解析失败
                    }
                }
                
                if (pipelineJson.has("projectId") && pipelineJson.has("pipelineId") && pipelineJson.has("scaleCount")) {
                    pipelinesArray.add(pipelineJson);
                }
            }
            
            return pipelinesArray.size() > 0 ? pipelinesArray : null;
        } catch (Exception e) {
            log.error("解析 pipelines 字符串失败", e);
            return null;
        }
    }

    private List<String> extractXmlTagContents(String xmlStr, String tagName) {
        List<String> contents = new ArrayList<>();
        if (StringUtils.isBlank(xmlStr)) {
            return contents;
        }
        
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(xmlStr);
        while (matcher.find()) {
            contents.add(matcher.group(1).trim());
        }
        return contents;
    }

    private String extractXmlTagValue(String xmlStr, String tagName) {
        if (StringUtils.isBlank(xmlStr)) {
            return null;
        }
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(xmlStr);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String getStringValue(JsonObject json, String key, String defaultValue) {
        return json.has(key) ? json.get(key).getAsString() : defaultValue;
    }

}

