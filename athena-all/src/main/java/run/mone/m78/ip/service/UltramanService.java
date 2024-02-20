package run.mone.m78.ip.service;

import com.intellij.openapi.components.ServiceManager;
import run.mone.m78.ip.client.GrpcClient;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/5 12:38
 */
public interface UltramanService {
    static UltramanService getInstance() {
        return ServiceManager.getService(UltramanService.class);
    }

    void run();

    void init();

    GrpcClient client();
}
