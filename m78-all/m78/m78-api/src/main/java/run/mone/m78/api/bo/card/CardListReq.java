package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardListReq {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int pageSize = 30;

    private Integer status;

    private String type;

    private Integer official;

    private Long workspaceId;

    private String userName;

    private String name;

    private String orderBy;

    //是否逆序
    private boolean isAsc;

    //是否是我收藏
    private boolean isMyCollection;

}
