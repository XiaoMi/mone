package run.mone.m78.service.dto;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.bo.bot.BotExtensionBo;
import run.mone.m78.service.bo.bot.BotSettingBo;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-04 17:01
 */
@Data
public class BotDto implements Serializable {

    @HttpApiDocClassDefine(value = "botInfo", description = "机器人信息")
    private BotBo botInfo;

    @HttpApiDocClassDefine(value = "botSetting", description = "机器人设置信息")
    private BotSettingBo botSetting;

    @HttpApiDocClassDefine(value = "botExtension", description = "机器人扩展信息")
    private BotExtensionBo botExtensionBo;


}
