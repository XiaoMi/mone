package run.mone.m78.api.bo.plugins;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/2/24 2:47 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PluginReq implements Serializable {

    private static final long serialVersionUID = 1572696491548329920L;

    @HttpApiDocClassDefine(value = "id", required = false, description = "组件id", defaultValue = "")
    private Long id;

    @HttpApiDocClassDefine(value = "orgId", required = false, description = "插件id", defaultValue = "")
    private Long orgId;

    @HttpApiDocClassDefine(value = "workspaceId", required = false, description = "组件所属空间id", defaultValue = "")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "name", required = false, description = "组件名称", defaultValue = "")
    private String name;

    @HttpApiDocClassDefine(value = "categoryName", required = false, description = "类型名称", defaultValue = "")
    private String categoryName;

    @HttpApiDocClassDefine(value = "categoryId", required = false, description = "类型id", defaultValue = "")
    private Long categoryId;

    @HttpApiDocClassDefine(value = "userName", required = false, description = "用户名", defaultValue = "")
    private String userName;

    @HttpApiDocClassDefine(value = "type", required = false, description = "内置类型名", defaultValue = "")
    private Integer type;

    @HttpApiDocClassDefine(value = "scale", required = false, description = "范围, 全部/我的/收藏 (all/mine/favorite)", defaultValue = "all")
    private String scale;

    @HttpApiDocClassDefine(value = "order", required = false, description = "排序, 最受欢迎/最近发布 (favorite/latested)", defaultValue = "lasted")
    private String order;

    @HttpApiDocClassDefine(value = "pageSize", required = false, description = "页大小", defaultValue = "")
    private Integer pageSize;

    @HttpApiDocClassDefine(value = "pageNum", required = false, description = "页码", defaultValue = "")
    private Integer pageNum;

    @HttpApiDocClassDefine(value = "status", required = false, description = "插件下组件启用状态，0(启用)，1(未启用)", defaultValue = "0")
    private Integer status;

}
