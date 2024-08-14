package run.mone.z.desensitization.app.test;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import run.mone.z.desensitization.service.common.CodeDesensitizeUtils;
import run.mone.z.desensitization.service.common.CodeExtractorUtils;
import run.mone.z.desensitization.service.common.CodeParseUtils;

import java.util.regex.Pattern;

/**
 * @author wmin
 * @date 2023/6/5
 */
public class MyTest {

    static String codeSnippet = "你是一名杰出的java程序员,我需要你帮我做一个分析和并判断。\n\n诉求如下：\n1.我会给你当前class的信息和一段需求描述，你帮我判断下实现这个需求时对当前项目上下文的依赖程度，\n依赖程度有：method、class、module、project，分别代表不依赖当前项目、依赖当前类、依赖当前模块、依赖当前项目.\n2.返回的结果必须是json格式,key固定位scope，value为依赖程度，如：{\"scope\":\"class\"}\n3.分析实现这个需求对当前项目上下文的依赖程度时有几条经验，供你参考：\n 3.1.请熟知Java项目常见的分module规则(层次划分、DDD等)及其依赖关系,并利用当前所处的位置进行依赖范围判断\n 3.2.如果不需要借助项目上下文就能实现的,则返回:{\"scope\":\"method\"}\n 3.3.如果依赖当前类(调用类的某些方法和常量等)和你的公共知识就能实现的，则返回{\"scope\":\"class\"}\n 3.4.如果判定依赖程度超过class级别，则需要你利用当前所处的类、模块及项目信息进行综合的推理，得出合适的依赖关系。\n4.如果需求描述里告诉了你scope,则你就严格按照我给定的scope\n  4.1.指定scope的格式有两种  (scope:method) 或者 (method)\n\n我给你两个例子供参考：\n例子1：\n当前class:\npublic class A {\n}\n\n当前module:\ndemo-service\n\n项目module列表：\ndemo-api、demo-service、demo-server\n\n需求：\n计算两数和\n\n返回:\n{\"scope\":\"method\"}\n\n例子1解释：\n需求为一个简单的sum需求，不需要依赖当前项目任何上下文就能实现。判定为不依赖\n\n例子2：\n当前class:\npublic class EmbeddingController {\n    @ResponseBody\n    @RequestMapping(value = \"/embedding\", method = RequestMethod.GET)\n    public Result\u003cVoid\u003e embedding(String text);\n}\n\n当前module:\ndemo-server\n\n项目module列表：\ndemo-api、demo-service、demo-server\n\n需求：\n将一个上传的文件内容进行向量化处理，并存储\n\n返回:\n{\"scope\":\"project\"}\n\n例子2解释：\n分析当前class上下文可以判定出需求对本项目是有一定依赖关系的;\n分析当前类名和class信息可以判断出当前类是一个Controller，Controller一般轻逻辑，通过依赖调用来实现具体逻辑；\n分析所在module是demo-server，要实现这个需求，最有可能会调用一个demo-service模块的向量化处理service，同时还有落库Dao层操作；\n综上判断判定依赖为project\n\n\n下面我将给你一个真实的需求，请返回我json判定结果：\n\n当前所在class:\nz_desensitization_start\n@DubboService(interfaceClass = DubboHealthService.class, timeout = 1000, group = \"${dubbo.group}\", version = \"1.0\")\n@Slf4j\npublic class DubboHealthServiceImpl implements DubboHealthService {\n\n    @Override\n    public Result\u003cString\u003e health() {\n        return Result.success(\"ok\");\n    }\n    \n    //计算两数之和\n}\nz_desensitization_end\n当前所在module:\nz-desensitization-app\n\n项目module列表:\nz-desensitization-api,test,z-desensitization-app,z-desensitization-server,z-desensitization-domain,odin.debug,z-desensitization-infrastructure\n\n需求:\n计算两数之和";

    static String codeSnippet2 = "public class DesensitizeReq implements Serializable {\n" +
            "    private String text;\n" +
            "    private List<SensitiveWordConfigBo> sensitiveWordConfigBo;\n" +
            "    private Boolean aiDesensitizeFlag;\n" +
            "    private String ip = \"1.1.1.1\";\n" +
            "\n" +
            "    public String getText() {\n" +
            "        return text;\n" +
            "    }\n" +
            "\n" +
            "    public void setText(String text) {\n" +
            "        this.text = text;\n" +
            "    }\n" +
            "\n" +
            "    public List<SensitiveWordConfigBo> getSensitiveWordConfigBo() {\n" +
            "        return sensitiveWordConfigBo;\n" +
            "    }\n" +
            "\n" +
            "    public void setSensitiveWordConfigBo(List<SensitiveWordConfigBo> sensitiveWordConfigBo) {\n" +
            "        this.sensitiveWordConfigBo = sensitiveWordConfigBo;\n" +
            "    }\n" +
            "\n" +
            "    public Boolean getAiDesensitizeFlag() {\n" +
            "        return aiDesensitizeFlag;\n" +
            "    }\n" +
            "\n" +
            "    public void setAiDesensitizeFlag(Boolean aiDesensitizeFlag) {\n" +
            "        this.aiDesensitizeFlag = aiDesensitizeFlag;\n" +
            "    }\n" +
            "\n" +
            "    //计算两数之和(method)\n" +
            "}";

    @Test
    public void testReg(){
        Pattern pattern = Pattern.compile("(?i)(ip|host|pwd|password|secret|token|credentials|idCard|identityCard)");
        System.out.println(pattern.matcher("IP").matches());
        System.out.println(pattern.matcher("password").matches());
        System.out.println(pattern.matcher("myHo").matches());
        System.out.println(pattern.matcher("idcard").matches());
    }

    @Test
    public void testClassAfterCodeExtractor() throws Exception {
        String codeSnippet1 = codeSnippet;
        Pair<Boolean, String> rst =  CodeExtractorUtils.codeExtractorWithLabel(codeSnippet1);
        System.out.println(rst);
        if (rst.getKey()){
            String[] index = rst.getValue().split("-");
            String code = codeSnippet1.substring(Integer.parseInt(index[0]), Integer.parseInt(index[1]));
            System.out.println(code);
            System.out.println("isClass:"+CodeParseUtils.isClass(code));
            CodeDesensitizeUtils.codeDesensitizeForClass(code);
        }
    }

    @Test
    public void testClass2() throws Exception {
        CodeDesensitizeUtils.codeDesensitizeForClass(codeSnippet2);
    }

    @Test
    public void testMethod() throws Exception {
        String codeSnippet = "public void setActionConfMap(Map<String, ActionInfo> actionConfMap) {\n" +
                "        String password = \"12345\";\n" +
                "        this.actionConfMap = actionConfMap;\n" +
                "    }";
        //System.out.println(CodeParseUtils.isClass(codeSnippet));
        System.out.println(CodeParseUtils.isMethod(codeSnippet));
        System.out.println(CodeDesensitizeUtils.codeDesensitizeForClass(codeSnippet));
    }

    @Test
    public void testCodeExtractor() throws Exception {
        String codeSnippet = "testtttttt public void setActionConfMap(Map<String, ActionInfo> actionConfMap) {\n" +
            "        this.actionConfMap = actionConfMap;\n" +
                "    String ip = \"12.0.0.1\";\n" +
            "    }  \n这是一些文本.....";
        codeSnippet = "wwwwww wewewdsc\nwewfedscfds public static void main(String[] args) {\n     String password = \"1\";\n        one(password);//arg:JCIdent\n        one(\"qwqw\");//arg:JCLiteral\n        two(\"qwqw\", password);\n        System.out.println(\"ddddeeee\");//arg:JCLiteral\n    }asdsfwwwwww wewewdsc ";
        Pair<Boolean, String> rst =  CodeExtractorUtils.codeExtractor(codeSnippet);
        if (rst.getKey()){
            String[] index = rst.getValue().split("-");
            String r = CodeDesensitizeUtils.codeDesensitizeForClass(codeSnippet.substring(Integer.parseInt(index[0]), Integer.parseInt(index[1])+1));
            StringBuilder sb = new StringBuilder(codeSnippet);
            sb.replace(Integer.parseInt(index[0]), Integer.parseInt(index[1])+2, r);
            System.out.println("-==-==");
            System.out.println(sb);
        }
    }
}
