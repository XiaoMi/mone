package run.mone.antlr.golang;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/1 22:47
 */
@Data
@Builder
public class Field implements Serializable {

    public String k;

    public String v;

    public String type;

}
