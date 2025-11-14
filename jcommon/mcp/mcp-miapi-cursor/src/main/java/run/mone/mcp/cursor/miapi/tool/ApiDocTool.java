package run.mone.mcp.cursor.miapi.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.cursor.miapi.http.HttpClient;
import run.mone.mcp.cursor.miapi.model.ParserResult;
import run.mone.mcp.cursor.miapi.parser.SourceCodeApiParser;
import run.mone.mcp.cursor.miapi.util.FileScanner;
import run.mone.mcp.cursor.miapi.util.gitProjectUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ApiDocTool implements ITool {

    @Value("${miapi.host}")
    private String host;

    private static final String api_url = "/OpenApi/generateDoc";

    private static final String api_url_project = "/OpenApi/createProject";

    @Value("${doc.host}")
    private String doc_host;

    @Value("${git.username}")
    private String gitUsername;
    @Value("${git.token}")
    private String gitToken;
    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();

    @Override
    public String getName() {
        return "create_api_doc";
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
            1.功能只是根据用户提供的java源代码生成接口文档.
            2.用户需要输入源代码地址或路径.
            """;
    }

    @Override
    public String parameters() {
        return """
                - path: (Required) java source code address or path.
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you must return whether the generation was successful):
            Example:
            ```json
            
            api docs result
            
            ```
            """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        String directoryPath = inputJson.has("path") ? inputJson.get("path").getAsString() : null;
        if (directoryPath == null || directoryPath.isEmpty()) {
            result.addProperty("error", "缺少必要的代码路径参数");
            return result;
        }

        try {
            if (!FileScanner.isDirectoryExists(directoryPath)) {
                result.addProperty("error", "代码路径或地址不正确");
                return result;
            }
            boolean isGit = directoryPath.endsWith(".git");
            String projectName;
            String actualDirectoryPath = directoryPath;

            if (isGit) {
                projectName = extractProjectNameFromGitUrl(directoryPath);
                actualDirectoryPath = gitProjectUtil.cloneRepository(directoryPath, projectName, gitUsername, gitToken);

                if (StringUtils.isBlank(actualDirectoryPath)) {
                    result.addProperty("error", "Git仓库克隆失败: " + directoryPath);
                    return result;
                }
            } else {
                // 普通目录处理
                projectName = StringUtils.substringAfterLast(directoryPath, "/");
                if (StringUtils.isBlank(projectName)) {
                    projectName = StringUtils.substringAfterLast(directoryPath, "\\");
                }
            }

            // 解析目录并生成文档
            String doc = parseDirectory(actualDirectoryPath);
            if (StringUtils.isBlank(doc)) {
                result.addProperty("error", "无法从目录中解析出文档: " + actualDirectoryPath);
                return result;
            }

            // 更新文档到数据库
            int projectId = updateDoc(doc, projectName);
            if (projectId <= 0) {
                result.addProperty("error", "文档更新到平台失败");
                return result;
            }
            if (isGit && actualDirectoryPath != null) {
                try {
                    FileScanner.deleteDirectory(actualDirectoryPath);
                } catch (Exception e) {
                    log.warn("清理临时目录失败: {}", actualDirectoryPath, e);
                }
            }

            // 构建成功响应
            result.addProperty("apiDocs", doc);
            result.addProperty("apiDocsUrl", doc_host + projectId);
            return result;

        } catch (Exception e) {
            result.addProperty("error", "生成接口信息失败: " + e.getMessage());
            return result;
        }
    }

    private String extractProjectNameFromGitUrl(String gitUrl) {
        String withoutGit = gitUrl.substring(0, gitUrl.length() - 4);
        return StringUtils.substringAfterLast(withoutGit, "/");
    }

    private String parseDirectory(String directoryPath) {
        List<String> javaFiles = FileScanner.scanJavaFiles(directoryPath);
        if (javaFiles.isEmpty()) {
            return "";
        }
        // 解析接口文档
        SourceCodeApiParser parser = new SourceCodeApiParser(directoryPath);
        ParserResult result = parser.parseDirectory(directoryPath);

        if (result.isSuccess()) {
            return result.toString();
        } else {
            return "";
        }
    }

    private int updateDoc(String docs, String projectName) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("docs", docs);
            map.put("groupId", "620");
            map.put("projectName", projectName);
            JsonObject response = httpClient.post(host + api_url, gson.toJson(map));
            return response.get("data").getAsInt();
        } catch (Exception e) {
            log.error("updateDoc error: ", e);
        }
        return 0;
    }
}
