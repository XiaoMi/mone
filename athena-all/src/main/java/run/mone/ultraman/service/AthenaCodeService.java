package run.mone.ultraman.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPrivateModifier;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.bo.MessageConsumer;
import com.xiaomi.youpin.tesla.ip.bo.z.EmbeddingStatus;
import com.xiaomi.youpin.tesla.ip.bo.z.ZKnowledgeRes;
import com.xiaomi.youpin.tesla.ip.bo.z.ZResult;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import it.unimi.dsi.fastutil.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.*;
import run.mone.ultraman.http.HttpClient;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/11/5 22:36
 */
@Slf4j
public class AthenaCodeService {

    private static Gson gson = new Gson();


    public static final boolean openCodeServer = true;

    public static final int CLASS_MAX_LEN = 3000;


    public static String getCodeServer() {
        return AthenaContext.ins().getZAddr();
    }


    @SneakyThrows
    public static Pair<Integer, String> callProxy(Project project, Map<String, String> map, String promptName, int timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder sb = new StringBuilder();
        CodeService.generateCodeWithAi4(project, promptName, new String[]{}, map, (p, msg) -> {
        }, new MessageConsumer() {
            @Override
            public void onEvent(AiMessage message) {
                sb.append(message.getText());
            }

            @Override
            public void end(AiMessage message) {
                latch.countDown();
            }
        });
        latch.await(timeout, TimeUnit.SECONDS);
        String str = sb.toString();
        return Pair.of(str.length(), str);
    }


    //解析代码，返回一个列表，每个元素包含一个类信息和对应的代码字符串
    public static List<AthenaPair<AthenaClassInfo, String>> parseCode(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        List<AthenaPair<AthenaClassInfo, String>> res = new ArrayList<>();
        cu.findAll(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
            String classCode = c.toString();
            String md5 = md5(classCode);

            AthenaClassInfo ci = AthenaClassInfo.builder().name(c.getNameAsString()).md5(md5).classCode(classCode).build();
            res.add(AthenaPair.of(ci, classCode));
        });
        return res;
    }

    public static AthenaClassInfo classInfoWithDetail(String code, boolean needDetail) {
        int len = code.length();

        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> pr = javaParser.parse(code);
        if (!pr.isSuccessful()) {
            System.out.println(pr.getProblems());
            return null;
        }
        CompilationUnit cu = pr.getResult().get();
        Optional<ClassOrInterfaceDeclaration> classOptional = cu.findAll(ClassOrInterfaceDeclaration.class).stream().findFirst();

        String packagePath = "";

        // 获取包声明
        Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration();
        if (packageDeclaration.isPresent()) {
            // 获取包的完整路径
            packagePath = packageDeclaration.get().getNameAsString();
        }

        //只保留public method的函数定义
        List<String> methodList = cu.findAll(MethodDeclaration.class).stream().filter(it -> it.isPublic()).map(it -> {
            if (it.getBody().isPresent()) {
                it.remove(it.getBody().get());
            }
            return it.toString();
        }).collect(Collectors.toList());


        //干掉所有private的函数
        cu.findAll(MethodDeclaration.class).stream().filter(NodeWithPrivateModifier::isPrivate).forEach(Node::removeForced);

        //干掉构造函数
        cu.findAll(ConstructorDeclaration.class).stream().forEach(Node::removeForced);

        cu.findAll(InitializerDeclaration.class, InitializerDeclaration::isStatic).forEach(Node::removeForced);

        if (len > 5000) {
            //删除所有字段
            cu.findAll(FieldDeclaration.class).stream().forEach(Node::removeForced);
        }

        String name = classOptional.isPresent() ? classOptional.get().getNameAsString() : "";

        List<String> list = new ArrayList<>();
        List<String> annoList = new ArrayList<>();
        if (classOptional.isPresent()) {
            annoList = classOptional.get().getAnnotations().stream().map(AnnotationExpr::getNameAsString).collect(Collectors.toList());
            list = classOptional.get().getImplementedTypes().stream().map(Node::toString).collect(Collectors.toList());
        }

        AthenaClassInfo athenaClassInfo = AthenaClassInfo.builder().name(name).packagePath(packagePath).interfaceList(list).annoList(annoList).publicMethodList(methodList).classCode(cu.toString()).build();

        if (needDetail) {
            List<String> imports = cu.getImports().stream().map(ImportDeclaration::getNameAsString).collect(Collectors.toList());
            athenaClassInfo.setImports(imports);
        }

        return athenaClassInfo;
    }

    public static AthenaClassInfo classInfo(String code) {
        return classInfoWithDetail(code, false);
    }

    public static String parseAndSkipCode(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        List<MethodDeclaration> list = cu.findAll(MethodDeclaration.class).stream().filter(it -> {
            Optional<Comment> comment = it.getComment();
            if (comment.isPresent()) {
                comment.stream().filter(it2 -> {
                    if (it2.isLineComment() && it2.toString().startsWith("//skip")) {
                        return false;
                    }
                    return true;
                });
            }
            return true;
        }).collect(Collectors.toList());
        return "";
    }

    //解析方法代码，返回一个包含AthenaMethodInfo对象的列表
    public static List<AthenaMethodInfo> parseMethodCode(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        return cu.findAll(MethodDeclaration.class).stream().map(it -> {
                    String methodCode = it.toString();
                    return AthenaMethodInfo.builder()
                            .name(it.getNameAsString())
                            .md5(md5(methodCode))
                            .code(methodCode).build();
                })
                .collect(Collectors.toList());
    }

    //解析方法代码，返回一个包含AthenaMethodInfo对象的列表
    public static List<AthenaFieldInfo> parseFieldCode(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        return cu.findAll(FieldDeclaration.class).stream().map(it -> {
                    String fieldCode = it.toString();
                    return AthenaFieldInfo.builder()
                            .name(fieldCode)
                            .md5(md5(fieldCode))
                            .code(fieldCode).build();
                })
                .collect(Collectors.toList());
    }


    //使用MD5算法对输入的字符串进行哈希计算
    public static String md5(String code) {
        return Hashing.md5().hashString(code, StandardCharsets.UTF_8).toString();
    }

    //上传方法代码的实现
    public static void uploadMethodCode(CodeReq req) {
        if (!openCodeServer) {
            return;
        }
        List<AthenaMethodInfo> list = parseMethodCode(req.getCode());
        req.setData(gson.toJson(list));
        HttpClient.asyncCallHttpServer(getCodeServer() + "/upload/method", "upload_method_code", gson.toJson(req), (msg) -> NotificationCenter.notice(null, "biz code upload success", true));
    }

    //获取方法代码列表
    public static List<String> getMethodCodeList(CodeReq req) {
        if (!openCodeServer) {
            return Lists.newArrayList();
        }
        String res = HttpClient.callHttpServer(getCodeServer() + "/code/method", "get_method_code", req.getRequirement());
        Type typeOfT = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(res, typeOfT);
    }


    public static EmbeddingStatus embeddingStatus(CodeReq req) {
        String res = HttpClient.callHttpServer(getCodeServer() + "/api/z/open/athena/code/embedding/status", "embedding_status", gson.toJson(req));
        Type typeOfT = new TypeToken<ZResult<EmbeddingStatus>>() {
        }.getType();
        ZResult<EmbeddingStatus> zres = gson.fromJson(res, typeOfT);
        return zres.getData();
    }


    //上传代码到服务器(那边的服务器会计算向量)
    public static void uploadCode(CodeReq req) {
        if (!openCodeServer) {
            return;
        }
        List<AthenaPair<AthenaClassInfo, String>> list = parseCode(req.getCode()).stream().collect(Collectors.toList());
        req.setData(gson.toJson(list));
        HttpClient.asyncCallHttpServer(getCodeServer() + "/api/z/open/athena/code/embedding", "upload_code", gson.toJson(req), (msg) -> NotificationCenter.notice(null, "biz code upload success", true));
    }

    //获取和这次业务相关的代码(根据需求取回相应的代码片段)
    public static List<ZKnowledgeRes> getCodeList(CodeReq req) {
        if (!openCodeServer) {
            return Lists.newArrayList();
        }
        log.info("project name:{} module name:{}", req.getProjectName(), req.getModuleName());
        try {
            String res = HttpClient.callHttpServer(getCodeServer() + "/api/z/open/athena/code/similarQuery", "get_code", gson.toJson(req));
            Type typeOfT = new TypeToken<ZResult<List<ZKnowledgeRes>>>() {
            }.getType();
            ZResult<List<ZKnowledgeRes>> result = gson.fromJson(res, typeOfT);
            return result.getData();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return Lists.newArrayList();
    }


    public static List<ZKnowledgeRes> callSimilar(CodeReq req) {
        if (!openCodeServer) {
            return Lists.newArrayList();
        }
        String res = HttpClient.callHttpServer(getCodeServer() + "/api/z/open/athena/code/similarQuery3", "get_code", gson.toJson(req));
        Type typeOfT = new TypeToken<ZResult<List<ZKnowledgeRes>>>() {
        }.getType();
        ZResult<List<ZKnowledgeRes>> result = gson.fromJson(res, typeOfT);
        return result.getData();
    }


}
