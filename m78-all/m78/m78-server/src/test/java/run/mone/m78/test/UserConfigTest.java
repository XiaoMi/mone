package run.mone.m78.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.model.ModelConfig;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.dao.mapper.UserConfigMapper;
import run.mone.m78.service.dao.entity.UserConfigPo;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class UserConfigTest {

    @Resource
    private UserConfigMapper userConfigMapper;

    @Autowired
    private UserService userService;

    @Test
    public void testInsert() {
        ModelConfig modelConfig = ModelConfig.builder().chatModel("gpt35_16").build();
        UserConfigPo data = UserConfigPo.builder().userName("name").modelConfig(modelConfig).build();
        userConfigMapper.insert(data);
    }

    @Test
    public void testGet() {
        System.out.println(userService.getUserConfig("name").getTranslateModel(Config.model));
    }
    @Test
    public void testList() {
        System.out.println(userConfigMapper.selectAll());
    }


}
