package run.mone.knowledge.service.rpc.dto;

import run.mone.knowledge.api.dto.DemoReqDto;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DemoDepReqDto {

    private Long id;

    public static DemoDepReqDto build(DemoReqDto reqDto) {
        DemoDepReqDto demoDepReqDto = new DemoDepReqDto();
        demoDepReqDto.id = reqDto.getId();
        return demoDepReqDto;
    }

}
