package run.mone.local.docean.fsm.bo;

import com.google.gson.JsonArray;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/5 13:14
 */
@Data
@Builder
public class PluginInfo implements Serializable {

    private String url;

    private String method;

    private Map<String, String> headers;

    private String meta;

    private String desc;

    private Long pluginId;

    private Long orgId;

    private String name;

    private Integer type;

    private JsonArray input;

    private JsonArray output;

    private String display;


}
