package run.mone.z.desensitization.api.service;


import com.xiaomi.youpin.infra.rpc.Result;

public interface DubboHealthService {

    Result<String> health();

}