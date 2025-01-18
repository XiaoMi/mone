package run.mone.m78.api.bo.datasource;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/7 16:18
 */
@Data
@Builder
public class TableQueryDTO implements Serializable {

    private int connectionId;

    private String tableName;


    private Integer lowerBound;

    private Integer upperBound;
}
