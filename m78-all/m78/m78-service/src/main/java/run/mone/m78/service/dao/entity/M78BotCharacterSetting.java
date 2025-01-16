package run.mone.m78.service.dao.entity;

import com.google.gson.JsonElement;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.mybatisflex.core.handler.GsonTypeHandler;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import run.mone.m78.service.bo.bot.BotSettingBo;

/**
 * 实体类。
 *
 * @author hoho
 * @since 2024-03-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_bot_character_setting")
public class M78BotCharacterSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * bot id
     */
    private Long botId;

    /**
     * bot设定
     */
    private String setting;

    /**
     * 系统设定
     */
    @Column("system_setting")
    private String systemSetting;

    /**
     * 模型
     */
    private String aiModel;

    /**
     * 对话轮次
     */
    private Integer dialogueTurns;

    /**
     * 对话超时时间(ms)
     */
    @Column("dialogue_timeout")
    private Integer dialogueTimeout;

    /**
     * 开场白
     */
    private String openingRemarks;

    /**
     * 开场白问题
     */
    private String openingQues;

    /**
     * 自定义prompt开关
     */
    private Integer customizePromptSwitch;

    /**
     * 自定义prompt
     */
    private String customizePrompt;

    /**
     * 音色开关
     */
    private Integer timbreSwitch;

    /**
     * 音色
     */
    private String timbre;

    private Integer deleted;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String temperature;

    private Boolean streaming;

    @Column(value = "audio_config", typeHandler = GsonTypeHandler.class)
    private  AudioConfig audioConfig;

    public M78BotCharacterSetting(BotSettingBo bo) {
        BeanUtils.copyProperties(bo, this);
        if (bo.getDialogueTurns() == null) {
            this.dialogueTurns = 0;
        }
        this.setUpdateTime(LocalDateTime.now());
    }

}
