package run.mone.docean.plugin.sidecar.manager;

import run.mone.docean.plugin.sidecar.bo.SideCarApp;

/**
 * @author goodjava@qq.com
 * @date 2022/11/8 10:48
 */
public interface RegService {

    Object reg(Object param);

    default void putFassInfo(SideCarApp app) {

    }

}
