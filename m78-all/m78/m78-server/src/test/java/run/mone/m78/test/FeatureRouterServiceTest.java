package run.mone.m78.test;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.FeatureRouterProvider;
import run.mone.m78.api.bo.datasource.SqlQueryRes;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.FlowTestRecordPo;
import run.mone.m78.service.service.feature.router.FeatureRouterService;
import run.mone.m78.service.service.flow.FlowRecordService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/21/24 11:06
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class FeatureRouterServiceTest {

    @Resource
    private FeatureRouterService featureRouterService;

    @Resource
    private FlowRecordService flowRecordService;

    @Resource
    private FeatureRouterProvider featureRouterProvider;

    @Test
    public void testFeatureRouterServiceQuery() {
        // 构建请求对象FeatureRouterReq，这里需要根据实际情况来填充请求数据
        FeatureRouterReq req = new FeatureRouterReq();
        req.setId(16L); // 假设的FeatureRouter的ID
        req.setUserName("name"); // 假设的用户名
        Map<String, Object> reqData = new HashMap<>();
        req.setReqData(reqData);

        // 构建预期的结果，这里需要根据实际情况来模拟预期数据
        String expectedResults = "[{\"text_before\":\"\",\"text_after\":\"\",\"creator\":\"\",\"t\":\"[\\\"a\\\", \\\"b\\\"]\",\"new_column1\":\"\",\"id\":1000,\"new_column\":\"default_value\",\"status\":\"1\"}]";

        // 调用测试方法
        Result<List<Map<String, Object>>> result = featureRouterService.query(req);

        // 验证返回的结果是否符合预期
        assertNotNull(result);
        assertEquals(GeneralCodes.OK.getCode(), result.getCode());
        assertEquals(expectedResults, GsonUtils.gson.toJson(result.getData().get(0).get("data")));
    }

    @Test
    public void testFeatureRouterServiceQuery2() {
        // 构建请求对象FeatureRouterReq，这里需要根据实际情况来填充请求数据
        FeatureRouterReq req = new FeatureRouterReq();
        req.setId(22L); // 假设的FeatureRouter的ID
        req.setUserName("name"); // 假设的用户名
        Map<String, Object> reqData = new HashMap<>();
        req.setReqData(reqData);

        // 调用测试方法
        Result<List<Map<String, Object>>> result = featureRouterService.query(req);
        System.out.println(result);

    }

    @Test
    public void testListAllFeatureRouter() {
        // 构建请求对象FeatureRouterReq，这里需要根据实际情况来填充请求数据
        FeatureRouterReq req = new FeatureRouterReq();
//        req.setId(null); // 为了测试全量查询，这里不设置ID
//        req.setLabelId(null); // 不设置LabelId
        req.setType(1); // 设置Type
//        req.setName(null); // 不设置Name
        req.setUserName("name"); // 设置UserName
//        req.setPage(null); // 不设置分页，以便全量查询
//        req.setPageSize(null); // 不设置分页大小

        // 调用需要测试的方法
        Pair<Long, List<FeatureRouterDTO>> result = featureRouterProvider.listAllFeatureRouter(req);

        // 验证返回的结果是否符合预期
        assertNotNull(result);
        assertNotNull(result.getRight());
        Assertions.assertTrue(result.getLeft() >= 0); // 确保返回的记录数是非负数
        // 如果有具体的预期结果，可以进一步比较
        // assertEquals(expectedTotalCount, result.getLeft().longValue()); // 预期的总记录数
        // assertEquals(expectedFeatureRouterDTOList, result.getRight()); // 预期的FeatureRouterDTO列表
    }

    @Test
    public void testGetFeatureRouterById() {
        Long id = 40L;
        // 调用需要测试的方法
        FeatureRouterDTO result = featureRouterProvider.getFeatureRouterDetailById(id);
        // 验证返回的结果是否符合预期
        assertNotNull(result);
        Assertions.assertTrue(result.getUserName().equals("name")); // 确保返回的记录数是非负数
    }
}
