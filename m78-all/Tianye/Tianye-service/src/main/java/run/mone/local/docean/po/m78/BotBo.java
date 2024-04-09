package run.mone.local.docean.po.m78;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-01 15:44
 */
@Data
public class BotBo implements Serializable {

    @HttpApiDocClassDefine(value = "id", description = "机器人id")
    private Long id;

    @HttpApiDocClassDefine(value = "name", description = "机器人名称")
    private String name;

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "avatarUrl", description = "机器人头像")
    private String avatarUrl;

    @HttpApiDocClassDefine(value = "creator", description = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "remark", description = "备注")
    private String remark;

    @HttpApiDocClassDefine(value = "permissions", description = "权限")
    private Integer permissions;

    @HttpApiDocClassDefine(value = "publishStatus", description = "发布状态")
    private Integer publishStatus;

    @HttpApiDocClassDefine(value = "publishStatusDesc", description = "发布状态描述")
    private String publishStatusDesc;

}
