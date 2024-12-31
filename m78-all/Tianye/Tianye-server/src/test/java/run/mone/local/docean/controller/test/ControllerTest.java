package run.mone.local.docean.controller.test;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import run.mone.local.docean.controller.TestController;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/2/24 09:16
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.local.docean","com.xiaomi.youpin"})
public class ControllerTest {

    @Resource
    private TestController tc;

    @org.junit.jupiter.api.Test
    public void testGetKnowledgeIdByUserName() {
        Long expected = 2061L; // 假设这是我们期望的返回值
        Long actual = tc.testGetKnowledgeIdByUserName();
        assertEquals(expected, actual);
    }


    @org.junit.jupiter.api.Test
    public void testGetPromptIdByUserName() {
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin.docean.plugin");
        TestController tc = Ioc.ins().getBean(TestController.class);
        Long expected = 71239L; // 假设这是我们期望的返回值
        Long actual = tc.testGetPromptIdByUserName();
        assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    public void testTestUserByToken() {
        // 初始化IOC容器，这里假设已经在测试类的其他地方进行了初始化
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");

        // 通过IOC容器获取TestController的实例
        TestController testController = Ioc.ins().getBean(TestController.class);

        // 调用testUserByToken方法并获取结果
        String expected = "zhangping17"; // 假设这是我们期望的返回值
        String actual = testController.testUserByToken();

        // 断言结果是否符合预期
        assertEquals(expected, actual);
    }

    @Test
    public void testWxMsgSend() {
        // 初始化IOC容器，这里假设已经在测试类的其他地方进行了初始化
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");

        // 通过IOC容器获取TestController的实例
        TestController testController = Ioc.ins().getBean(TestController.class);

        // 调用wxMsgSend方法并获取结果
        String expected = "success"; // 假设这是我们期望的返回值
        String actual = testController.wxMsgSend();

        // 断言结果是否符合预期
        assertEquals(expected, actual);
    }


}
