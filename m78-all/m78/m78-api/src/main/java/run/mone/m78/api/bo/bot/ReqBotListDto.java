package run.mone.m78.api.bo.bot;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqBotListDto implements Serializable {

    private Integer status;

    private String name;

    private String category;

    private Long workspaceId;

    private Integer pageNum;

    private Integer pageSize;

    //是否筛选我收藏的
    private Boolean isMyCollect;

    private String username;

    //按使用次数排序
    private boolean useTimes;

    private boolean orderByStar;

    private boolean orderByUtime;

    private Integer appId;

}
