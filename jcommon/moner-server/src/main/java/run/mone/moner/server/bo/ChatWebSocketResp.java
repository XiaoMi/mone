package run.mone.moner.server.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatWebSocketResp {

    private long roleId;

    private String roleName;

    private String roleType;

    private String content;

    // 与前端约定的messageType，用于确定流式数据的开始与结束
    private String messageType;

    private String type;
}
