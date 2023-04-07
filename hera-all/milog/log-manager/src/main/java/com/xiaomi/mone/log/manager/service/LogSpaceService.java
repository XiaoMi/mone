package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.dto.SpacePermTreeDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;

import java.util.List;

public interface LogSpaceService {

    /**
     * 新建
     *
     * @param cmd
     * @return
     */
    Result<String> newMilogSpace(MilogSpaceParam cmd);

    /**
     * getById
     *
     * @param id
     * @return
     */
    Result<MilogSpaceDTO> getMilogSpaceById(Long id);

    /**
     * 分页查询
     *
     * @param spaceName
     * @param page
     * @param pagesize
     * @return
     */
    Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(String spaceName, Integer page, Integer pagesize);


    Result<List<MapDTO<String, Long>>> getMilogSpaces();

    /**
     * 更新
     *
     * @param param
     * @return
     */
    Result<String> updateMilogSpace(MilogSpaceParam cmd);

    Result<String> deleteMilogSpace(Long id);

    /**
     * 刷新sapce的部门ID字段
     */
    void refreshSpaceDeptId();

    Result<SpacePermTreeDTO> getSpacecPermission(Long spaceId);

    Result<String> setSpacePermission(Long spaceId, String permDeptIds);

    MilogSpaceDO buildMiLogSpace(MilogSpaceParam cmd, String appCreator);
}
