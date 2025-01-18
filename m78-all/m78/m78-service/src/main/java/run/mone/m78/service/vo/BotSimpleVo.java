package run.mone.m78.service.vo;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.im.PublishRecordDTO;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.bo.bot.BotSettingBo;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description: 简略版机器人信息
 * @date 2024-03-27 11:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotSimpleVo implements Serializable {

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

    @HttpApiDocClassDefine(value = "botAvgStar", description = "机器人平均分")
    private Double botAvgStar;

    private List<PublishRecordDTO> publishRecordDTOS;

}
