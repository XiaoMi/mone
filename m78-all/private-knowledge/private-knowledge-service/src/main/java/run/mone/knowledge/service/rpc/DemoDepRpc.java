package run.mone.knowledge.service.rpc;

import run.mone.knowledge.service.rpc.dto.DemoDepReqDto;
import run.mone.knowledge.service.rpc.dto.DemoDepResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DemoDepRpc {

    public DemoDepResDto remoteReq(DemoDepReqDto reqDto) {
        log.info("DemoDepRpc.remoteReq 请求{}", reqDto);
        //远程接口请求和响应要转换dto
        DemoDepResDto resDto = DemoDepResDto.build();
        log.info("DemoDepRpc.remoteReq 响应{}", resDto);
        return resDto;
    }

}
