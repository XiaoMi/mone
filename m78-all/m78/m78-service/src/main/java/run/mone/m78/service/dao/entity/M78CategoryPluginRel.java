package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
@Table(value = "m78_category_plugin_rel")
public class M78CategoryPluginRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 分类id
     */
    private Long catId;

    /**
     * plugin id
     */
    private Long pluginId;

    /**
     * 是否删除0-否 1-是
     */
    private Integer deleted;

    private Date createTime;

    private Date updateTime;

}
