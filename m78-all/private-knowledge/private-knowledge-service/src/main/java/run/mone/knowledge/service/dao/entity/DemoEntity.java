package run.mone.knowledge.service.dao.entity;

import run.mone.knowledge.api.dto.DemoResDto;
import run.mone.knowledge.service.rpc.dto.DemoDepResDto;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DemoEntity {

    private String test2;

    public DemoResDto buildDemoResDto(DemoDepResDto rpcResDto) {
        DemoResDto dto = new DemoResDto();
        dto.setTest1(rpcResDto.getTest1());
        dto.setTest2(test2);
        return dto;
    }

    public static DemoEntity build() {
        DemoEntity entity = new DemoEntity();
        entity.test2 = "hello world";
        return entity;
    }

}
