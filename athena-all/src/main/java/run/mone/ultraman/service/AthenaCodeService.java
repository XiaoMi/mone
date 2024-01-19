package run.mone.ultraman.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.MessageConsumer;
import run.mone.m78.ip.bo.z.EmbeddingStatus;
import run.mone.m78.ip.bo.z.ZKnowledgeRes;
import run.mone.m78.ip.bo.z.ZResult;
import run.mone.m78.ip.common.NotificationCenter;
import run.mone.m78.ip.service.CodeService;
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
        return null;
    }


    //解析代码，返回一个列表，每个元素包含一个类信息和对应的代码字符串
    public static List<AthenaPair<AthenaClassInfo, String>> parseCode(String code) {
        return null;
    }

    public static AthenaClassInfo classInfo(String code) {
        return null;
    }


    public static String parseAndSkipCode(String code) {
        return "";
    }

    //解析方法代码，返回一个包含AthenaMethodInfo对象的列表
    public static List<AthenaMethodInfo> parseMethodCode(String code) {
        return null;
    }

    //解析方法代码，返回一个包含AthenaMethodInfo对象的列表
    public static List<AthenaFieldInfo> parseFieldCode(String code) {
        return null;
    }


    //使用MD5算法对输入的字符串进行哈希计算
    public static String md5(String code) {
        return Hashing.md5().hashString(code, StandardCharsets.UTF_8).toString();
    }

    //上传方法代码的实现
    public static void uploadMethodCode(CodeReq req) {
    }

    //获取方法代码列表
    public static List<String> getMethodCodeList(CodeReq req) {
        return null;
    }


    public static EmbeddingStatus embeddingStatus(CodeReq req) {
        return null;
    }


    //上传代码到服务器(那边的服务器会计算向量)
    public static void uploadCode(CodeReq req) {
    }

    //获取和这次业务相关的代码(根据需求取回相应的代码片段)
    public static List<ZKnowledgeRes> getCodeList(CodeReq req) {
        return Lists.newArrayList();
    }


    public static List<ZKnowledgeRes> callSimilar(CodeReq req) {
        return null;
    }


}
