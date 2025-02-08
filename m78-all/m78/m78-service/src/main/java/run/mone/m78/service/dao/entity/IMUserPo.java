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
import java.sql.Timestamp;

import static run.mone.m78.api.constant.TableConstant.IM_USER_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(IM_USER_TABLE)
public class IMUserPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    @Column("username")
    private String userName;

    @Column("password")
    private String password;

    @Column("is_online")
    private boolean isOnline;

    @Column("last_seen")
    private Timestamp lastSeen;

    @Column("create_time")
    private Timestamp createTime;

}
