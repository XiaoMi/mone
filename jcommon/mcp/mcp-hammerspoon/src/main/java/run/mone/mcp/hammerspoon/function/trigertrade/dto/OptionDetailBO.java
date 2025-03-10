package run.mone.mcp.hammerspoon.function.trigertrade.dto;

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
    private double bidPrice;
    private int bidSize;
    private double askPrice;
    private int askSize;
    private int volume;
    private double latestPrice;
    private double preClose;
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
