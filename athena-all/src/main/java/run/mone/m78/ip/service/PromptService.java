package run.mone.m78.ip.service;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.Message;
import run.mone.m78.ip.bo.PromptContext;
import run.mone.m78.ip.bo.RobotContext;
import run.mone.m78.ip.util.HintUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static run.mone.m78.ip.util.PromptUtils.*;

/**
 * @author goodjava@qq.com
 * @author baoyu
 * @date 2023/5/11 21:53
 * <p>
 * 这个类主要处理Prompt在Idea中的落地(写入编辑器)
 */
@Slf4j
public class PromptService {

    private static Gson gson = new Gson();

    /**
     * 动态执行
     *
     * @param req
     * @return
     */
    public static String dynamicInvoke(GenerateCodeReq req) {
        setReq(req);
        switch (req.getPromptType()) {
            case createClass -> createClass(req.getProject(), req.getModule().getName(), req.getPromptName());
            case createClass4 -> createClass4(req);
            case createMethod -> createMethod(req);
            case createMethod2 -> createMethod2(req);
            case comment -> addComment(req);
            case lineByLineComment -> lineByLineCommentOrCode(req.getPromptName(), req);
            case createFile -> createFile(req.getPromptName(), req.getFileName(), req);
            case modifyClass -> updateClass(req);
            case modifyMethod -> updateMethod(req);
            case select -> select(req, req.getProject(), req.getModule(), req.getPromptInfo());
            case removeComment -> removeComment(req.getProject());
            case showInfo -> showInfo(req);
            case repleaceSelectContent -> repleaceSelectContent(req);
            case testPrompt -> testPrompt(req);
            case checkPomVersion -> checkPomVersion(req);
            case generateBootStrapAnno -> generateAnnoForBootStrap(req.getPromptName(), req.getProject());
            case inlayHint -> inlayHint(req);
            case genBizMethodCode -> genBizMethodCode(req);
            case generateMethod -> generateMethod(req, req.getProject(), "");
            case bot -> RobotService.bot(req);
            case generateMiapiMethod -> generateMiapiMethod(req);
            case generateInterface -> generateInterface(req);
            case question -> question(req);
            case createClass2 ->
                    createClass2(req.getProject(), req.getPromptName(), req.getShowDialog(), req.getParam());
            default -> {
                return "UnSupport";
            }
        }
        return "ok";
    }

    private static void createClass4(GenerateCodeReq req) {

    }

    //ai会向你提问(绝大部分分多步的操作,ai问你更合适)
    private static void question(GenerateCodeReq req) {

    }

    //后边代码尽量用这里获取的内容,editor中的内容放入到req中,避免后边的再次获取
    public static void setReq(GenerateCodeReq req) {

    }

    private static String getClassPackage(String qualifiedName) {
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            return qualifiedName.substring(0, lastDotIndex);
        }
        return "";
    }


    //获取编辑器的注释信息
    private static String getComment(Editor editor) {
        return getComment(editor, "//biz:");
    }

    private static String getComment(Editor editor, String str) {
        return "";
    }


    /**
     * 生成业务方法
     *
     * @param req
     */
    public static void genBizMethodCode(GenerateCodeReq req) {

    }

    private static String addCode(GenerateCodeReq req, Map<String, String> m, PromptContext context) {
        return "";
    }


    //添加上下文信息到映射中
    private static void addContext(GenerateCodeReq req, Map<String, String> m, PromptContext promptContext) {

    }

    public static String getResourceFromAi(Project project, Map<String, String> m, PromptContext promptContext, List<String> resourceCode) {
        return "";
    }


    public static void inlayHint(GenerateCodeReq req) {

    }

    /**
     * 测试prompt的接口
     *
     * @param req
     */
    private static void testPrompt(GenerateCodeReq req) {

    }

    private static void repleaceSelectContent(GenerateCodeReq req) {

    }

    /**
     * 在聊天窗口显示信息
     *
     * @param req
     */
    public static void showInfo(GenerateCodeReq req) {

    }


    public static void generateMethod(Project project, String content) {

    }


    //创建方法都收口在这里了
    public static void generateMethod(GenerateCodeReq req) {

    }

    private static void initAiReqMap(GenerateCodeReq req, Map aiReqMap) {

    }

    private static void analysisScope(GenerateCodeReq req, PromptContext context) {

    }

    private static void addField(GenerateCodeReq req, Map<String, String> map) {

    }

    private static void addClass(GenerateCodeReq req, Map<String, String> map, PromptContext context) {

    }


    public static String getCode(GenerateCodeReq req, PromptContext context) {
       return "";
    }

    /**
     * 删除逐行注释
     *
     * @param project
     */
    private static void removeComment(Project project) {
        PsiMethod psiMethod = CodeService.getMethod(project);
        Editor editor = CodeService.getEditor(project);
        if (null == psiMethod) {
            HintUtils.show(editor, Message.selectMethodMsg, true);
            return;
        }
        PsiMethodUtils.deleteCommentsFromMethod(project, psiMethod, comment -> comment.getText().startsWith("//Athena:"));
    }



    /**
     * 生成方法(在edit中)
     *
     * @param project
     * @param text
     */
    public static void generateMethod(GenerateCodeReq req, Project project, String text) {

    }


    private static void createMethod(GenerateCodeReq req) {

    }

    /**
     * stream样式添加注释
     */
    public static void comment2(RobotContext context, GenerateCodeReq req) {
    }

    /**
     * 给出一些修改代码的意见
     */
    public static void modifyCodeSuggest(RobotContext context, GenerateCodeReq req) {
    }

    /**
     * 需要导入那些依赖
     *
     * @param context
     * @param req
     */
    public static void getImport(RobotContext context, GenerateCodeReq req) {
        req.setPromptName("get_import");
        addComment(req);
    }


    /**
     * 根据类生成sql建表文件
     *
     * @param context
     * @param req
     */
    public static void createTabStatement(RobotContext context, GenerateCodeReq req) {
        createFile("create_tab_statement", "tmp.sql", req);
    }

    /**
     * 把class转换为json文件
     *
     * @param context
     * @param req
     */
    public static void classJson(RobotContext context, GenerateCodeReq req) {
        createFile("class_json", "tmp.json", req);
    }


    /**
     * 根据业务需求直接生成代码
     *
     * @param context
     * @param req
     */
    public static void bizCodeGen(RobotContext context, GenerateCodeReq req) {
    }




    /**
     * 创建方法2
     * <p>
     * 会打开两个表单
     * 1.填参数
     * 2.选中类和方法
     *
     * <p>
     * 选择类和方法,然后插入代码
     * <p>
     * 可以用来生成Controller中的方法
     *
     * @param req
     */
    public static void createMethod2(GenerateCodeReq req) {


    }

    public static void addFields(Project project, String serviceName, String shortServiceName, PsiClass psiClass, Editor editor) {
    }

    public static void addImports(GenerateCodeReq req, Project project, String serviceName, String reqClass, Editor editor) {
    }

    public static void addImports(GenerateCodeReq req, Project project, String serviceName, String reqClass, Editor editor, boolean unitTest, boolean resource, String unitVersion) {

    }

    /**
     * 给interface中添加方法(返回值可能被包装要注意)
     *
     * @param psiClass
     */
    private static void addMethodToInterface(GenerateCodeReq req, PsiClass psiClass, PsiMethod pm, PsiClass reqClass) {

    }

    private static void modifyReq(GenerateCodeReq req, PsiMethod pm, String reqClass) {

    }

    private static void setLabels(GenerateCodeReq req, Map<String, Object> map) {

    }


    /**
     * 生成使用中间件的代码
     *
     * @param context
     * @param req
     */
    public static void midCodeGen(RobotContext context, GenerateCodeReq req) {
    }

    /**
     * 逐行注释
     *
     * @param context
     * @param req
     */
    public static void cc(RobotContext context, GenerateCodeReq req) {
    }

    /**
     * fix(问题修复,给出来的是建议)
     *
     * @param context
     * @param req
     */
    public static void fix(RobotContext context, GenerateCodeReq req) {
    }

    //暂时只生成接口引入
    public static void generateMiapiMethod(GenerateCodeReq req) {

    }

}
