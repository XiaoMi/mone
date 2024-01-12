package run.mone.mimeter.dashboard.bo.common;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageBase implements Serializable {
    /**
     * 当前的页码
     */
    @HttpApiDocClassDefine(value = "page", required = true, description = "当前的页码", defaultValue = "1")
    private int page;

    @HttpApiDocClassDefine(value = "pageSize", required = true, description = "每页条数", defaultValue = "10")
    private int pageSize;

    /**
     * 总记录数
     */
    @HttpApiDocClassDefine(value = "total", required = true, description = "总记录数", defaultValue = "100")
    private long total = 0;
}
