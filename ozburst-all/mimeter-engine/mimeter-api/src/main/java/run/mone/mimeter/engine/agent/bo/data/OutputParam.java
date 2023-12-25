package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutputParam implements Serializable {

    private int origin;
    private String paramName;
    private String parseExpr;

    public OutputParam(int origin,String paramName, String parseExpr) {
        this.origin = origin;
        this.paramName = paramName;
        this.parseExpr = parseExpr;
    }
}
