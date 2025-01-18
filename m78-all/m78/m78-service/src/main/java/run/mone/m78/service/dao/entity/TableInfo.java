package run.mone.m78.service.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:20 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TableInfo {

    private String tableName;
    private List<String> columnInfoList;
    private Integer type;
    private String source;
    private String pkName;

}
