package run.mone.mimeter.dashboard.service;


import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.common.TestReq;

public interface DubboHealthService {

    String health(TestReq testReq);

    Result<TestReq> getUid();
}