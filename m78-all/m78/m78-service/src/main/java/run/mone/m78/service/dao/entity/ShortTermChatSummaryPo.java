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

import static run.mone.m78.api.constant.TableConstant.SHORT_TERM_CHAT_SUMMARY_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(SHORT_TERM_CHAT_SUMMARY_TABLE)
public class ShortTermChatSummaryPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    @Column("bot_id")
    private Long botId;

    @Column
    private String username;

    @Column
    private String summary;

    @Column
    private Integer priority;

    @Column
    private Boolean positive;

    /**
     * 是否删除 0-否 1-是
     */
    private int deleted;

    @Column("ctime")
    private Long ctime;

    @Column("expire_time")
    private Long expireTime;

}
