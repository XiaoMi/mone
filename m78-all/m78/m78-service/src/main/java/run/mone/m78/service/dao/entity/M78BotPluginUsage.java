package run.mone.m78.service.dao.entity;

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
@Table(value = "m78_bot_plugin_usage")
public class M78BotPluginUsage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 插件id
     */
    private BigInteger pluginId;

    /**
     * bot id
     */
    private BigInteger botId;

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
     * 类型标记, 预留
     */
    private Integer type;

}
