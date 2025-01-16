package run.mone.m78.service.dao.entity;

import com.google.gson.JsonObject;
import com.mybatisflex.annotation.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.api.bo.plugins.BotPluginDTO;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.MappingUtils;

/**
 *  实体类。
 *
 * @author mason
 * @since 2024-03-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_bot_plugin")
@Slf4j
public class M78BotPlugin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 插件所属组织id
     */
    @Column("org_id")
    private Long orgId;

    /**
     * 所属空间id
     */
    @Column("workspace_id")
    private Long workspaceId;

    /**
     * 名称
     */
    private String name;

    /**
     * 插件api_url
     */
    @Column("api_url")
    private String apiUrl;

    /**
     * 插件头像
     */
    @Column("avatar_url")
    private String avatarUrl;

    /**
     * 导出的featureRouterId
     */
    @Column("feature_router_id")
    private BigInteger featureRouterId;

    private String meta;

    /**
     * 发布时间
     */
    @Column("release_time")
    private String releaseTime;

    /**
     * 创建时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 更新时间
     */
    @Column("modify_time")
    private String modifyTime;

    /**
     * 预留, 行状态标记
     * 用作启用状态
     */
    private Integer status;

    /**
     * 调试状态
     */
    @Column("debug_status")
    private Integer debugStatus;

    /**
     * 用户名
     */
    @Column("user_name")
    private String userName;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 类型标记, 0：featureRouter类型，1: 自定义类型
     */
    private Integer type;

    /**
     * 分类
     */
    private String category;

    /**
     * 插件所属分类id
     */
    @Column("category_id")
    private BigInteger categoryId;

    /**
     * 描述(可以很长)
     */
    private String description;

    @RelationOneToOne(selfField = "category_id", targetField = "id")
    private M78Category m78Category;

    /**
     * 插件使用次数
     */
    private Long pluginUseTimes;

    public BotPluginDTO toDTO() {
        // HINT: may be more to setup
        BotPluginDTO res = new BotPluginDTO();
        MappingUtils.copyProperties(this, res);
        res.setType(PluginTypeEnum.getTypeEnumByCode(this.type).getDesc());
        if (StringUtils.isBlank(this.description)) {
            res.setDesc(extractDescFromMeta(this.getMeta()));
        } else {
            res.setDesc(this.description);
        }
        if (StringUtils.isNotBlank(this.getMeta())) {
            try {
                BotPluginDTO.BotPluginMeta botPluginMeta = GsonUtils.gson.fromJson(this.getMeta(), BotPluginDTO.BotPluginMeta.class);
                // HINT: remove empty input and output maps
                List<BotPluginDTO.BotPluginMetaParam> input = botPluginMeta.getInput();
                if (CollectionUtils.isNotEmpty(input)) {
                    input.removeIf(i -> i == null || StringUtils.isBlank(i.getName()));
                }
                List<BotPluginDTO.BotPluginMetaParam> output = botPluginMeta.getOutput();
                if (CollectionUtils.isNotEmpty(output)) {
                    output.removeIf(i -> i == null || StringUtils.isBlank(i.getName()));
                }
                res.setBotPluginMeta(botPluginMeta);
                res.setDisplay(botPluginMeta.getDisplay());
            } catch (Exception e) {
                log.warn("Error while try to parse meta, meta will only be available in DTO str form, nested exception is:", e);
            }
        }
        return res;
    }

    private String extractDescFromMeta(String meta) {
        try {
            if (StringUtils.isBlank(meta)) {
                return "";
            }
            JsonObject jsonObject = GsonUtils.gson.fromJson(meta, JsonObject.class);
            if (jsonObject == null) {
                return "";
            }
            if (jsonObject.get("desc") != null) {
                return jsonObject.get("desc").getAsString();
            }
            return "";
        } catch (Exception e) {
            log.warn("Error while try to parse meta during desc retrieval, nested exception is:", e);
            return "";
        }
    }

    public static M78BotPlugin fromDTO(BotPluginDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO input can not be NULL!");
        }
        M78BotPlugin res = new M78BotPlugin();
        MappingUtils.copyProperties(dto, res);
        res.setType(PluginTypeEnum.getTypeEnumByName(dto.getType()).getCode());
        if (dto.getBotPluginMeta() != null) {
            dto.getBotPluginMeta().setDesc(dto.getDesc());
            dto.getBotPluginMeta().setDisplay(dto.getDisplay());
            res.setMeta(GsonUtils.gson.toJson(dto.getBotPluginMeta()));
        }
        if (StringUtils.isNotBlank(dto.getDesc())) {
            res.setDescription(dto.getDesc());
        }
        return res;
    }
}
