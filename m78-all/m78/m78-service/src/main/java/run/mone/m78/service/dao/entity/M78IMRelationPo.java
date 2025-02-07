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
@Table(value = "m78_im_relation")
public class M78IMRelationPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private BigInteger id;

    @Column("bot_id")
    private BigInteger botId;

    @Column("bot_name")
    private String botName;

    @Column("im_type_id")
    private Integer imTypeId;

    @Column("relation_flag")
    private String relationFlag;

    @Column("creator")
    private String creator;

    @Column("deleted")
    private Integer deleted;

    @Column("create_time")
    private LocalDateTime createTime;

    @Column("secret")
    private String secret;
}
