package com.xiaomi.miapi.util;

import com.xiaomi.miapi.vo.BusProjectVo;

import java.sql.Timestamp;
import java.util.Comparator;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public class ProjectCompareByDate implements Comparator<BusProjectVo> {
    @Override
    public int compare(BusProjectVo o1, BusProjectVo o2) {
        Long o1Ctime = o1.getCtime();
        return o1Ctime.compareTo(o2.getCtime());
    }
}
