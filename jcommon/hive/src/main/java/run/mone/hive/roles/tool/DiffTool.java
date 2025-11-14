package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;
import run.mone.hive.roles.ReactorRole;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Diff tool that shows changes from last checkpoint commit (HEAD) to current work-tree.
 */
@Slf4j
public class DiffTool implements ITool {

    public static final String NAME = "diff_since_checkpoint";
    private static final int DEFAULT_CONTEXT = 3;
    private static final int DEFAULT_MAX_BYTES = 200 * 1024; // 200KB
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;

    public DiffTool() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getName() {
        return NAME;
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
        return "展示从最近检查点(HEAD)到当前工作区的差异；可指定checkpoint_id与路径范围。";
    }

    @Override
    public String parameters() {
        return "checkpoint_id(可选), paths(可选, 数组或逗号分隔), context_lines(可选), max_bytes(可选)";
    }

    @Override
    public String usage() {
        return "<diff_since_checkpoint>\n" +
                "<checkpoint_id>可选</checkpoint_id>\n" +
                "<paths>可选, 例: src/main/java</paths>\n" +
                "<context_lines>3</context_lines>\n" +
                "</diff_since_checkpoint>";
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject req) {
        JsonObject res = new JsonObject();
        try {
            if (role.getFileCheckpointManager() == null) {
                res.addProperty("error", "FileCheckpointManager 未初始化或不可用");
                return res;
            }

            String checkpointId = req.has("checkpoint_id") ? StringUtils.trimToEmpty(req.get("checkpoint_id").getAsString()) : "";
            int context = req.has("context_lines") ? Math.max(0, req.get("context_lines").getAsInt()) : DEFAULT_CONTEXT;
            int maxBytes = req.has("max_bytes") ? Math.max(1024, req.get("max_bytes").getAsInt()) : DEFAULT_MAX_BYTES;

            List<String> paths = new ArrayList<>();
            if (req.has("paths")) {
                if (req.get("paths").isJsonArray()) {
                    req.get("paths").getAsJsonArray().forEach(e -> paths.add(e.getAsString()));
                } else {
                    String s = req.get("paths").getAsString();
                    for (String p : s.split(",")) {
                        if (StringUtils.isNotBlank(p)) paths.add(p.trim());
                    }
                }
            }

            String diff = role.getFileCheckpointManager().diffFromCheckpoint(StringUtils.defaultIfBlank(checkpointId, null), paths, context);
            if (diff == null) diff = "";

            boolean truncated = false;
            byte[] bytes = diff.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > maxBytes) {
                int keep = maxBytes;
                diff = new String(bytes, 0, keep, StandardCharsets.UTF_8) + "\n...\n[diff output truncated]";
                truncated = true;
            }

            if (diff.isEmpty()) {
                res.addProperty("message", "无差异");
            } else {
                String escapedDiff = HtmlUtils.htmlEscape(diff);
                res.addProperty("diff", escapedDiff);
            }
            res.addProperty("text_truncated", truncated);

            // 将受影响文件简要列表（基于简易解析）
            JsonArray changed = new JsonArray();
            String[] lines = diff.split("\n");
            for (String line : lines) {
                if (line.startsWith("+++ b/") || line.startsWith("--- a/")) {
                    String p = line.substring(6);
                    changed.add(p);
                }
            }
            res.add("changed_files", changed);
            res.addProperty("changed_files_count", changed.size());

            // 自动发送diff通知
            if (isAutoDiffNotify()) {
                sendDiffNotification(role, checkpointId, changed);
            }

        } catch (Exception e) {
            log.error("DiffTool execute error", e);
            res.addProperty("error", e.getMessage());
        }
        return res;
    }

     /**
     * 是否开启diff notify
     */
    private boolean isAutoDiffNotify() {
       try {
            String p = System.getProperty("hive.auto.diff.notify");
            if (p == null) {
                p = System.getenv("HIVE_AUTO_DIFF_NOTIFY");
            }
            if (p == null) {
                return false;
            }
            p = p.trim().toLowerCase();
            log.info("auto diff enabled by props:{}", p);
            return "true".equals(p) || "1".equals(p) || "yes".equals(p) || "on".equals(p);
        } catch (Throwable ignore) {
            return false;
        }
    }

    /**
     * 发送diff通知到外部接口
     */
    private void sendDiffNotification(ReactorRole role, String checkpointId, JsonArray changedFiles) {
        try {
            // 获取项目名称
            String projectName = extractProjectName(role.getWorkspacePath());
            if (StringUtils.isEmpty(projectName)) {
                log.warn("无法获取项目名称，跳过diff通知");
                return;
            }

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("cmd", "show_checkpoint_diff");
            requestBody.addProperty("projectName", projectName);

            // 如果有checkpointId，则添加
            if (StringUtils.isNotEmpty(checkpointId)) {
                requestBody.addProperty("checkpointId", checkpointId);
            }

            // 发送HTTP请求
            String url = "http://127.0.0.1:3458/tianye";
            sendHttpPostRequest(url, requestBody.toString());

            log.info("已发送diff通知: projectName={}, checkpointId={}, changedFiles={}",
                    projectName, checkpointId, changedFiles.size());
        } catch (Exception e) {
            log.error("发送diff通知失败", e);
        }
    }

    /**
     * 从工作区路径中提取项目名称
     */
    private String extractProjectName(String workspacePath) {
        if (StringUtils.isEmpty(workspacePath)) {
            return null;
        }
        try {
            return Paths.get(workspacePath).getFileName().toString();
        } catch (Exception e) {
            log.error("提取项目名称失败: {}", workspacePath, e);
            return null;
        }
    }

    /**
     * 发送HTTP POST请求
     */
    private void sendHttpPostRequest(String urlString, String jsonBody) throws Exception {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(urlString)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            int responseCode = response.code();
            log.debug("HTTP POST 响应码: {}", responseCode);

            if (!response.isSuccessful()) {
                log.warn("HTTP POST 请求失败: {}", responseCode);
            }
        }
    }
}

