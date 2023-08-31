package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/3/24
 */
@Data
@ToString(callSuper = true)
public class AddEsParam implements ArgCheck, Serializable {
    private String esUser;
    private String esPwd;
    private String esAddress;
    private String name;
    private Integer threads;
    private String type;
    private Integer id;
    private Integer bulkActions;          //每一批数量
    private Integer bulkByteSize;         //每一批大小
    private Integer concurrentRequests;   //并发请求数
    private Integer flushInterval;        //刷新间隔
    private Integer retryNum;             //重试次数
    private Integer retryInterval;        //重试间隔

    @Override
    public void encrypted() {
        if (StringUtils.isNotBlank(esPwd)) {
            esPwd = "******";
        }
    }

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(esUser)) {
            return false;
        }
        if (StringUtils.isBlank(esPwd)) {
            return false;
        }
        if (StringUtils.isBlank(esAddress)) {
            return false;
        }
        if (threads == null || threads <= 0) {
            return false;
        }
        if (bulkActions == null || bulkActions < 0) {
            return false;
        }
        if (bulkByteSize == null || bulkByteSize < 0) {
            return false;
        }
        if (concurrentRequests == null || concurrentRequests < 0) {
            return false;
        }
        if (flushInterval == null || flushInterval < 0) {
            return false;
        }
        if (retryNum == null || retryNum < 0) {
            return false;
        }
        if (retryInterval == null || retryInterval < 0) {
            return false;
        }
        return true;
    }
}
