package com.xiaomi.youpin.gwdash.bo;
import java.util.Objects;
import java.util.Set;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/4/29 17:26
 */
public class ResourceParam {
    private String ip;
    private int cpu;
    private long mem;
    private int remainCpu;
    private long remainMem;
    private Set<Integer> ports;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public long getMem() {
        return mem;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }


    public int getRemainCpu() {
        return remainCpu;
    }

    public void setRemainCpu(int remainCpu) {
        this.remainCpu = remainCpu;
    }

    public long getRemainMem() {
        return remainMem;
    }

    public Set<Integer> getPorts() {
        return ports;
    }

    public void setPorts(Set<Integer> ports) {
        this.ports = ports;
    }

    public void setRemainMem(long remainMem) {
        this.remainMem = remainMem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceParam)) {
            return false;
        }
        ResourceParam that = (ResourceParam) o;
        return getCpu() == that.getCpu() &&
                getMem() == that.getMem() &&
                getRemainCpu() == that.getRemainCpu() &&
                getRemainMem() == that.getRemainMem() &&
                getIp().equals(that.getIp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIp(), getCpu(), getMem(), getRemainCpu(), getRemainMem());
    }
}
