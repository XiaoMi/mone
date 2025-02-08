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

import static run.mone.m78.api.constant.TableConstant.IM_FRIENDSHIP_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(IM_FRIENDSHIP_TABLE)
public class IMFriendshipPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    @Column("user_id1")
    private Integer userId1;

    @Column("user_id2")
    private Integer userId2;

    /**
     * @see IMFriendshipStatusEnum
     */
    @Column("status")
    private int status;

    @Column("create_time")
    private Timestamp createTime;



}
