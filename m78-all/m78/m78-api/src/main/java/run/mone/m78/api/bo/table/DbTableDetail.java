package run.mone.m78.api.bo.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.datasource.ConnectionInfoDTO;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/25/24 16:36
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTableDetail {

    private ConnectionInfoDTO connectionInfo;

    private String tableName;

}
