package run.mone.m78.test;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.code.editor.EditorPath;
import run.mone.m78.service.service.api.Context;
import run.mone.m78.service.service.code.editor.CodeEditorService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/1/16 14:29
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class CodeEditorTest {


    @Resource
    private CodeEditorService codeEditorService;


    @Test
    public void testGit() {
        List<EditorPath> res = codeEditorService.getProjectTree("https://127.0.0.1", "token", "sync");
        System.out.println(new Gson().toJson(res));
    }

    @Test
    public void testGitFileRead() {
        String file = codeEditorService.getGitFile("https://127.0.0.1", "token", "pom.xml", "sync");
        System.out.println(file);
    }

    @Test
    public void testGitFileUpdate() {
        boolean file = codeEditorService.updateGitFile("https://127.0.0.1", "token", "README.md", "sync","123456","test");
        System.out.println(file);
    }
    @Test
    public void testGetScript() {
        String file = codeEditorService.getScript((Long)24L);
        System.out.println(file);
    }

    @Test
    public void testRunScript() {
        Map<String,Object> m=new HashMap<>();
        m.put("x",4);
        Object file = codeEditorService.runGroovy(m,"x*3");
        System.out.println(file);
    }

    @Test
    public void testRunScript2() throws InstantiationException, IllegalAccessException {
        Map<String,String> m=new HashMap<>();
        m.put("first","1");
        m.put("second","2");
        List<Object> l=new ArrayList<>();
        l.add(m);
        l.add(new Context());
        Object file = codeEditorService.runGroovy(l.toArray(),"import run.mone.m78.service.service.api.Context\n" +
                "import run.mone.m78.service.bo.ApiResult\n" +
                "import com.google.gson.JsonObject\n" +
                "import java.lang.NumberFormatException\n" +
                "\n" +
                "def execute(Map<String, String> params, Context context) {\n" +
                "    // 获取两个数值参数\n" +
                "    String firstParam = params.get('first')\n" +
                "    String secondParam = params.get('second')\n" +
                "\n" +
                "    // 转换参数为整数\n" +
                "    int firstNumber, secondNumber\n" +
                "    try {\n" +
                "        firstNumber = firstParam?.toInteger()\n" +
                "        secondNumber = secondParam?.toInteger()\n" +
                "    } catch (NumberFormatException e) {\n" +
                "        return ApiResult.builder()\n" +
                "            .code(400)\n" +
                "            .message('Invalid input: Please provide valid integers.')\n" +
                "            .data(null)\n" +
                "            .build()\n" +
                "    }\n" +
                "\n" +
                "    // 计算两数之和\n" +
                "    int sum = firstNumber + secondNumber\n" +
                "\n" +
                "    // 创建返回的JSON对象\n" +
                "    JsonObject resultData = new JsonObject()\n" +
                "    resultData.addProperty('sum', sum)\n" +
                "\n" +
                "    // 封装ApiResult\n" +
                "    return ApiResult.builder()\n" +
                "        .code(200)\n" +
                "        .message('The sum of the two numbers is calculated successfully.')\n" +
                "        .data(resultData)\n" +
                "        .build()\n" +
                "}");
        System.out.println(file);
    }
}
