package run.mone.antlr.golang;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/1 22:48
 */
@Data
@Builder
public class ParseResult {


    private List<Field> fieldList;

    private Map<String, List<String>> methodMap;

}
