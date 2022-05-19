package com.xiaomi.youpin.docker;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * <p>
 * container 使用的信息
 */
@Data
@Builder
public class UseInfo {

    /**
     * 使用中的cpu数量
     */
    private int useCpuNum;


    /**
     * 使用中的内存数量
     */
    private long useMemNum;

    /**
     * 安装的应用
     */
    private Set<String> apps;


    /**
     * 安装的应用的详细信息
     */
    private List<AppInfo> appInfos;

}
