package run.mone.local.docean.controller.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import run.mone.local.docean.service.M78Service;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2024/3/7 10:49
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.local.docean", "com.xiaomi.youpin"})
public class M78ServiceTest {

    @Resource
    private M78Service m78Service;


    @Test
    public void testAsk() {
        String question = "今天北京天气怎么样?";
//        String question = "hi";
        String history = "";
        String knowledge = "";
        String plugin = "[{\"desc\":\"查询某个地方的天气\",\"function\":\"weather\",\"params\":{\"location\":\"那个地方\"}}]";
        Object obj = m78Service.ask2(question, history, knowledge, plugin,  null);
        System.out.println(obj);
    }


}
