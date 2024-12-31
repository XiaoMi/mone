package run.mone.local.docean.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.IMRecordProvider;

/**
 * @author zhangping17@xiaomi.com
 * @date 3/1/24 16:40
 */
@Slf4j
@Service
public class IMRecordService {

    @Reference(interfaceClass = IMRecordProvider.class, group = "${dubbo.group}", version = "${dubbo.version}", timeout = 30000, check = false)
    private IMRecordProvider imRecordProvider;

    public IMRecordProvider getIMRecordProvider() {
        return imRecordProvider;
    }
}
