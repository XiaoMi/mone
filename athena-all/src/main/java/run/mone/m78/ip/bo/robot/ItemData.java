package run.mone.m78.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/1 13:59
 */
@Data
@Builder
public class ItemData implements Serializable, MessageData {

    private int index;

    private String title;

    private String value;

    private Map<String, String> metaMap;

}
