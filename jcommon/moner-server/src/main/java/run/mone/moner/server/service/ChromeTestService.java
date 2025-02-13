package run.mone.moner.server.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 11:20
 */
@Service
@Slf4j
public class ChromeTestService {


    //测试prompt
    public String prompt(String prompt, LLM llm) {
        String d = "帮我搜索下苹果笔记本";
        String ask = AiTemplate.renderTemplate(prompt, ImmutableMap.of("data", d));
        AiMessage msg = AiMessage.builder().build();
        String answer = llm.ask(Lists.newArrayList(msg));
        log.info("answer:{}", answer);
        return answer;
    }

    //打开新的tag
    public String openTag(JsonObject req, JsonObject res) {
        String dataStr = """
                 <action type="createNewTab" url="https://www.jd.com/" auto="true">
                            打开京东
                 </action>
                """;
        res.addProperty("data", dataStr);
        return res.toString();
    }


    //滚动一屏
    public String scrollOneScreen(JsonObject req, JsonObject res) {
        res.addProperty("data", """
                //滚动屏幕
                <action type="scrollOneScreen">
                </action>
                //暂停
                <action type="pause">
                </action>
                
                <action type="cancelBuildDomTree">
                </action>
                
                <action type="pause">
                </action>
                
                <action type="buildDomTree">
                </action>
               
                <action type="pause">
                </action>
                
                //截图 并且 把截图回传回来
                <action type="screenshot" send="true" test="true">
                </action>
                
                
                """);
        return res.toString();
    }

    //添加一个invoke 方法,会根据方法名在这个类里找到相应方法,参数永远是(JsonObject req, JsonObject res), 返回永远是 String(class)
    public String invoke(String methodName, JsonObject req, JsonObject res) {
        return invokeMethod(this, methodName, req, res);
    }

    //给定Object 和 method 反射执行 这个method ,参数是 (JsonObject req, JsonObject res) (class)
    public String invokeMethod(Object obj, String methodName, JsonObject req, JsonObject res) {
        try {
            Method method = obj.getClass().getMethod(methodName, JsonObject.class, JsonObject.class);
            return (String) method.invoke(obj, req, res);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error invoking method: {}", methodName, e);
            throw new RuntimeException("Error invoking method: " + methodName, e);
        }
    }


}
