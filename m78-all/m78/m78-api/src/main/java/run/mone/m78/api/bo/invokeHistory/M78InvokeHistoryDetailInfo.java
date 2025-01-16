package run.mone.m78.api.bo.invokeHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78InvokeHistoryDetailInfo {

    private Long id;

    private Integer type; // 类型, 1bot, 2flow, 3plugin

    private Long relateId;

    private String inputs;

    private String outputs;

    private Long invokeTime;

    private Integer invokeWay; // 调用方式, 1页面, 2接口, 3系统内部, 4调试等等

    private String invokeUserName;
}
