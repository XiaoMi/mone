package com.xiaomi.mone.monitor.service.prometheus;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gaoxihui
 * @date 2021/7/28 10:00 上午
 */
public enum MetricSuffix {
    _total,
    _count,
    _bucket,
    _created;

    public static MetricSuffix getByName(String name) {
        if(StringUtils.isEmpty(name)){
            return null;
        }
        if(MetricSuffix._total.name().equals(name)){
            return MetricSuffix._total;
        }
        if(MetricSuffix._count.name().equals(name)){
            return MetricSuffix._count;
        }
        if(MetricSuffix._bucket.name().equals(name)){
            return MetricSuffix._bucket;
        }
        if(MetricSuffix._created.name().equals(name)){
            return MetricSuffix._created;
        }

        return null;
    }
}
