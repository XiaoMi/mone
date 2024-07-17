package run.mone.knowledge.service.rpc.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DemoDepResDto {

    private String test1;

    public static DemoDepResDto build() {
        DemoDepResDto resDto = new DemoDepResDto();
        resDto.test1 = "test1";
        return resDto;
    }
}
