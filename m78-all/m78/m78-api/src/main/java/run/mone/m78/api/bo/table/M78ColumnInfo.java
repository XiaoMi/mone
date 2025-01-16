package run.mone.m78.api.bo.table;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/15/24 12:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78ColumnInfo {

    @HttpApiDocClassDefine(value = "name", description = "列名")
    private String name;

    @HttpApiDocClassDefine(value = "desc", description = "列描述")
    private String desc;

    @HttpApiDocClassDefine(value = "type", description = "列类型")
    private String type;

    @HttpApiDocClassDefine(value = "necessary", description = "是否必需")
    private Boolean necessary;

    private Boolean primaryKey;
}
