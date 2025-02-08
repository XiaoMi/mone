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
import run.mone.m78.api.bo.knowledge.KnowledgeConfig;

import static run.mone.m78.api.constant.TableConstant.CHAT_TOPICS_TABLE;

/**
 * @author goodjava@qq.com
 * @date 2024/1/16 14:24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CHAT_TOPICS_TABLE)
public class ChatTopicPo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    /**
     * @see ChatTopicTypeEnum
     */
    @Column("type")
    private Integer type;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column("state")
    private Integer state;

    @Column("user_name")
    private String userName;

    @Column(value = "knowledge_config", typeHandler = Fastjson2TypeHandler.class)
    private KnowledgeConfig knowledgeConfig;

}
