package run.mone.mimeter.dashboard.common;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;

@Data
public class DubboParamItem {
    private Class itemClass;
    private Type itemType;

    private String itemName;
    private String itemClassStr;
    private String defaultValue;
    private String exampleValue;
    private List<DubboParamItem> itemValue;
}
