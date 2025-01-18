package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.plugins.BotPluginOrgDTO;

/**
 * 实体类。
 *
 * @author mason
 * @since 2024-03-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_bot_plugin_org")
public class M78BotPluginOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 所属空间id
     */
    @Column("workspace_id")
    private Long workspaceId;

    /**
     * 头像地址
     */
    @Column("avatar_url")
    private String avatarUrl;

    /**
     * 最新的插件发布时间
     */
    private String releaseTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String modifyTime;

    /**
     * 预留, 行状态标记
     * 现用作上线状态
     */
    private Integer status;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 类型标记, 预留
     */
    private Integer type;

    /**
     * 描述(可以很长)
     */
    private String description;

    /**
     * 插件的评价平均分
     */
    private double pluginAvgStar;

    /**
     * 是否官方插件，1:官方，0:非官方
     */
    private Integer official;

    @RelationOneToMany(selfField = "id", targetField = "org_id")
    private List<M78BotPlugin> plugins;

    // 将当前对象转换为BotPluginOrgDTO
    public BotPluginOrgDTO toDTO() {
        BotPluginOrgDTO dto = new BotPluginOrgDTO();
        dto.setId(this.id);
        dto.setWorkspaceId(this.workspaceId);
        dto.setPluginOrgName(this.name);
        dto.setAvatarUrl(this.avatarUrl);
        dto.setReleaseTime(this.releaseTime);
        dto.setCreateTime(this.createTime);
        dto.setModifyTime(this.modifyTime);
        dto.setStatus(this.status);
        dto.setCreator(this.userName);
        dto.setModifier(this.modifier);
        dto.setPluginOrgDesc(this.description);
        dto.setPluginAvgStar(this.pluginAvgStar);
        dto.setOfficial(this.official);
        if (this.plugins != null) {
            dto.setPlugins(this.plugins.stream().map(M78BotPlugin::toDTO).collect(Collectors.toList()));
            dto.setPluginCnt(plugins.size());
        }
        return dto;
    }

    // 以BotPluginOrgDTO为入参，将其转换为M78BotPluginOrg并返回
    public static M78BotPluginOrg fromDTO(BotPluginOrgDTO dto) {
        M78BotPluginOrg org = new M78BotPluginOrg();
        org.setId(dto.getId());
        org.setWorkspaceId(dto.getWorkspaceId());
        org.setName(dto.getPluginOrgName());
        org.setAvatarUrl(dto.getAvatarUrl());
        org.setReleaseTime(dto.getReleaseTime());
        org.setCreateTime(dto.getCreateTime());
        org.setModifyTime(dto.getModifyTime());
        org.setStatus(dto.getStatus());
        org.setUserName(dto.getCreator());
        org.setModifier(dto.getModifier());
        org.setDescription(dto.getPluginOrgDesc());
        org.setOfficial(dto.getOfficial());
        if (dto.getPlugins() != null) {
            org.setPlugins(dto.getPlugins().stream().map(pluginDTO -> {
                M78BotPlugin plugin = new M78BotPlugin();
                plugin = M78BotPlugin.fromDTO(pluginDTO);
                return plugin;
            }).collect(Collectors.toList()));
        }
        return org;
    }
}
