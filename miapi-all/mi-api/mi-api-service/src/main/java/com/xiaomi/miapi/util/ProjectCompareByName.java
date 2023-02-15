package com.xiaomi.miapi.util;

import com.xiaomi.miapi.vo.BusProjectVo;

import java.util.Comparator;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public class ProjectCompareByName implements Comparator<BusProjectVo> {
    @Override
    public int compare(BusProjectVo p1, BusProjectVo p2) {
        return p1.getName().compareTo(p2.getName());
    }
}
