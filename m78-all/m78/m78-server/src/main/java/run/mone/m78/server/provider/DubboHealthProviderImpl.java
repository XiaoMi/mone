package run.mone.m78.server.provider;

import com.xiaomi.data.push.common.Health;
import run.mone.m78.api.DubboHealthProvider;
import run.mone.m78.api.constant.CommonConstant;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.qps.QpsAop;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version="1.0")
@Slf4j
public class DubboHealthProviderImpl implements DubboHealthProvider {

    @Autowired
    private QpsAop qpsAop;

    @Override
    public Result<Health> health() {
        long qps = qpsAop.getQps();
        return Result.success(new Health("0.0.1", "2024-11-11", qps));
    }
}
