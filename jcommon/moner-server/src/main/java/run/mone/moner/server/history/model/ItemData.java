package run.mone.moner.server.history.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemData implements Serializable, MessageData {

    private int index;

    private String title;

    private String value;

    private Map<String, String> metaMap;

}
