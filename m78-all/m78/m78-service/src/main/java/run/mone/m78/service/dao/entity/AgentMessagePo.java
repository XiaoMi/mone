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

import static run.mone.m78.api.constant.TableConstant.AGENT_MESSAGE_TABLE;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/23/24 15:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(AGENT_MESSAGE_TABLE)
public class AgentMessagePo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("source_agent")
    private Long sourceAgent;

    @Column("target_agent")
    private Long targetAgent;

    private String content;

    private Integer status;

    private Long ctime;

    private Long utime;

    @Column(value = "meta", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> meta;
}
