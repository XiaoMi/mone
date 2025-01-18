package run.mone.m78.service.bo.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 5:19 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TableBO {

    private String tableName;

    private String status;

    private String uuid;

    private List<Map<String, Object>> data;

    private boolean hasData;

    private String originalFileName;

    private int favorite;

    private String metaContent;

    // TODO: other metas that maybe needed in the future
}
