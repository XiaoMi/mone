/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.m78.service.service.git.impl;

import com.google.common.base.Preconditions;
import com.xiaomi.data.push.antlr.java.Java8Expr;
import com.xiaomi.data.push.antlr.java.JavaExprDTO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.gitlab.GitTreeItem;
import run.mone.m78.service.service.git.InnerGitLabService;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shanwb
 * @date 2024-02-26
 */
@Service
public class InnerGitLabServiceImpl implements InnerGitLabService {

    //帮我生成一个org.gitlab4j.api.GitLabApi类的缓存方法，避免每次都new一个对象，新建方式如下GitLabApi gitLabApi = new GitLabApi(baseUrl, gitToken); (method)
    private static final ConcurrentHashMap<String, GitLabApi> gitLabApiCache = new ConcurrentHashMap<>();

    /**
     * 获取GitLabApi实例，如果缓存中不存在则创建新的实例并缓存
     *
     * @param baseUrl GitLab服务器的基础URL
     * @param gitToken 访问GitLab API的令牌
     * @return GitLabApi实例
     */
	public static GitLabApi getGitLabApi(String baseUrl, String gitToken) {
        String key = baseUrl + "#" + gitToken;
        return gitLabApiCache.computeIfAbsent(key, k -> new GitLabApi(baseUrl, gitToken));
    }

    private static List<String> SUPPORT_FILE_TYPES = Arrays.asList(
            ".java", ".xml", ".html", ".css", ".js", ".json", ".md",
            ".yml", ".yaml", ".properties", ".sql", ".txt", ".sh", ".bat", ".py", ".c", ".cpp",
            ".h", ".rb", ".php", ".cs", ".swift", ".go", ".rs"
    );

    /**
     * 判断给定的文件名后缀是否在支持的文件类型集合中
     *
     * @param fileName 文件名
     * @return 如果文件名后缀在支持的文件类型集合中，返回true；否则返回false
     */
	//给一个文件名，判断后缀是否在SUPPORT_FILE_TYPES 结合中 (class)
    public boolean isSupportedFileType(String fileName) {
        return SUPPORT_FILE_TYPES.stream().anyMatch(fileName::endsWith);
    }


    /**
     * 利用GitLabApi获取远程仓库指定文件路径的内容，并将获取的base64内容转成原文返回
     *
     * @param branch 分支名称
     * @param gitDomain GitLab域名
     * @param gitToken GitLab访问令牌
     * @param projectId 项目ID
     * @param filePath 文件路径
     * @return 文件内容的原文
     * @throws RuntimeException 当从GitLab获取文件内容出错时抛出
     */
	//利用GitLabApi获取远程仓库指定文件路径的内容，并将获取的base64内容转成原文返回，方法签名为：public String getFileContent(String branch, String gitDomain, String gitToken, String projectId, String filePath) (class)
    @Override
    public String getFileContent(String branch, String gitDomain, String gitToken, String projectId, String filePath) {
        try {
            Preconditions.checkArgument(null != filePath, "filePath can not be null");
            Preconditions.checkArgument(isSupportedFileType(filePath), "not support this file type");

            String baseUrl = "https://" + gitDomain;
            GitLabApi gitLabApi = getGitLabApi(baseUrl, gitToken);

            RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(projectId, filePath, branch);
            String encodedContent = file.getContent();
            byte[] decodedBytes = Base64.getDecoder().decode(encodedContent);

            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (GitLabApiException e) {
            throw new RuntimeException("Error retrieving file content from GitLab", e);
        }
    }

    /**
     * 解析指定项目的Java文件，提取类信息、包信息、方法和字段等信息
     *
     * @param branch 分支名称
     * @param gitDomain Git仓库域名
     * @param gitToken Git访问令牌
     * @param projectId 项目ID
     * @param filePath 文件路径
     * @return 包含解析后信息的JavaExprDTO对象
     */
	@Override
    public JavaExprDTO parseProjectJavaFile(String branch, String gitDomain, String gitToken, String projectId, String filePath) {
        Preconditions.checkArgument(null != filePath, "filePath can not be null");
        Preconditions.checkArgument(filePath.endsWith(".java"), "only support .java file");

        String originalContent = this.getFileContent(branch, gitDomain, gitToken, projectId, filePath);

        Java8Expr java8Expr = new Java8Expr();
        java8Expr.walk(originalContent);

        JavaExprDTO javaExprDTO = new JavaExprDTO();
        javaExprDTO.setClassInfo(java8Expr.getClassInfo());
        javaExprDTO.setClassPackage(java8Expr.getClassPackage());
        javaExprDTO.setMethods(java8Expr.getMethods());
        javaExprDTO.setFields(java8Expr.getFields());

        return javaExprDTO;
    }

    /**
     * 利用GitLabApi获取指定projectId的目录结构，递归获取用于WEB IDE端进行工程展示。
     *
     * @param branch 分支名称
     * @param gitDomain GitLab域名
     * @param gitToken GitLab访问令牌
     * @param projectId 项目ID
     * @return 项目的目录结构列表
     * @throws RuntimeException 当从GitLab获取项目结构时发生错误
     */
	//利用GitLabApi获取指定projectId的目录结构，需要完整递归完，用于WEB IDE端进行工程展示。入参有：String branch, String gitDomain, String gitToken, String projectId (class)
    @Override
    public List<GitTreeItem> getProjectStructureTree(String branch, String gitDomain, String gitToken, String projectId) {
        try {
            String baseUrl = "https://" + gitDomain;
            GitLabApi gitLabApi = getGitLabApi(baseUrl, gitToken);
            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(projectId, null, branch, true);

            List<GitTreeItem> treeItemList = transformProjectStructureTree(treeItems);

            return treeItemList;
        } catch (GitLabApiException e) {
            throw new RuntimeException("Error retrieving project structure from GitLab", e);
        }
    }

    //将调用getProjectStructureTree获取到的List<TreeItem>进行结构改造，新结构TreeItem2较TreeItem新增属性parentId,父子关系的判定基于TreeItem的path和type (class)
    private List<GitTreeItem> transformProjectStructureTree(List<TreeItem> treeItems) {
        Map<String, GitTreeItem> itemMap = new HashMap<>();
        List<GitTreeItem> result = new ArrayList<>();

        // First, convert all TreeItems to TreeItem2 and store them in a map for easy lookup
        for (TreeItem item : treeItems) {
            GitTreeItem gitTreeItem = new GitTreeItem();
            BeanUtils.copyProperties(item, gitTreeItem);
            gitTreeItem.setType(item.getType().name());
            itemMap.put(item.getPath(), gitTreeItem);
        }

        // Next, establish parent-child relationships
        for (GitTreeItem gitTreeItem : itemMap.values()) {
            String path = gitTreeItem.getPath();
            String parentPath = path.contains("/") ? path.substring(0, path.lastIndexOf('/')) : null;
            gitTreeItem.setParentId(parentPath != null ? itemMap.get(parentPath).getId() : null);

            result.add(gitTreeItem);
        }

        return result;
    }




}
