package com.xiaomi.youpin.docker;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
@Builder
public class DockerLimit {

    /**
     * --cpuset-cpus="1,3"
     * --cpuset-cpus="0-2"
     */
    private String cpu;
    private Long mem;

    /**
     * 10-1000  默认是500
     */
    private Integer blkioWeight;


    /**
     * 不绑定cpu
     */
    private boolean useCpus;


    /**
     * cpu 的数量,可以是0.1
     */
    private float cpuNum;

}
