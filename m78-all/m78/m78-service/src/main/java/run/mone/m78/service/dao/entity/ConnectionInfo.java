package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:29 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table("m78_connection_info")
public class ConnectionInfo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String host;

    private String port;

    private String database;

    private String user;

    private String pwd;

    @Column("jdbc_url")
    private String jdbcUrl;

    private String cluster;

    private String kerberos;

    private String queue;

    private int type;

    //这个内容是当meta信息用的
    @Column("custom_knowledge")
    private String customKnowledge;

    @Column("user_name")
    private String userName;

    @Column("create_time")
    private String createTime;

    // store only
    @Column("modify_time")
    private String updateTime;
}
