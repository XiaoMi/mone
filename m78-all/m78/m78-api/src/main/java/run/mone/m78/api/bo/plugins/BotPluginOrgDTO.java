package run.mone.m78.api.bo.plugins;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/7/24 18:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BotPluginOrgDTO {

    @HttpApiDocClassDefine(value = "id", required = true, description = "插件id", defaultValue = "插件id")
    private Long id;

    @HttpApiDocClassDefine(value = "workspaceId", required = true, description = "插件所属空间id", defaultValue = "插件所属空间id")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "pluginOrgName", required = true, description = "插件名称", defaultValue = "插件名称")
    private String pluginOrgName;

    @HttpApiDocClassDefine(value = "pluginOrgName", required = true, description = "插件名称", defaultValue = "插件名称")
    private String pluginOrgDesc;

    private String avatarUrl;

    private Integer pluginCnt;

    private Integer botRefCnt;

    private double pluginAvgStar;

    @HttpApiDocClassDefine(value = "createTime", required = true, description = "插件创建时间", defaultValue = "插件创建时间")
    private String createTime;

    @HttpApiDocClassDefine(value = "status", required = true, description = "插件上架状态", defaultValue = "0：上架， 1：未上架")
    private Integer status;

    private String modifyTime;

    private String releaseTime;

    @HttpApiDocClassDefine(value = "creator", required = true, description = "插件创建人", defaultValue = "插件名称")
    private String creator;

    @HttpApiDocClassDefine(value = "modifier", required = true, description = "插件更新人", defaultValue = "插件名称")
    private String modifier;

    private List<BotPluginDTO> plugins;

    private List<String> pluginCategory;

    private Integer official;
}
