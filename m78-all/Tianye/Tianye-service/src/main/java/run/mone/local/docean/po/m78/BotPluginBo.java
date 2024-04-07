package run.mone.local.docean.po.m78;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-07 14:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotPluginBo implements Serializable {

    private Long orgId;

    private String orgName;

    private List<BotPluginDetailBo> pluginDetailList;

}
