package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/2/18 11:31 上午
 * 应用对应的区域
 */
@Data
public class Area {
    String code;
    String name;
    String cname;
    List<Region> regions;
}
