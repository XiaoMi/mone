package run.mone.m78.service.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:59 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QueryContext {

    private ConnectionInfo connectionInfo;

}
