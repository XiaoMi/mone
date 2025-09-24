package run.mone.mcp.hammerspoon.function.tigertrade.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shanwb
 * @date 2025-03-10
 */
@Data
public class OptionDetailBO implements Serializable {

    private String identifier;
    //call | put
    private String optionType;
    //yyyy-MM-dd
    private String expiry;

    private String strike;
    private String right;
    private Double bidPrice;
    private Integer bidSize;
    private Double askPrice;
    private Integer askSize;
    private int volume;
    private Double latestPrice;
    private Double preClose;
    private int openInterest;
    private int multiplier;
    private long lastTimestamp;
    private double impliedVol;
    private double delta;
    private double gamma;
    private double theta;
    private double vega;
    private double rho;

}
