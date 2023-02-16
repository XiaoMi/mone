package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/2/18 11:36 上午
 * 接入hera的应用对应的分区
 */
@Data
public class Region {
    String name;
    List<Env> envs;
}
