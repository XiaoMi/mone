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

import static run.mone.m78.api.constant.TableConstant.LONG_TERM_CHAT_SUMMARY_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(LONG_TERM_CHAT_SUMMARY_TABLE)
public class LongTermChatSummaryPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * m78的appId默认为0
     */
    @Column("app_id")
    private Integer appId;

    @Column("bot_id")
    private Long botId;

    @Column
    private String username;

    @Column
    private String summary;

    /**
     * 内容为list
     */
    @Column
    private String content;

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

}
