package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.CHAT_MESSAGE_TABLE;

/**
 * @author goodjava@qq.com
 * @date 2024/1/16 14:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CHAT_MESSAGE_TABLE)
public class ChatMessagePo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("topic_id")
    private Integer topicId;

    @Column("message")
    private String message;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    /**
     * 1 未删除 2 已删除
     */
    @Column("state")
    private Integer state;

    @Column("user_name")
    private String userName;

    @Column("message_role")
    private String messageRole;

    @Column(value = "meta", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, String> meta;

}
