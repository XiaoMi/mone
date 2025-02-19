package run.mone.local.docean.po.m78;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-01 16:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotVo implements Serializable {

    @HttpApiDocClassDefine(value = "botId", description = "机器人id")
    private Long botId;

    @HttpApiDocClassDefine(value = "botName", description = "机器人名称")
    private String botName;

    @HttpApiDocClassDefine(value = "botInfo", description = "机器人信息")
    private BotBo botInfo;

    @HttpApiDocClassDefine(value = "botSetting", description = "机器人设置信息")
    private BotSettingBo botSetting;

    @HttpApiDocClassDefine(value = "botPluginList", description = "机器人插件列表")
    private List<BotPluginBo> botPluginList;

    @HttpApiDocClassDefine(value = "botFlowBoList", description = "机器人工作流列表")
    private List<BotFlowBo> botFlowBoList;

    @HttpApiDocClassDefine(value = "knowledgeBoList", description = "知识库列表")
    private List<KnowledgeBo> knowledgeBoList;

    @Builder.Default
    private String dbInfo = "";

    @Builder.Default
    private String userName = "";

    @Builder.Default
    private Map<String,String> meta = new HashMap<>();

}
