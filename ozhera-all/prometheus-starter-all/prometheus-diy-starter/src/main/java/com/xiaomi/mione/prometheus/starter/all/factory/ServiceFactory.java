package com.xiaomi.mione.prometheus.starter.all.factory;

import com.xiaomi.mione.prometheus.starter.all.service.MatrixPrometheusService;
import com.xiaomi.mione.prometheus.starter.all.service.MilinePrometheusService;
import com.xiaomi.mione.prometheus.starter.all.service.PrometheusService;
import org.apache.commons.lang3.StringUtils;

import com.xiaomi.mione.prometheus.starter.all.domain.Const ;


/**
 * @Description
 * @Author dingtao
 * @Date 2023/3/5 3:33 PM
 */
public class ServiceFactory {

    public static PrometheusService getPrometheusService(String platform){
        if(StringUtils.isEmpty(platform)){
            throw new IllegalArgumentException("platform is empty");
        }
        switch (platform) {
            case Const.MILINE:
                return new MilinePrometheusService();
            case Const.MATRIX:
                return new MatrixPrometheusService();
            default:
                throw new IllegalArgumentException("platform is invalid");
        }
    }
}
