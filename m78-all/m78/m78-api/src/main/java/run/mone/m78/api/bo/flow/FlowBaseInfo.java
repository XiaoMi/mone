package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.enums.FlowRunStatusEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowBaseInfo implements Serializable {

    private Integer id;

    private Long workSpaceId;

    private String name;

    private String creator;

    private String desc;

    //0 未删除 1 已删除
    private Integer state;

    private Integer publishStatus;

    /**
     * @see FlowRunStatusEnum
     */
    private Integer runStatus;

    //头像地址
    private String avatarUrl;

    private List<NodeInputInfo> inputs;

    private List<NodeOutputInfo> outputs;

    private Long ctime;

    private Long utime;

}
