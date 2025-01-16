package run.mone.m78.api.bo.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ConnectionInfoDTO {

    private Integer id;

    private String host;

    private String port;

    private String database;

    private String user;

    private String pwd;

    private String jdbcUrl;

    private String cluster;

    private String kerberos;

    private String queue;

    private String userName;

    private int type;

    private String customKnowledge;

    private String createTime;

    private String updateTime;

}
