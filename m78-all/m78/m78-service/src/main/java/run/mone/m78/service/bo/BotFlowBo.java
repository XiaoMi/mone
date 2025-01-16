package run.mone.m78.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-07 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotFlowBo implements Serializable {

    private Long id;

    private String name;

    private String avatarUrl;

    private String desc;

    private String inputs;

    private String nodes;

    private String edges;

    //tianye可直接使用的req
    private String tianyeBotReq;

    // flow record id
    private String flowRecordId;
}
