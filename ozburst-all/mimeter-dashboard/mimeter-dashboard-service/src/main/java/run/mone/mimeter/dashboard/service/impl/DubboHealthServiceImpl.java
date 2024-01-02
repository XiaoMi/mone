package run.mone.mimeter.dashboard.service.impl;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import org.apache.dubbo.config.annotation.DubboService;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.common.TestReq;
import run.mone.mimeter.dashboard.service.DubboHealthService;

@DubboService(group = "${dubbo.group}",version = "${dubbo.version}")
@ApiModule(value = "mimeter的健康检测服务", apiInterface = DubboHealthService.class)
public class DubboHealthServiceImpl implements DubboHealthService {
    @Override
    @ApiDoc(value = "health方法")
    public String health(TestReq testReq) {
        return testReq.getUid();
    }

    @Override
    public Result<TestReq> getUid() {
        TestReq req = new TestReq();
        req.setUid("dzx");
        return Result.success(req);
    }
}
