package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 *  实体类。
 *
 * @author zhangping17
 * @since 2024-03-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_im_record")
public class M78IMRecordPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private BigInteger id;

    @Column("bot_id")
    private BigInteger botId;

    @Column("chat_id")
    private String chatId;

    @Column("im_type_id")
    private Integer imTypeId;

    @Column("user_name")
    private String userName;

    @Column("status")
    private Integer status;

    @Column("create_time")
    private LocalDateTime createTime;
}
