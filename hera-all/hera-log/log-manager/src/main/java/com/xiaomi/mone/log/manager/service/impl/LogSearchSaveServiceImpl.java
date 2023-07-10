/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.enums.FavouriteSearchEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.mapper.MilogLogSearchSaveMapper;
import com.xiaomi.mone.log.manager.model.convert.SearchSaveConvert;
import com.xiaomi.mone.log.manager.model.dto.SearchSaveDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogSearchSaveDO;
import com.xiaomi.mone.log.manager.model.vo.SearchSaveInsertCmd;
import com.xiaomi.mone.log.manager.model.vo.SearchSaveUpdateCmd;
import com.xiaomi.mone.log.manager.service.LogSearchSaveService;
import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
@Service
public class LogSearchSaveServiceImpl implements LogSearchSaveService {

    @Resource
    private MilogLogSearchSaveMapper logSearchSaveMapper;

    public Result<PageInfo<SearchSaveDTO>> list(Long storeId, Integer pageNum, Integer pageSize) {
        Long count = logSearchSaveMapper.countByStoreId(storeId);
        List<MilogLogSearchSaveDO> list = logSearchSaveMapper.selectByStoreId(storeId, pageSize * (pageNum - 1), pageSize);
        List<SearchSaveDTO> searchSaveDTOList = SearchSaveConvert.INSTANCE.fromDOList(list);
        PageInfo<SearchSaveDTO> res = new PageInfo<>(pageNum, pageSize, count.intValue(), searchSaveDTOList);
        return Result.success(res);
    }

    public SearchSaveDTO getById(Long id) {
        return SearchSaveConvert.INSTANCE.fromDO(logSearchSaveMapper.selectById(id));
    }

    public Result<Integer> save(SearchSaveInsertCmd cmd) {
        if (cmd.getSort() == null) {
            return Result.failParam("分类字段sort不能为空");
        }
        switch (FavouriteSearchEnum.queryByCode(cmd.getSort())) {
            case TEXT:
                if (isRepeatName(cmd.getName())) {
                    return Result.failParam("名称不能重复");
                }
                break;
            case STORE:
                Integer isMyFavouriteStore = logSearchSaveMapper.isMyFavouriteStore(MoneUserContext.getCurrentUser().getUser(), cmd.getStoreId());
                if (isMyFavouriteStore >= 1) {
                    return Result.failParam("已收藏");
                }
            case TAIL:
                Integer isMyFavouriteTail = logSearchSaveMapper.isMyFavouriteTail(MoneUserContext.getCurrentUser().getUser(), cmd.getTailId());
                if (isMyFavouriteTail >= 1) {
                    return Result.failParam("已收藏");
                }
                break;
        }

        MilogLogSearchSaveDO logSearchSaveDO = SearchSaveConvert.INSTANCE.toDO(cmd);
        long current = System.currentTimeMillis();
        logSearchSaveDO.setCreateTime(current);
        logSearchSaveDO.setUpdateTime(current);
        String user = MoneUserContext.getCurrentUser().getUser();
        logSearchSaveDO.setCreator(user);
        logSearchSaveDO.setUpdater(user);
        Integer maxOrder = logSearchSaveMapper.getMaxOrder(user, cmd.getSort());
        logSearchSaveDO.setOrderNum(maxOrder == null ? 100 : maxOrder + 100);
        int insert = logSearchSaveMapper.insert(logSearchSaveDO);
        return Result.success(insert);
    }

    public Result<Integer> update(SearchSaveUpdateCmd cmd) {
        MilogLogSearchSaveDO milogLogSearchSaveDO = logSearchSaveMapper.selectById(cmd.getId());
        if (milogLogSearchSaveDO == null) {
            return Result.failParam("找不到数据");
        }
        if (!cmd.getName().equals(milogLogSearchSaveDO.getName()) && isRepeatName(cmd.getName())) {
            return Result.failParam("名称不能重复");
        }
        milogLogSearchSaveDO.setName(cmd.getName());
        milogLogSearchSaveDO.setQueryText(cmd.getQueryText());
        milogLogSearchSaveDO.setIsFixTime(cmd.getIsFixTime());
        milogLogSearchSaveDO.setStartTime(cmd.getStartTime());
        milogLogSearchSaveDO.setEndTime(cmd.getEndTime());
        milogLogSearchSaveDO.setCommon(cmd.getCommon());
        milogLogSearchSaveDO.setUpdateTime(System.currentTimeMillis());
        milogLogSearchSaveDO.setUpdater(MoneUserContext.getCurrentUser().getUser());
        int i = logSearchSaveMapper.updateById(milogLogSearchSaveDO);
        return Result.success(i);
    }

    public Result<Integer> removeById(Long id) {
        int i = logSearchSaveMapper.removeById(id);
        return Result.success(i);
    }

    private boolean isRepeatName(String name) {
        Long count = logSearchSaveMapper.countByStoreAndName(name, MoneUserContext.getCurrentUser().getUser());
        return count >= 1;
    }

}
