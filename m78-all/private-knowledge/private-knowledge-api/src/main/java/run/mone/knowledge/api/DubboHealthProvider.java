package run.mone.knowledge.api;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.data.push.common.Health;

public interface DubboHealthProvider {

    Result<Health> health();
}