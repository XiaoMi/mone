package run.mone.m78.test;

import com.mybatisflex.core.paginate.Page;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.BotPluginProvider;
import run.mone.m78.api.bo.plugins.BotPluginDTO;
import run.mone.m78.api.bo.plugins.PluginReq;
import run.mone.m78.service.dao.entity.M78BotPlugin;
import run.mone.m78.service.service.plugins.BotPluginService;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 19:27
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class BotPluginServiceTest {


    @Resource
    private BotPluginService botPluginService;

    @Resource
    private BotPluginProvider botPluginProvider;


    @Test
    public void testGetM78BotPluginById() {
        Long invalidId = 1L;

        // 测试空ID的情况
        Result<BotPluginDTO> resultWithNullId = botPluginService.getM78BotPluginById(invalidId);
        System.out.println(resultWithNullId);
    }

    @Test
    public void testSaveOrUpdateM78BotPlugin() {
        // 创建一个有效的M78BotPlugin对象
        M78BotPlugin validPlugin = new M78BotPlugin();
        validPlugin.setName("TestPlugin");
        // 其他必要的设置...

        // 测试保存有效的M78BotPlugin对象
        Result<Long> resultWithValidPlugin = botPluginService.saveOrUpdateM78BotPlugin("曹宝玉", validPlugin);
        System.out.println(resultWithValidPlugin);
    }

    @Test
    public void testGetPageData() {
        Result<Page<BotPluginDTO>> pageDataResult = botPluginService.listM78BotPluginsByRequest(PluginReq.builder().pageNum(1).pageSize(10).build(), null);
        System.out.println(pageDataResult);
    }

    @Test
    public void testGetBotPluginById() {
        Long validId = 3L; // 假设这是一个有效的ID
        Long invalidId = null; // 无效的ID，应触发异常

        // 测试有效ID的情况
        BotPluginDTO resultWithValidId = botPluginProvider.getBotPluginById(validId);
        assertNotNull(resultWithValidId);
        assertEquals(validId, resultWithValidId.getId());

        // 测试无效ID的情况（期望抛出IllegalArgumentException异常）
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            botPluginProvider.getBotPluginById(invalidId);
        });
        String expectedMessage = "须传递plugin的id";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetBotPlugins() {
        // 构建请求参数
        PluginReq req = new PluginReq();
        req.setId(null); // 测试不通过ID过滤
        req.setName("TestPlugin"); // 测试通过名称过滤
        req.setPageNum(1); // 测试分页参数
        req.setPageSize(10);

        // 调用待测试的方法
        Pair<Long, List<BotPluginDTO>> result = botPluginProvider.getBotPlugins(req);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getRight());
        assertTrue(result.getLeft() >= 0); // 确保返回的总数不为负数
        assertTrue(req.getPageSize().intValue() >= result.getRight().size()); // 验证返回的列表大小是否与请求的页面大小匹配

        // 验证返回的数据是否符合过滤条件
        for (BotPluginDTO dto : result.getRight()) {
            assertEquals("TestPlugin", dto.getName());
        }

        // 打印结果，便于调试
        System.out.println(result);
    }


}
