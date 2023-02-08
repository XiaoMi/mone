package run.mone.geth.bo;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class TradeResult {
    private Boolean res;
    private String trade_id;
    private String sender;
    private String receiver;
    private BigInteger amount;
    private String msg;
    private Date time;
}
