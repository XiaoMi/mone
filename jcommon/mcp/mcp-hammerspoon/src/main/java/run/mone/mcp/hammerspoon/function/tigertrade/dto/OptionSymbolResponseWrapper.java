package run.mone.mcp.hammerspoon.function.tigertrade.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OptionSymbolResponseWrapper implements Serializable {

    private List<OptionSymbolBO> symbolItems;
    private int code;
    private String message;
    private long timestamp;
    private String sign;

}
