package common;

import lombok.Data;

@Data
public class TpsRecord {
    boolean needRecordTps;

    public TpsRecord(boolean needRecordTps) {
        this.needRecordTps = needRecordTps;
    }
}
