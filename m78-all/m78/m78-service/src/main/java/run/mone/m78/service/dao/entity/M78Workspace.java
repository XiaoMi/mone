package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author hoho
 * @since 2024-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_workspace")
public class M78Workspace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @HttpApiDocClassDefine(value = "id",description = "工作空间id")
    private Long id;

    /**
     * workspace名称
     */
    @HttpApiDocClassDefine(value = "name",description = "工作空间名称")
    private String name;

    private String avatarUrl;

    @HttpApiDocClassDefine(value = "remark",description = "工作空间描述")
    private String remark;

    /**
     * 所有者
     */
    @HttpApiDocClassDefine(value = "owner",description = "所有者")
    private String owner;

    /**
     * 创建人
     */
    @HttpApiDocClassDefine(value = "creator",description = "创建人")
    private String creator;

    /**
     * 是否删除0-否 1-是
     */
    @HttpApiDocClassDefine(value = "deleted",description = "是否删除0-否 1-是")
    private Integer deleted;

    /**
     * 创建时间
     */
    @HttpApiDocClassDefine(value = "createTime",description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @HttpApiDocClassDefine(value = "updater",description = "更新人")
    private String updater;

    @HttpApiDocClassDefine(value = "updateTime",description = "更新时间")
    private LocalDateTime updateTime;

    @HttpApiDocClassDefine(value = "version", description = "版本")
    private Integer version;

}
