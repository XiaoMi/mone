package run.mone.knowledge.api;

import com.xiaomi.data.push.common.Health;
import run.mone.knowledge.api.dto.DemoReqDto;
import run.mone.knowledge.api.dto.DemoResDto;
import com.xiaomi.youpin.infra.rpc.Result;

public interface DemoProvider {

    Result<DemoResDto> query(DemoReqDto reqDto);
}