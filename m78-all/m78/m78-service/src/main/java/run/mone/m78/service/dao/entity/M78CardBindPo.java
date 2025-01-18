package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.CARD_BIND_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CARD_BIND_TABLE)
public class M78CardBindPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("bot_id")
    private Long botId;

    @Column("card_id")
    private Long cardId;

    @Column("relate_id")
    private Long relateId;

    @Column
    private String type;

    @Column("bind_detail")
    private String bindDetail;

    @Column
    private Long ctime;

    @Column
    private Long utime;


}
