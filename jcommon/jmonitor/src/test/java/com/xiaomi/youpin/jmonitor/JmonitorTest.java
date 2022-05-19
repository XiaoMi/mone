package com.xiaomi.youpin.jmonitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.jvmstat.monitor.MonitorException;

import java.net.UnknownHostException;
import java.util.List;

/**
 * @author gaoyibo
 */
public class JmonitorTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getMonitorInfo() {
        Jmonitor service = new Jmonitor();
        MonitorInfo monitorInfo = service.getMonitorInfo("25166");
        System.out.println("cpu占有率=" + monitorInfo.getCpuRatio());

        System.out.println("可使用内存=" + monitorInfo.getTotalMemory());
        System.out.println("剩余内存=" + monitorInfo.getFreeMemory());
        System.out.println("最大可使用内存=" + monitorInfo.getMaxMemory());

        System.out.println("操作系统=" + monitorInfo.getOsName());
        System.out.println("总的物理内存=" + monitorInfo.getTotalMemorySize() + "kb");
        System.out.println("剩余的物理内存=" + monitorInfo.getFreeMemory() + "kb");
        System.out.println("已使用的物理内存=" + monitorInfo.getUsedMemory() + "kb");
        System.out.println("线程总数=" + monitorInfo.getTotalThread());
    }

    @Test
    public void getNetworkInfo() {
        Jmonitor service = new Jmonitor();
        try {
            NetworkInfo networkInfo = service.getNetworkInfo();
            System.out.println(networkInfo);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getJps() throws MonitorException {
        Jmonitor service = new Jmonitor();

        System.out.println(service.getJpsInfo());
    }

    @Test
    public void getJstat() {
        Jmonitor service = new Jmonitor();

        String[] args = {"-class", "10664"};
        service.getJstat(args);
    }

    @Test
    public void getJstack() {
        Jmonitor service = new Jmonitor();

        String[] args = {"-m", "72849"};
        try {
            service.getJstack(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void threadTotal() {
        Jmonitor service = new Jmonitor();
        Integer a = service.getThreadTotal("25166");
        System.out.println(a);
    }


    @Test
    public void testa() throws MonitorException {
        Jmonitor jmonitor = new Jmonitor();
        List<Jmonitor.JInfo> list = jmonitor.getJpsInfo();
        list.stream().forEach(it -> {
            System.out.println(it.getName());
        });
    }
}