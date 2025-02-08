package run.mone.m78.service.dao.entity;

import com.google.common.collect.ImmutableMap;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.mybatisflex.core.handler.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import run.mone.m78.service.bo.bot.BotBo;

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
@Table(value = "m78_bot")
public class M78Bot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * workspaceId
     */
    private Long workspaceId;

    /**
     * 备注
     */
    private String remark;

    private String creator;

    private String updator;

    private String avatarUrl;

    /**
     * 开放权限0-私有 1-公开
     */
    private Integer permissions;

    /**
     * 0未发布，1发布
     */
    private Integer publishStatus;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer deleted;

    /**
     * 热度，近似值，不准确
     */
    private Long botUseTimes;

    @Column("bot_avg_star")
    private Double botAvgStar;

    @Column(value = "meta", typeHandler = GsonTypeHandler.class)
    private Map<String, String> meta;

    //app的id
    private Integer appId;

    public M78Bot(BotBo bo) {
        BeanUtils.copyProperties(bo, this);
        this.setUpdateTime(LocalDateTime.now());
    }

}
