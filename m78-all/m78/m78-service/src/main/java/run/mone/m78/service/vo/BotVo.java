package run.mone.m78.service.vo;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.im.M78IMRelationDTO;
import run.mone.m78.api.bo.im.PublishRecordDTO;
import run.mone.m78.api.bo.knowledge.KnowledgeBo;
import run.mone.m78.api.bo.table.DbTableBo;
import run.mone.m78.service.bo.BotFlowBo;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.bo.bot.BotSettingBo;
import run.mone.m78.service.bo.plugin.BotPluginBo;

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

    @HttpApiDocClassDefine(value = "botCategory", description = "机器人分类")
    private List<String> botCategory;

    @HttpApiDocClassDefine(value = "botSetting", description = "机器人设置信息")
    private BotSettingBo botSetting;

    @HttpApiDocClassDefine(value = "botPluginList", description = "机器人插件列表")
    private List<BotPluginBo> botPluginList;

    @HttpApiDocClassDefine(value = "botFlowBoList", description = "机器人工作流列表")
    private List<BotFlowBo> botFlowBoList;

    @HttpApiDocClassDefine(value = "knowledgeBoList", description = "知识库列表")
    private List<KnowledgeBo> knowledgeBoList;

    @HttpApiDocClassDefine(value = "tableList", description = "数据库表列表")
    private List<DbTableBo> tableList;

    @HttpApiDocClassDefine(value = "botAvgStar", description = "机器人平均分")
    private Double botAvgStar;

    /**
     * 发布详情
     */
    private List<PublishRecordDTO> publishRecordDTOS;

    private List<M78IMRelationDTO> imRelationDTOS;

    @Builder.Default
    private Map<String, String> meta = new HashMap<>();

    public String getMetaValue(String key, String defaultValue) {
        if (null == this.meta || this.meta.size() == 0) {
            return defaultValue;
        }
        return meta.getOrDefault(key, defaultValue);
    }
}
