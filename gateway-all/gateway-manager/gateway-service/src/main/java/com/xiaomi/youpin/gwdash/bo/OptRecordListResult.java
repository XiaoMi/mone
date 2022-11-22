package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.youpin.gwdash.dao.model.OptRecord;
import lombok.Data;

import java.util.List;

@Data
public class OptRecordListResult {
    private int total;

    private List<OptRecord> infoList;
}
