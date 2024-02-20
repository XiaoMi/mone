package run.mone.m78.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ZPromptAddonItem implements Serializable {
    String value;
    Map<String,String> meta;
}
