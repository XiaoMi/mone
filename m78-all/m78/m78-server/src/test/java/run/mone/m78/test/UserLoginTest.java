package run.mone.m78.test;

import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.bo.user.UserLoginReq;
import run.mone.m78.service.bo.user.UserLoginRes;
import run.mone.m78.service.service.user.UserLoginService;

import javax.annotation.Resource;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 10:27
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class UserLoginTest {

    @Resource
    private UserLoginService userLoginService;

    @Test
    public void userLoginTest() {
        UserLoginReq userLoginReq = new UserLoginReq();
        userLoginReq.setUserName("张三");
        userLoginReq.setPassword("123");
        Result<UserLoginRes> result = userLoginService.login(userLoginReq);
        Assert.assertEquals("登录成功", result.getData().toString());
    }

    @Test
    public void userLoginOutTest() {
        UserLoginReq userLoginReq = new UserLoginReq();
        userLoginReq.setUserName("张三");
        userLoginReq.setPassword("123");
        Result<String> result = userLoginService.loginOut(userLoginReq);
        Assert.assertEquals("退出成功", result.getData().toString());
    }
}
