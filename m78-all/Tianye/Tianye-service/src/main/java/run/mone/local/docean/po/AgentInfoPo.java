package run.mone.local.docean.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import static run.mone.local.docean.tianye.common.TableConstants.TABLE_AGENT_CONFIG_INFO;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/27/24 16:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(TABLE_AGENT_CONFIG_INFO)
public class AgentInfoPo {

    @Id
    private Long id;

    @Column
    private String token;

    @Column("avatar_path")
    private String avatarPath;

    @Column
    private byte[] avatar;
}
