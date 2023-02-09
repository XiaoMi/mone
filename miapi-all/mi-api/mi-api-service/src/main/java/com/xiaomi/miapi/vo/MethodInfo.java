package com.xiaomi.miapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class MethodInfo implements Serializable {
    private String serviceName;
    private String methodName;
    private String group = "";
    private String version = "";
    private int timeout = 10000;
    private int retries = 3;
    private String[] parameterTypes;
    private Object[] args;
    private String addr;
    private String ip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return timeout == that.timeout && retries == that.retries && Objects.equals(serviceName, that.serviceName) && Objects.equals(methodName, that.methodName) && Objects.equals(group, that.group) && Objects.equals(version, that.version) && Arrays.equals(parameterTypes, that.parameterTypes) && Arrays.equals(args, that.args) && Objects.equals(addr, that.addr) && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(serviceName, methodName, group, version, timeout, retries, addr, ip);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", timeout=" + timeout +
                ", retries=" + retries +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                ", addr='" + addr + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
