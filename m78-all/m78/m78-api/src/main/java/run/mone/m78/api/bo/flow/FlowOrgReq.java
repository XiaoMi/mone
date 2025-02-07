package run.mone.m78.api.bo.flow;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 15:38
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FlowOrgReq {

    @HttpApiDocClassDefine(value = "id", required = false, description = "flowid", defaultValue = "")
    private Long id;

    @HttpApiDocClassDefine(value = "workspaceId", required = false, description = "flow所属workspaceId", defaultValue = "")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "name", required = false, description = "flow名称", defaultValue = "")
    private String name;

    @HttpApiDocClassDefine(value = "categoryId", required = false, description = "flow类型id", defaultValue = "")
    private String categoryId;

    @HttpApiDocClassDefine(value = "userName", required = false, description = "flow用户名", defaultValue = "")
    private String userName;

    @HttpApiDocClassDefine(value = "pageSize", required = false, description = "页大小", defaultValue = "")
    private Integer pageSize;

    @HttpApiDocClassDefine(value = "pageNum", required = false, description = "页码", defaultValue = "")
    private Integer pageNum;

    @HttpApiDocClassDefine(value = "scope", required = false, description = "显示范围", defaultValue = "all/mine/favorite")
    private String scope;

    @HttpApiDocClassDefine(value = "order", required = false, description = "排序", defaultValue = "popular/latest, now only create_time/modify_time")
    private String order;

    @HttpApiDocClassDefine(value = "orgOnly", required = false, description = "是否只展示flow(不返回flow下的组件列表)", defaultValue = "false")
    private boolean orgOnly;

    @HttpApiDocClassDefine(value = "status", required = false, description = "flow发布状态，0(已发布)，1(未发布)", defaultValue = "1")
    private Integer status;

    @HttpApiDocClassDefine(value = "subStatus", required = false, description = "flow下组件启用状态，0(启用)，1(未启用)", defaultValue = "0")
    private Integer subStatus;

    @HttpApiDocClassDefine(value = "official", required = false, description = "是否官方flow，0(非官方)，1(官方)", defaultValue = "0")
    private Integer official;
}
