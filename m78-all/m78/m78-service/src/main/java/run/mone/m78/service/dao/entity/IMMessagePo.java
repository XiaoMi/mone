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

import static run.mone.m78.api.constant.TableConstant.IM_MESSAGE_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(IM_MESSAGE_TABLE)
public class IMMessagePo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    @Column("sender_id")
    private Integer senderId;

    @Column("receiver_id")
    private Integer receiverId;

    @Column
    private String message;

    @Column("sent_time")
    private Timestamp sentTime;

    @Column
    private Integer status;

}
