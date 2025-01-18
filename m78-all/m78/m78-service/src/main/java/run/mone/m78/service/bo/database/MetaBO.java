package run.mone.m78.service.bo.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 6:03 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MetaBO {

    private String tableName;

    private List<Column> columns;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Column {
        private String name;
        private String constraint;
        private String type;
    }
}
