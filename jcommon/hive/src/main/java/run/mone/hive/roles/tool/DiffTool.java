package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Diff tool that shows changes from last checkpoint commit (HEAD) to current work-tree.
 */
@Slf4j
public class DiffTool implements ITool {

    public static final String NAME = "diff_since_checkpoint";
    private static final int DEFAULT_CONTEXT = 3;
    private static final int DEFAULT_MAX_BYTES = 200 * 1024; // 200KB

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
                res.addProperty("diff", diff);
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

        } catch (Exception e) {
            log.error("DiffTool execute error", e);
            res.addProperty("error", e.getMessage());
        }
        return res;
    }
}

