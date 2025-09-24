package run.mone.local.docean.po.m78;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-01 15:58
 */
@Data
public class BotSettingBo implements Serializable {

    @HttpApiDocClassDefine(value = "id", description = "id")
    private Long id;

    @HttpApiDocClassDefine(value = "botId", description = "机器人id")
    private Long botId;

    /**
     * 系统设定
     */
    private String systemSetting;

    /**
     * 人物设定
     */
    @HttpApiDocClassDefine(value = "setting", description = "机器人设定")
    private String setting;

    /**
     * 模型
     */
    @HttpApiDocClassDefine(value = "aiModel", description = "模型")
    private String aiModel;

    /**
     * 对话轮次
     */
    @HttpApiDocClassDefine(value = "dialogueTurns", description = "对话轮次")
    private Integer dialogueTurns;

    /**
     * 开场白
     */
    @HttpApiDocClassDefine(value = "openingRemarks", description = "开场白")
    private String openingRemarks;

    /**
     * 开场白问题
     */
    @HttpApiDocClassDefine(value = "openingQues", description = "开场白问题")
    private List<String> openingQues;

    @HttpApiDocClassDefine(value = "customizePromptSwitch", description = "自定义prompt开关 0-关闭 1-开启")
    private Integer customizePromptSwitch;

    /**
     * 自定义prompt
     */
    @HttpApiDocClassDefine(value = "customizePrompt", description = "自定义prompt")
    private String customizePrompt;

    /**
     * 音色开关
     */
    @HttpApiDocClassDefine(value = "timbreSwitch", description = "音色开关 0-关闭 1-开启")
    private Integer timbreSwitch;

    /**
     * 音色
     */
    @HttpApiDocClassDefine(value = "timbre", description = "音色")
    private String timbre;

    /**
     * 温度
     */
    @HttpApiDocClassDefine(value = "temperature", description = "温度")
    private String temperature;
}
