package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.manager.model.dto.RegionDTO;

import java.util.List;

/**
 * @author zhangping17
 * @date 2021-10-15
 */
public interface NeoAppInfoService {

    /**
     * 根据treeId集合构建region等信息
     * @param treeIds
     * @return
     */
    List<RegionDTO> getNeoAppInfo(List<String> treeIds);
}
