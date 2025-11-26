package run.mone.agentx.dto;

import lombok.Data;

@Data
public class FavoriteRequest {
    private Integer userId;
    private Integer targetId;
    private Integer type;
} 