package run.mone.m78.service.service.code.editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.gitlab.Gitlab;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import run.mone.m78.api.FeatureRouterProvider;
import run.mone.m78.api.bo.code.editor.EditorPath;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.service.service.feature.router.dubbo.FeatureRouterProviderImpl;

import javax.annotation.Resource;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j

/**
 * CodeEditorService类提供了一系列与代码编辑和Git仓库交互相关的服务。
 *
 * 主要功能包括：
 * - 获取和编辑模拟文件的内容。
 * - 从Git仓库中获取和更新文件内容。
 * - 获取项目的文件树结构。
 * - 获取和更新脚本内容。
 * - 执行Groovy脚本。
 *
 * 该类依赖于FeatureRouterProviderImpl、Gson和OkHttpClient等组件，并使用了Spring的@Service注解和Lombok的@Slf4j注解。
 */

public class CodeEditorService {
    @Resource
    private FeatureRouterProviderImpl featureRouterProvider;
    private final Gson gson = new Gson();
    final OkHttpClient client = new OkHttpClient();
    public String main = "package demo;\n" +
            "\n" +
            "//TIP To <b>Run</b> code, press <shortcut actionId=\"Run\"/> or\n" +
            "// click the <icon src=\"AllIcons.Actions.Execute\"/> icon in the gutter.\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        //TIP Press <shortcut actionId=\"ShowIntentionActions\"/> with your caret at the highlighted text\n" +
            "        // to see how IntelliJ IDEA suggests fixing it.\n" +
            "        System.out.printf(\"Hello and welcome!\");\n" +
            "\n" +
            "        for (int i = 1; i <= 5; i++) {\n" +
            "            //TIP Press <shortcut actionId=\"Debug\"/> to start debugging your code. We have set one <icon src=\"AllIcons.Debugger.Db_set_breakpoint\"/> breakpoint\n" +
            "            // for you, but you can always add more by pressing <shortcut actionId=\"ToggleLineBreakpoint\"/>.\n" +
            "            System.out.println(\"i = \" + i);\n" +
            "        }\n" +
            "    }\n" +
            "}";
    public String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <groupId>demo</groupId>\n" +
            "    <artifactId>test</artifactId>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "\n" +
            "    <properties>\n" +
            "        <maven.compiler.source>17</maven.compiler.source>\n" +
            "        <maven.compiler.target>17</maven.compiler.target>\n" +
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "    </properties>\n" +
            "\n" +
            "</project>";

    /**
     * 根据文件名返回对应的文件内容
     *
     * @param name 文件名
     * @return 对应文件的内容，如果文件名为"pom.xml"则返回pom内容，否则返回main内容
     */
    public String getMockFile(String name) {
        if ("pom.xml".equals(name)) {
            return pom;
        }
        return main;
    }

    /**
     * 从Git仓库中获取指定文件的内容
     *
     * @param url    Git仓库的URL
     * @param token  访问Git仓库的认证令牌
     * @param path   文件在仓库中的路径
     * @param branch 文件所在的分支
     * @return 指定文件的内容
     */
    public String getGitFile(String url, String token, String path, String branch) {
        Gitlab git = new Gitlab();
        return git.fetchFile(parseGitHostUrl(url), parseProjectID(url), token, path, branch);
    }

    /**
     * 更新Git仓库中的文件
     *
     * @param url        Git仓库的URL
     * @param token      访问Git仓库的私有令牌
     * @param path       文件在仓库中的路径
     * @param branch     分支名称
     * @param content    文件的新内容
     * @param commit_msg 提交信息
     * @return 更新是否成功
     */
    public boolean updateGitFile(String url, String token, String path, String branch, String content, String commit_msg) {
        try {
            Map<String, String> m = new HashMap<>();
            URL u = new URL(url);
            String host = parseGitHostUrl(url);
            String projectID = parseProjectID(url);
            if (host.contains("..") || projectID.contains("..") || path.contains("..")) {
                throw new RuntimeException("invalid url addr");
            }
            String addr = String.format("%sapi/v4/projects/%s/repository/files/%s",
                    host, projectID, URLEncoder.encode(path, "UTF-8"));
            m.put("content", content);
            m.put("commit_message", commit_msg);
            if (!StringUtils.isEmpty(branch)) {
                m.put("branch", branch);
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("PRIVATE-TOKEN", token);
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), gson.toJson(m));
            Request request = new Request.Builder()
                    .url(addr)
                    .put(body)
                    .headers(Headers.of(headers))
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 编辑模拟文件的内容
     *
     * @param name    文件名
     * @param content 文件内容
     */
    public void editMockFile(String name, String content) {
        if ("pom.xml".equals(name)) {
            pom = content;
            return;
        }
        main = content;
    }

    private static String parseGitHostUrl(String gitUrl) {
        try {
            URL u = new URL(gitUrl);
            return u.getProtocol() + "://" + u.getHost() + "/";
        } catch (Exception e) {
            return null;
        }
    }

    private static String parseGitGroup(String gitUrl) {
        try {
            URL u = new URL(gitUrl);
            String path = u.getPath();
            if (StringUtils.isEmpty(path) || path.split("/").length < 3) {
                return "";
            }
            return path.substring(1, path.lastIndexOf("/"));
        } catch (Exception e) {
            return null;
        }
    }

    private static String parseProjectID(String gitUrl) {
        try {
            URL u = new URL(gitUrl);
            String path = u.getPath();
            path = path.replaceAll("^/|/$", "");
            return URLEncoder.encode(path, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取项目的文件树
     *
     * @param url    项目的URL地址
     * @param token  访问API所需的私有令牌
     * @param branch 项目的分支名称，如果为空则使用默认分支
     * @return 包含项目文件树的EditorPath列表，如果发生异常则返回null
     */
    public List<EditorPath> getProjectTree(String url, String token, String branch) {
        try {
            List<EditorPath> res = new ArrayList<>();
            URL u = new URL(url);
            String host = parseGitHostUrl(url);
            String projectID = parseProjectID(url);
            String addr = StringUtils.isEmpty(branch) ?
                    host + "api/v4/projects/" + projectID + "/repository/tree?per_page=100&private_token=" + token + "&path="
                    : host + "api/v4/projects/" + projectID + "/repository/tree?per_page=100&private_token=" + token + "&ref=" + branch + "&path=";
            Map<String, String> headers = new HashMap<>();
//            headers.put("PRIVATE-TOKEN", token);

            return buildEditorPath(addr, "", headers);
        } catch (Exception e) {
            log.error("getProjectTree {} {}", url, token, e);
            return null;
        }
    }

//    private List<EditorPath> buildEditorPath(String baseURL, String path, Map<String, String> headers) {
//        try {
//            List<EditorPath> res = new ArrayList<>();
//            String response = HttpClientV2.get(baseURL + path, headers, 10000);
//            List<Map<String, String>> treeList = gson.fromJson(response, new TypeToken<List<Map<String, String>>>() {
//            }.getType());
//            for (Map<String, String> node : treeList) {
//                if ("tree".equals(node.get("type"))) {
//                    String nextBase = "".equals(path) ? baseURL : baseURL + path + "/";
//                    List<EditorPath> child = buildEditorPath(nextBase, node.get("name"), headers);
//                    if (child == null) {
//                        continue;
//                    }
//                    EditorPath ep = EditorPath.builder().name(node.get("name")).dir(true).dirChild(child).build();
//                    res.add(ep);
//                } else {
//                    EditorPath ep = EditorPath.builder().name(node.get("name")).build();
//                    res.add(ep);
//                }
//            }
//            return res;
//        } catch (Exception e) {
//            log.error("buildEditorPath  {} {}", baseURL, path, e);
//            return null;
//        }
//    }

    private List<EditorPath> buildEditorPath(String baseURL, String path, Map<String, String> headers) {
        try {
            List<EditorPath> res = new ArrayList<>();
            Lock l = new ReentrantLock();
            String response = HttpClientV2.get(baseURL + path, headers, 10000);
            List<Map<String, String>> treeList = gson.fromJson(response, new TypeToken<List<Map<String, String>>>() {
            }.getType());
            CountDownLatch latch = new CountDownLatch(treeList.size());
            for (Map<String, String> node : treeList) {
                if ("tree".equals(node.get("type"))) {
                    new Thread(() -> {
                        String nextBase = "".equals(path) ? baseURL : baseURL + path + "/";
                        List<EditorPath> child = buildEditorPath(nextBase, node.get("name"), headers);
                        if (child == null) {
                            return;
                        }
                        EditorPath ep = EditorPath.builder().name(node.get("name")).dir(true).dirChild(child).build();
                        l.lock();
                        res.add(ep);
                        l.unlock();
                        latch.countDown();
                    }).start();
                } else {
                    EditorPath ep = EditorPath.builder().name(node.get("name")).build();
                    l.lock();
                    res.add(ep);
                    l.unlock();
                    latch.countDown();
                }
            }
            latch.await();
            return res;
        } catch (Exception e) {
            log.error("buildEditorPath  {} {}", baseURL, path, e);
            return null;
        }
    }

    /**
     * 获取一个模拟的文件树结构
     *
     * @return 包含模拟文件树结构的列表
     */
    public List<EditorPath> getMockTree() {
        List<EditorPath> res = new ArrayList<>();
        res.add(EditorPath.builder()
                .dir(true).name("src").dirChild(
                        Arrays.asList(
                                EditorPath.builder()
                                        .dir(true)
                                        .name("main")
                                        .dirChild(Arrays.asList(
                                                EditorPath.builder()
                                                        .dir(true)
                                                        .name("java")
                                                        .dirChild(Arrays.asList(
                                                                EditorPath.builder()
                                                                        .dir(true)
                                                                        .name("demo")
                                                                        .dirChild(Arrays.asList(
                                                                                EditorPath.builder()
                                                                                        .dir(false)
                                                                                        .name("Main.java")
                                                                                        .build()
                                                                        ))
                                                                        .build()
                                                        ))
                                                        .build()
                                        ))
                                        .build()
                        )
                )
                .build());
        res.add(EditorPath.builder()
                .name("pom.xml")
                .build());
        return res;
    }

    /**
     * 根据名称获取脚本内容
     *
     * @param name 脚本名称
     * @return 脚本内容
     */
    public String getScript(String name) {
        FeatureRouterReq req = new FeatureRouterReq();
        req.setType(1);
        req.setName(name);
        Pair<Long, List<FeatureRouterDTO>> scripts = featureRouterProvider.listAllFeatureRouter(req);
        return scripts.getRight().get(0).getContent();
    }

    public String getScript(Long id) {
        FeatureRouterReq req = new FeatureRouterReq();
        req.setType(1);
        req.setId(id);
        Pair<Long, List<FeatureRouterDTO>> scripts = featureRouterProvider.listAllFeatureRouter(req);
        return scripts.getRight().get(0).getContent();
    }

    /**
     * 更新脚本内容
     *
     * @param id      脚本的唯一标识
     * @param content 脚本的新内容
     * @return 更新是否成功
     */
    public boolean updateScript(Long id, String content) {
        FeatureRouterReq req = new FeatureRouterReq();
        req.setType(1);
        req.setContent(content);
        return featureRouterProvider.updateFeatureRouterMappingContent(req);
    }

    /**
     * 执行给定的Groovy脚本并返回结果
     *
     * @param vars   传递给脚本的变量映射
     * @param script 要执行的Groovy脚本
     * @return 脚本执行的结果
     */
    public Object runGroovy(Map<String, Object> vars, String script) {
        Binding binding = new Binding();

// set a variable named "x" with the value 2
        vars.forEach(binding::setVariable);


// create a new GroovyShell instance with the binding
        GroovyShell shell = new GroovyShell(binding);

// evaluate the script and get the result
        return shell.evaluate(script);
    }

    public Object runGroovy(Object[] vars, String script) throws InstantiationException, IllegalAccessException {
        GroovyClassLoader loader = new GroovyClassLoader();
        Class groovyClass = loader.parseClass(script);

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        // Call Groovy method with multiple params
        return groovyObject.invokeMethod("execute", vars);
//        GroovyShell shell = new GroovyShell();
//        shell.parse(script);
//        return shell.getMetaClass().invokeMethod(shell,"execute",vars);
    }


}
