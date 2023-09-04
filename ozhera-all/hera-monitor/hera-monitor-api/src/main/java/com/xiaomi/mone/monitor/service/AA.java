package com.xiaomi.mone.monitor.service;

/**
 * @author gaoxihui
 * @date 2021/7/6 1:49 下午
 */
public interface AA {
    void testA();

    public String testError() throws Exception;

    public String testSlowQuery()  throws InterruptedException;

    public void appPlatMove(Integer OProjectId,Integer OPlat,Integer NProjectId,Integer Nplat,Integer newIamId,String NprojectName);

}
