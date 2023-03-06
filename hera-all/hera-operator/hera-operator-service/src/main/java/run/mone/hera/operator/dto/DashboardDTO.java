package run.mone.hera.operator.dto;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-02-22
 */
@Data
public class DashboardDTO {
    private String prometheusDatasource;
    private String dashboardFolderName;
    private String username;
    private String password;
}
