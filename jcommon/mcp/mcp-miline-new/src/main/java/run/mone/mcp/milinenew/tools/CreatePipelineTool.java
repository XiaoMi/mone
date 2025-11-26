package run.mone.mcp.milinenew.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 创建流水线工具
 * 用于在Miline平台为指定项目创建CI/CD流水线
 *
 * @author liguanchen
 * @date 2025/11/18
 */
@Slf4j
public class CreatePipelineTool implements ITool {
    @Value("${git.email.suffix}")
    private String gitUserName;

    public static String gitName = "";
    public static final String name = "create_pipeline";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String CREATE_PIPELINE_URL = BASE_URL + "/createPipeline";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public CreatePipelineTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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
                创建Miline流水线的工具，为指定项目创建包含编译和部署阶段的完整CI/CD流水线。
                
                **使用场景：**
                - 为新项目创建标准的CI/CD流水线
                - 配置Maven项目的编译和K8s部署流程
                - 自动化构建和部署配置
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - pipelineName: (必填) 流水线名称
                - gitUrl: (必填) Git仓库地址
                - gitName: (必填) 项目名称，默认为git项目名
                - gitBranch: (选填) Git分支，默认为master
                - env: (选填) 环境，默认为staging
                - pipelineCname: (选填) 流水线中文名称
                - desc: (选填) 流水线描述
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
                <create_pipeline>
                <projectId>项目ID</projectId>
                <pipelineName>流水线名称</pipelineName>
                <gitUrl>Git仓库地址</gitUrl>
                <gitName>项目名称，默认为git项目名</gitName>
                <gitBranch>分支名(可选，默认master)</gitBranch>
                <env>环境(可选，默认staging)</env>
                %s
                </create_pipeline>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例: 创建流水线
                <create_pipeline>
                <projectId>600941</projectId>
                <pipelineName>流水线-测试</pipelineName>
                <gitUrl>https://git.n.xiaomi.com/cefe/lgc.git</gitUrl>
                <gitBranch>master</gitBranch>
                <env>staging</env>
                </create_pipeline>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            // 验证必填参数
            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
            if (!inputJson.has("pipelineName") || StringUtils.isBlank(inputJson.get("pipelineName").getAsString())) {
                result.addProperty("error", "缺少必填参数'pipelineName'");
                return result;
            }
            if (!inputJson.has("gitUrl") || StringUtils.isBlank(inputJson.get("gitUrl").getAsString())) {
                result.addProperty("error", "缺少必填参数'gitUrl'");
                return result;
            }

            // 解析参数
            gitName = inputJson.has("gitName") && StringUtils.isNotBlank(inputJson.get("gitName").getAsString())
                    ? inputJson.get("gitName").getAsString() : "";
            String projectId = inputJson.get("projectId").getAsString();
            String pipelineName = inputJson.get("pipelineName").getAsString();
            pipelineName = new File(pipelineName).getName();
            String gitUrl = inputJson.get("gitUrl").getAsString();
            String gitBranch = inputJson.has("gitBranch") && StringUtils.isNotBlank(inputJson.get("gitBranch").getAsString())
                    ? inputJson.get("gitBranch").getAsString() : "master";
            String env = inputJson.has("env") && StringUtils.isNotBlank(inputJson.get("env").getAsString())
                    ? inputJson.get("env").getAsString() : "staging";
            String pipelineCname = inputJson.has("pipelineCname") ? inputJson.get("pipelineCname").getAsString() : "";
            String desc = inputJson.has("desc") ? inputJson.get("desc").getAsString() : "";

            // 解析Git URL获取git地址、组和项目名
            String[] gitParts = parseGitUrl(gitUrl);
            String gitAddress = gitParts[0];
            String gitGroup = gitParts[1];
            String gitProject = gitParts[2];

            // 构建流水线配置
            Map<String, Object> pipelineConfig = buildPipelineConfig(
                    projectId, pipelineName, pipelineCname, desc,
                    gitUrl, gitBranch, gitAddress, gitGroup, gitProject, env
            );

            // 构建用户信息
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", gitUserName);
            userMap.put("userType", 0);

            List<Object> requestBody = List.of(userMap, pipelineConfig);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("createPipeline request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(CREATE_PIPELINE_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("createPipeline response: {}", responseBody);

                ApiResponse<Long> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Long.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Long data = apiResponse.getData();
                result.addProperty("projectId", projectId);
                result.addProperty("pipelineId", data);
                result.addProperty("result", String.format(
                        "成功创建流水线！\n" +
                                "- 项目ID: %s\n" +
                                "- 流水线ID: %s\n" +
                                "- 流水线名称: %s\n" +
                                "- Git地址: %s\n" +
                                "- 分支: %s\n" +
                                "- 环境: %s",
                        projectId,
                        data,
                        pipelineName,
                        gitUrl,
                        gitBranch,
                        env
                ));
                return result;
            }
        } catch (Exception e) {
            log.error("执行create_pipeline操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 解析Git URL
     * 示例: https://git.n.xiaomi.com/cefe/lgc.git
     * 返回: [git.n.xiaomi.com, cefe, lgc]
     */
    private String[] parseGitUrl(String gitUrl) {
        String cleanUrl = gitUrl.replace("https://", "").replace("http://", "").replace(".git", "");
        String[] parts = cleanUrl.split("/");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Git URL格式不正确: " + gitUrl);
        }
        String gitAddress = parts[0];
        String gitGroup = parts[1];
        String gitProject = parts[2];
        return new String[]{gitAddress, gitGroup, gitProject};
    }

    /**
     * 构建流水线配置
     */
    private Map<String, Object> buildPipelineConfig(
            String projectId, String pipelineName, String pipelineCname, String desc,
            String gitUrl, String gitBranch, String gitAddress, String gitGroup,
            String gitProject, String env) {

        Map<String, Object> config = new HashMap<>();
        config.put("projectId", projectId);
        config.put("pipelineName", pipelineName);
        config.put("pipelineCname", pipelineCname);
        config.put("desc", desc);
        config.put("gitUrl", gitUrl);
        config.put("gitBranch", gitBranch);
        config.put("gitCommitId", "");
        config.put("env", env);
        config.put("gitAddress", gitAddress);
        config.put("gitGroup", gitGroup);
        config.put("gitProject", gitProject);
        config.put("name", gitProject + "-1");
        config.put("deployEnvGroup", env);
        config.put("parentPipelineId", 0);

        // 构建阶段配置
        List<Map<String, Object>> phaseSettings = new ArrayList<>();
        phaseSettings.add(buildCompilePhase(env));
        phaseSettings.add(buildDeployPhase(env));
        config.put("phaseSetting", phaseSettings);

        return config;
    }

    /**
     * 构建编译阶段配置
     */
    private Map<String, Object> buildCompilePhase(String env) {
        Map<String, Object> phase = new HashMap<>();
        phase.put("phaseTitle", "编译");
        phase.put("phaseName", "k8sJobFlow");
        phase.put("phaseOrder", 1);

        List<Map<String, Object>> templates = new ArrayList<>();
        templates.add(buildCompileTemplate(env));
        phase.put("templateSetting", templates);

        return phase;
    }

    /**
     * 构建编译模板配置
     */
    private Map<String, Object> buildCompileTemplate(String env) {
        Map<String, Object> template = new HashMap<>();
        template.put("cname", null);
        template.put("templateStatus", 0);
        template.put("templateTitle", "K8s Job");
        template.put("templateOrder", 1);
        template.put("templateName", "K8sJob");

        List<Map<String, Object>> steps = new ArrayList<>();

        // 1. 下载代码步骤
        steps.add(createStep("k8s_checkout", "k8s下载代码",
                Map.of("image", "MIONE/checkout", "checkoutPath", "")));

        // 2. Maven构建步骤
        steps.add(createStep("k8s_maven_build", "K8s maven项目构建",
                Map.of(
                        "image", "MIONE/maven-build:21",
                        "buildProfile", env,
                        "MVN_OPT", "",
                        "subPath", "",
                        "_pipeline.faas.sidecar.func", "",
                        "_pipeline.faas.sidecar.env", "",
                        "preCMD", ""
                )));

        // 3. Java镜像构建步骤
        steps.add(createStep("k8s_java_image_build", "K8s java镜像构建",
                Map.of(
                        "image", "MIONE/java-image-build",
                        "jarPath", gitName + "-server/target/*.jar",//todo projectName
                        "fromImage", ""
                )));

        // 4. 清理工作空间步骤
        steps.add(createStep("k8s_clean_workspace", "K8s清理任务",
                Map.of("image", "MIONE/clean-workspace")));

        template.put("stepSetting", steps);
        return template;
    }

    /**
     * 构建部署阶段配置
     */
    private Map<String, Object> buildDeployPhase(String env) {
        Map<String, Object> phase = new HashMap<>();
        phase.put("phaseTitle", "部署");
        phase.put("phaseName", "k8sFlow");
        phase.put("phaseOrder", 2);

        List<Map<String, Object>> templates = new ArrayList<>();
        templates.add(buildDeployTemplate(env));
        phase.put("templateSetting", templates);

        return phase;
    }

    /**
     * 构建部署模板配置
     */
    private Map<String, Object> buildDeployTemplate(String env) {
        Map<String, Object> template = new HashMap<>();
        template.put("cname", null);
        template.put("templateStatus", 0);
        template.put("templateTitle", "K8s部署");
        template.put("templateOrder", 1);
        template.put("templateName", "K8sDeploy");

        List<Map<String, Object>> steps = new ArrayList<>();

        // K8s部署步骤
        Map<String, Object> deployParams = new HashMap<>();
        deployParams.put("type", "k8s");
        deployParams.put("_pipeline.replicas", 1);
        deployParams.put("_pipeline.deploy_cpu", 1);
        deployParams.put("_pipeline.deploy_memory", "2Gi");
        deployParams.put("JAVA_OPT", "-XX:+UseG1GC --add-opens=java.base/java.time=ALL-UNNAMED  --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/sun.reflect=ALL-UNNAMED --add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --enable-preview");
        deployParams.put("OT_AGENT", true);
        deployParams.put("loadTestingComponent", "0");
        deployParams.put("autoCapacity", false);
        deployParams.put("_pipeline.ai_scale", false);
        deployParams.put("_deploy.autoscale", 0);
        deployParams.put("_pipeline.log_path", "");
        deployParams.put("mione.kc_appid", "");
        deployParams.put("mione.kc_sidlist", "");
        deployParams.put("_k8s.deploy_timeout", 60);
        deployParams.put("_deploy.mesh_init_container", false);
        deployParams.put("_pipeline.sidecars", "");
        deployParams.put("sidecarIds", "");
        deployParams.put("_deploy.pod_load_level", "");
        deployParams.put("_deploy.biz.anti_affinity", "");
        deployParams.put("_pipeline.min_ready_seconds", 30);
        deployParams.put("_pipeline.probe", "");
        deployParams.put("_pipeline.envs", "");

        // 根据环境设置zone
        String zone = env.equals("staging") ? "pub-st:mione-staging" : "auto";
        deployParams.put("_pipeline.zone", zone);

        deployParams.put("_pipeline.iamTree", "{\"fullIamId\":\"2;3;43;12087;313362\",\"fullIamName\":\"小米;中国区;系统组(待废弃);mione;miline\"}");
        deployParams.put("init_scripts", "");
        deployParams.put("_pipeline.pvcs", "");
        deployParams.put("_pipeline.gracefulShutdown", false);
        deployParams.put("_pipeline.configmaps", "[{\"conigMapName\":\"\",\"refName\":[],\"ref\":[]}]");
        deployParams.put("_pipeline.faas.sidecar.env", "");
        deployParams.put("_pipeline.faas.sidecar.image", "");

        steps.add(createStep("k8s_deploy", "K8s部署", deployParams));

        template.put("stepSetting", steps);
        return template;
    }

    /**
     * 创建步骤配置
     */
    private Map<String, Object> createStep(String stepName, String stepCname, Map<String, Object> params) {
        Map<String, Object> step = new HashMap<>();
        step.put("stepName", stepName);
        step.put("stepParams", params);
        step.put("stepCname", stepCname);
        return step;
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
    private static class PipelineResult {
        private Integer id;
        private String name;
        private String gitUrl;
        private String gitBranch;
    }
}
