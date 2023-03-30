package run.mone.processor.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/3/29 16:54
 */
@Data
@Builder
public class MethodCode implements Serializable {

    private String name;

    private String code;

    private List<Pair<Class,String>> paramList;

    private Class returnType;

}
