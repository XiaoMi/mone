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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Pair<Class, String>> getParamList() {
        return paramList;
    }

    public void setParamList(List<Pair<Class, String>> paramList) {
        this.paramList = paramList;
    }

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }
}
