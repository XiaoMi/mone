package com.xiaomi.youpin.docean.plugin.dubbo.common;

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import org.apache.dubbo.annotation.DubboReference;
import org.apache.dubbo.annotation.DubboService;

/**
 * @author goodjava@qq.com
 * @date 2022/3/25 15:37
 */
public class DubboInfoUtils {

    public static ServiceInfo getService(Service service) {
        ServiceInfo info = new ServiceInfo();
        info.setAsync(service.async());
        info.setInterfaceClass(service.interfaceClass());
        info.setGroup(service.group());
        info.setTimeout(service.timeout());
        info.setVersion(service.version());
        info.setName(service.name());
        return info;
    }

    public static ServiceInfo getService(DubboService service) {
        ServiceInfo info = new ServiceInfo();
        info.setAsync(service.async());
        info.setInterfaceClass(service.interfaceClass());
        info.setGroup(service.group());
        info.setTimeout(service.timeout());
        info.setVersion(service.version());
        info.setName(service.name());
        return info;
    }


    public static ReferenceInfo getReference(Reference reference) {
        ReferenceInfo info =  new ReferenceInfo();
        info.setCheck(reference.check());
        info.setCluster(reference.cluster());
        info.setGroup(reference.group());
        info.setName(reference.name());
        info.setInterfaceClass(reference.interfaceClass());
        info.setVersion(reference.version());
        info.setTimeout(reference.timeout());
        return info;
    }

    public static ReferenceInfo getReference(DubboReference reference) {
        ReferenceInfo info =  new ReferenceInfo();
        info.setCheck(reference.check());
        info.setCluster(reference.cluster());
        info.setGroup(reference.group());
        info.setName(reference.name());
        info.setInterfaceClass(reference.interfaceClass());
        info.setVersion(reference.version());
        info.setTimeout(reference.timeout());
        return info;
    }


}
