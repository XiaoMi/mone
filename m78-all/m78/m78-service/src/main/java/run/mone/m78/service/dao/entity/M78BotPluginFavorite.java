package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(value = "m78_bot_plugin_favorite")
public class M78BotPluginFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private BigInteger id;

    /**
     * 插件id
     */
    private BigInteger pluginId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private Timestamp modifyTime;

    /**
     * 预留, 行状态标记
     */
    private Integer status;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 类型标记, 预留
     */
    private Integer type;

}
