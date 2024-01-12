package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CommonReqInfo implements Serializable {

    /**
     * 1:http get
     * 2:http post form
     * 3:http post json
     * 4:dubbo
     */
    private int paramsType;

    private String debugUrl;

    private String paramJson;

//    private List<Object> getOrFormParamsList;

    private Map<String,String> queryParamMap;

    private Map<String,String> headers;

    @Override
    public String toString() {
        return "CommonReqInfo{" +
                "paramsType=" + paramsType +
                ", debugUrl='" + debugUrl + '\'' +
                ", paramJson='" + paramJson + '\'' +
                ", queryParamMap=" + queryParamMap +
                ", headers=" + headers +
                '}';
    }
}
