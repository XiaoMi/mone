package run.mone.local.docean.dto;

import java.io.Serializable;
import java.util.List;

public class ExecCommandResult implements Serializable {


    private Integer resultCode;
    private List<String> resultData;
    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultData(List<String> resultData) {
        this.resultData = resultData;
    }

    public List<String> getResultData() {
        return resultData;
    }

}
