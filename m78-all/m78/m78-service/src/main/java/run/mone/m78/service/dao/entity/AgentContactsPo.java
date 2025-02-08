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

import static run.mone.m78.api.constant.TableConstant.AGENT_CONTACTS_TABLE;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/23/24 15:39
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(AGENT_CONTACTS_TABLE)
public class AgentContactsPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String label; // 预留

    @Column("agent_id")
    private Long agentId;

    @Column("contact_agent_id")
    private Long contactAgentId;

    private Integer status;

    private Long ctime;

    private Long utime;

    @Column(value = "meta", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> meta;
}
