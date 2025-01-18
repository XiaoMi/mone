package run.mone.m78.api.bo.flow;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class FlowQryParam implements Serializable {
    @HttpApiDocClassDefine(value = "workSpaceId", required = false, description = "workSpaceId", defaultValue = "0")
    private Long workSpaceId;
    @HttpApiDocClassDefine(value = "userName", required = false, description = "用户名", defaultValue = "0")
    private String userName;
    @HttpApiDocClassDefine(value = "name", required = false, description = "名称", defaultValue = "0")
    private String name;
    // 0 未发布、1 已发布
    @HttpApiDocClassDefine(value = "publishStatus", required = false, description = " 0 未发布、1 已发布", defaultValue = "0")
    private Integer publishStatus;

    // 排序字段名 ctime,utime
    @HttpApiDocClassDefine(value = "orderFieldName", required = false, description = "排序字段名 ctime,utime", defaultValue = "0")
    private String orderFieldName;
    @HttpApiDocClassDefine(value = "needDetail", required = false, description = "是否需要详情", defaultValue = "0")
    private boolean needDetail;
    //查询范围, all、mine、favorite
    @HttpApiDocClassDefine(value = "scale", required = false, description = "查询范围, all、mine、favorite", defaultValue = "0")
    private String scale;

    @HttpApiDocClassDefine(value = "pageNum", required = false, description = "页码", defaultValue = "0")
    private Integer pageNum = 1;
    @HttpApiDocClassDefine(value = "official", required = false, description = "页长", defaultValue = "0")
    private Integer pageSize = 10;
    @HttpApiDocClassDefine(value = "flowBaseId", required = false, description = "flow id", defaultValue = "0")
    private Integer flowBaseId;
    @HttpApiDocClassDefine(value = "categoryId", required = false, description = "插件类型id", defaultValue = "")
    private String categoryId;
    @HttpApiDocClassDefine(value = "official", required = false, description = "是否官方插件，0(非官方)，1(官方)", defaultValue = "0")
    private Integer official;
}
