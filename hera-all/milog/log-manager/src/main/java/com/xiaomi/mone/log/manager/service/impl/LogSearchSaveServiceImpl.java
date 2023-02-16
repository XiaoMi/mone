package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.convert.SearchSaveConvert;
import com.xiaomi.mone.log.manager.mapper.MilogLogSearchSaveMapper;
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
 *  服务实现类
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
        if (isRepeatName(cmd.getStoreId(), cmd.getName())) {
            return Result.failParam("名称不能重复");
        }
        MilogLogSearchSaveDO logSearchSaveDO = SearchSaveConvert.INSTANCE.toDO(cmd);
        long current = System.currentTimeMillis();
        logSearchSaveDO.setCreateTime(current);
        logSearchSaveDO.setUpdateTime(current);
        String user = MoneUserContext.getCurrentUser().getUser();
        logSearchSaveDO.setCreator(user);
        logSearchSaveDO.setUpdater(user);
        int insert = logSearchSaveMapper.insert(logSearchSaveDO);
        return Result.success(insert);
    }

    public Result<Integer> update(SearchSaveUpdateCmd cmd) {
        MilogLogSearchSaveDO milogLogSearchSaveDO = logSearchSaveMapper.selectById(cmd.getId());
        if (milogLogSearchSaveDO == null) {
            return Result.failParam("找不到数据");
        }
        if (!cmd.getName().equals(milogLogSearchSaveDO.getName()) && isRepeatName(cmd.getStoreId(), cmd.getName())) {
            return Result.failParam("名称不能重复");
        }
        milogLogSearchSaveDO.setName(cmd.getName());
        milogLogSearchSaveDO.setParam(cmd.getParam());
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

    private boolean isRepeatName(Long storeId, String name) {
        Long count = logSearchSaveMapper.countByStoreAndName(storeId, name);
        return count >= 1;
    }

}
