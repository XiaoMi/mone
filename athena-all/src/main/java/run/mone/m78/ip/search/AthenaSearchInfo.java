package run.mone.m78.ip.search;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 12:08
 */
@Data
@Builder
public class AthenaSearchInfo implements Serializable {

    private String type;

    private String selectValue;

    private String value;

    private String desc;

    private String methodName;


}
