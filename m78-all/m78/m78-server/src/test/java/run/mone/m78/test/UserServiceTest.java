package run.mone.m78.test;

import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.user.UserConfig;
import run.mone.m78.service.bo.user.UserInfoVo;
import run.mone.m78.service.dao.mapper.UserConfigMapper;
import run.mone.m78.service.dao.entity.UserConfigPo;
import run.mone.m78.service.dao.entity.table.UserConfigPoTableDef;
import run.mone.m78.service.service.cache.CacheService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author goodjava@qq.com
 * @date 2024/1/25 18:17
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    @MockBean
    private CacheService cacheService;

    @Resource
    @MockBean
    private UserConfigMapper userConfigMapper;

    @Test
    public void testGetUserConfigWithCache() {
        String userName = "testUser";
        UserConfig expectedConfig = UserConfig.builder().build();
        expectedConfig.setId(123);

        // Mocking the cacheService behavior
        when(cacheService.get(userName)).thenReturn(expectedConfig);

        // Test the method with useCache = true
        UserConfig actualConfig = userService.getUserConfig(userName, true);
        assertEquals(expectedConfig, actualConfig);

        // Verify that the cacheService.get was called
        verify(cacheService).get(userName);
    }

    @Test
    public void testGetUserConfigWithoutCacheAndUserExists() {
        String userName = "testUser";
        UserConfigPo userConfigPo = new UserConfigPo();
        userConfigPo.setId(123);

        // Mocking the userConfigMapper and cacheService behavior
        when(userConfigMapper.selectOneByCondition(UserConfigPoTableDef.USER_CONFIG_PO.USER_NAME.eq(userName))).thenReturn(userConfigPo);
        when(cacheService.get(userName)).thenReturn(null);

        // Test the method with useCache = false
        UserConfig actualConfig = userService.getUserConfig(userName, false);
        assertNotNull(actualConfig);
        // Verify that the cacheService.set was called
        verify(cacheService).set(eq(userName), any(UserConfig.class));
    }

    @Test
    public void testGetUserConfigWithoutCacheAndUserNotExists() {
        String userName = "testUser";

        // Mocking the userConfigMapper and cacheService behavior
        when(userConfigMapper.selectOneByCondition(UserConfigPoTableDef.USER_CONFIG_PO.USER_NAME.eq(userName))).thenReturn(null);
        when(cacheService.get(userName)).thenReturn(null);

        // Test the method with useCache = false
        UserConfig actualConfig = userService.getUserConfig(userName, false);
        assertNotNull(actualConfig);

        // Verify that the cacheService.set was called with a new UserConfig containing the defaultModelConfig
        verify(cacheService).set(eq(userName), any(UserConfig.class));
    }

    @Test
    public void testGetUserInfo() {
        // Mock the static UserUtil.getUser method to return a mock AuthUserVo
        AuthUserVo mockUserVo = new AuthUserVo();
        mockUserVo.setName("TestUser");
        mockUserVo.setEmail("test@example.com");
        mockUserVo.setAccount("testAccount");
        mockUserVo.setAvatarUrl("http://test.com/avatar.jpg");
        // Assume isAdministrator and getUserOrgInfo methods are properly mocked to return expected values
        // Assume getUserZToken method is properly mocked to return expected value

        when(UserUtil.getUser()).thenReturn(mockUserVo);

        // Call the method to test
        UserInfoVo actualUserInfo = userService.getUserInfo();

        // Assertions to verify the UserInfoVo fields are correctly set based on the mocked AuthUserVo
        assertNotNull(actualUserInfo);
        assertEquals("TestUser", actualUserInfo.getDisplayName());
        assertEquals("test@example.com", actualUserInfo.getEmail());
        assertEquals("testAccount", actualUserInfo.getUsername());
        assertEquals("http://test.com/avatar.jpg", actualUserInfo.getAvatar());
        // Add more assertions if necessary to cover all fields

        // Verify that the static method was called
        UserUtil.getUser();
    }

    @Test
    public void testIsAdministratorWithAdminUser() {
        // 创建一个模拟的管理员用户
        AuthUserVo mockAdminUser = new AuthUserVo();
        mockAdminUser.setAccount("dingpei");
        mockAdminUser.setUserType(0);

//        // 创建一个模拟的管理者列表，包含上面的管理员用户
//        List<NodeUserRelVo> mockManagers = new ArrayList<>();
//        NodeUserRelVo mockManager = new NodeUserRelVo();
//        mockManager.setAccount("adminUser");
//        mockManagers.add(mockManager);


        // 调用isAdministrator方法并断言结果为true，因为用户是管理员
        boolean result = userService.isAdministrator(mockAdminUser);
        assertTrue(result);
    }


}
