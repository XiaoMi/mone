package run.mone.knowledge.service;

import run.mone.knowledge.api.dto.DemoReqDto;
import run.mone.knowledge.api.dto.DemoResDto;
import run.mone.knowledge.service.dao.DemoDao;
import run.mone.knowledge.service.dao.entity.DemoEntity;
import run.mone.knowledge.service.rpc.DemoDepRpc;
import run.mone.knowledge.service.rpc.dto.DemoDepReqDto;
import run.mone.knowledge.service.rpc.dto.DemoDepResDto;
import com.xiaomi.youpin.infra.rpc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    @Autowired
    private DemoDepRpc demoDepRpc;
    @Autowired
    private DemoDao demoDao;

    public Result<DemoResDto> query(DemoReqDto reqDto) {
        //rpc远程调用
        DemoDepResDto rpcResDto = demoDepRpc.remoteReq(DemoDepReqDto.build(reqDto));
        //持久化实体查询
        DemoEntity entity = demoDao.getById(reqDto.getId());
        return Result.success(entity.buildDemoResDto(rpcResDto));
    }
}
