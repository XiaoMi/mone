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
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogStoreSpaceAuthDao;
import com.xiaomi.mone.log.manager.domain.Store;
import com.xiaomi.mone.log.manager.mapper.MilogLogSearchSaveMapper;
import com.xiaomi.mone.log.manager.model.convert.MilogSpaceConvert;
import com.xiaomi.mone.log.manager.model.convert.SearchSaveConvert;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.dto.SearchSaveDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceTreeFavouriteDTO;
import com.xiaomi.mone.log.manager.model.dto.StoreTreeDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogSearchSaveDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.SearchSaveInsertCmd;
import com.xiaomi.mone.log.manager.model.vo.SearchSaveUpdateCmd;
import com.xiaomi.mone.log.manager.service.IMilogLogSearchSaveService;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
@Service
@Slf4j
public class MilogLogSearchSaveServiceImpl implements IMilogLogSearchSaveService {

    @Resource
    private MilogLogSearchSaveMapper logSearchSaveMapper;

    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private MilogStoreSpaceAuthDao storeSpaceAuthDao;

    @Resource
    private MilogLogSearchSaveMapper searchSaveMapper;

    @Resource
    private TpcSpaceAuthService spaceAuthService;

    @Resource
    private Store store;

    public Result<List<SearchSaveDTO>> list(Long storeId, Integer sort) {
        List<SearchSaveDTO> list = logSearchSaveMapper.selectByCreator(MoneUserContext.getCurrentUser().getUser(), sort);
        if (CollectionUtils.isNotEmpty(list) && null != storeId) {
            list = list.stream().filter(searchSaveDTO -> Objects.equals(storeId, searchSaveDTO.getStoreId())).collect(Collectors.toList());
        }
        return Result.success(list);
    }

    public SearchSaveDTO getById(Long id) {
        return SearchSaveConvert.INSTANCE.fromDO(logSearchSaveMapper.selectById(id));
    }

    public Result<Integer> save(SearchSaveInsertCmd cmd) {
        if (cmd.getSort() == null) {
            return Result.failParam("The classification field sort cannot be empty");
        }
        switch (FavouriteSearchEnum.queryByCode(cmd.getSort())) {
            case TEXT:
                if (isRepeatName(cmd.getName())) {
                    return Result.failParam("Names cannot be duplicated");
                }
                break;
            case STORE:
                Integer isMyFavouriteStore = logSearchSaveMapper.isMyFavouriteStore(MoneUserContext.getCurrentUser().getUser(), cmd.getStoreId());
                if (isMyFavouriteStore >= 1) {
                    return Result.failParam("Bookmarked");
                }
            case TAIL:
                Integer isMyFavouriteTail = logSearchSaveMapper.isMyFavouriteTail(MoneUserContext.getCurrentUser().getUser(), cmd.getTailId());
                if (isMyFavouriteTail >= 1) {
                    return Result.failParam("Bookmarked");
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
            return Result.failParam("Data not found");
        }
        if (!cmd.getName().equals(milogLogSearchSaveDO.getName()) && isRepeatName(cmd.getName())) {
            return Result.failParam("Names cannot be duplicated");
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

    public Result<Boolean> swapOrder(Long idFrom, Long idTo) {
        MilogLogSearchSaveDO from = logSearchSaveMapper.selectById(idFrom);
        MilogLogSearchSaveDO to = logSearchSaveMapper.selectById(idTo);
        int t = from.getOrderNum();
        from.setOrderNum(to.getOrderNum());
        to.setOrderNum(t);
        int fromRes = logSearchSaveMapper.updateById(from);
        int toRes = logSearchSaveMapper.updateById(to);
        return Result.success(fromRes + toRes == 2);
    }

    public Result<Integer> defavourite(Integer sort, Long id) {
        if (sort == null || id == null) {
            Result.failParam("The parameter cannot be empty");
        }
        Map<String, Object> paramMap = new HashMap<>();
        switch (FavouriteSearchEnum.queryByCode(sort)) {
            case STORE:
                paramMap.put("store_id", id);
                break;
            case TAIL:
                paramMap.put("tail_id", id);
                break;
            default:
                return Result.failParam("Invalid sort field");
        }
        paramMap.put("creator", MoneUserContext.getCurrentUser().getUser());
        paramMap.put("sort", sort);
        int res = logSearchSaveMapper.deleteByMap(paramMap);
        return res == 1 ? Result.success() : Result.fail(CommonError.ParamsError);
    }

    public Result<List<SpaceTreeFavouriteDTO>> storeTree() {
        List<SpaceTreeFavouriteDTO> dtoList = new ArrayList<>();
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> userPermSpacePage = spaceAuthService.getUserPermSpace("", 0, Integer.MAX_VALUE);
        List<MilogSpaceDTO> spaceDTOList = MilogSpaceConvert.INSTANCE.fromTpcPage(userPermSpacePage.getData()).getList();
//        List<MilogSpaceDTO> spaceDTOList = space.getMilogSpaceByPage("", 0, Integer.MAX_VALUE).getList();
        if (CollectionUtils.isEmpty(spaceDTOList)) {
            return Result.success(dtoList);
        }
        List<Long> spaceIdList = spaceDTOList.stream().map(MilogSpaceDTO::getId).collect(Collectors.toList());
        List<MilogLogStoreDO> storeList = store.getStoreList(spaceIdList);
        Map<Long, List<MilogLogStoreDO>> spaceStoreMap = new HashMap<>();
        if (storeList != null && !storeList.isEmpty()) {
            for (MilogLogStoreDO store : storeList) {
                if (spaceStoreMap.containsKey(store.getSpaceId())) {
                    spaceStoreMap.get(store.getSpaceId()).add(store);
                } else {
                    List storeFerryList = new ArrayList();
                    storeFerryList.add(store);
                    spaceStoreMap.put(store.getSpaceId(), storeFerryList);
                }
            }
        }
        List<SearchSaveDTO> favouriteList = searchSaveMapper.selectByCreator(MoneUserContext.getCurrentUser().getUser(), FavouriteSearchEnum.STORE.getCode());
        Set<Long> favouriteStoreIdSet = new HashSet<>();
        if (favouriteList != null && !favouriteList.isEmpty()) {
            favouriteStoreIdSet = favouriteList.stream().map(SearchSaveDTO::getStoreId).collect(Collectors.toSet());
        }
        SpaceTreeFavouriteDTO dto;
        List<MilogLogStoreDO> storeFerryList;
        List<StoreTreeDTO> children;

        for (MilogSpaceDTO space : spaceDTOList) {
            dto = new SpaceTreeFavouriteDTO();
            dto.setValue(space.getId());
            dto.setLabel(space.getSpaceName());

            children = new ArrayList<>();
            storeFerryList = spaceStoreMap.get(space.getId());
            if (storeFerryList != null && !storeFerryList.isEmpty()) {
                for (MilogLogStoreDO storeDO : storeFerryList) {
                    children.add(StoreTreeDTO.Of(storeDO.getId(), storeDO.getLogstoreName(), favouriteStoreIdSet.contains(storeDO.getId()) ? 1 : 0));
                }
            }
            dto.setChildren(children);
            dtoList.add(dto);
        }
        return Result.success(dtoList);
    }

    public Result<Integer> initOrder(String key) {
        if (!"384384".equals(key)) {
            return null;
        }
        List<MilogLogSearchSaveDO> saveList = logSearchSaveMapper.getAll();
        Map<String, Integer> orderMap = new HashMap<>();
        if (saveList != null && !saveList.isEmpty()) {
            log.info("save List size is #{}", saveList.size());
            for (MilogLogSearchSaveDO save : saveList) {
                if (orderMap.containsKey(save.getCreator())) {
                    Integer order = orderMap.get(save.getCreator());
                    save.setOrderNum(order + 100);
                    orderMap.put(save.getCreator(), order + 100);
                } else {
                    save.setOrderNum(100);
                    orderMap.put(save.getCreator(), 100);
                }
                logSearchSaveMapper.updateById(save);
            }
        }

        return null;
    }
}
