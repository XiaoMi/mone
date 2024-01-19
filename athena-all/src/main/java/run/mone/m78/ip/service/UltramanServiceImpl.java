package run.mone.m78.ip.service;

import run.mone.m78.ip.client.GrpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/5 12:38
 */
@Slf4j
public class UltramanServiceImpl implements UltramanService {

    private GrpcClient grpcClient;

    private boolean openHttpServer = false;


    public UltramanServiceImpl() {
        grpcClient = new GrpcClient();
    }


    @Override
    public void run() {
        log.info("athena run");
    }

    @Override
    public void init() {
        log.info("athena service init");

    }

    private static void openHttpServer() throws Exception {

    }

    @Override
    public GrpcClient client() {
        return grpcClient;
    }
}
