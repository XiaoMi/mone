package run.mone.m78.service.bo.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-07 15:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotPluginDetailBo implements Serializable {

    private Long pluginId;

    private Long orgId;

    private String name;

    private String apiUrl;

    private String meta;

    private String creator;

    private Integer type;

    private String desc;

    private String avatarUrl;

}
