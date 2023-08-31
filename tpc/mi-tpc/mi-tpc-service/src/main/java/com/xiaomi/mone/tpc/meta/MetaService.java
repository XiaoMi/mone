package com.xiaomi.mone.tpc.meta;

import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.impl.FlagDao;
import com.xiaomi.mone.tpc.node.util.FlagUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 原信息存储服务
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/10 11:14
 */
@Slf4j
@Service
public class MetaService {

    @Autowired
    private FlagDao flagDao;

    public ResultVo<FlagVo> save(FlagAddParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        FlagEntity entity = flagDao.getOneByFlagNameAndkey(param.getParentId(), param.getType(), param.getFlagName(), param.getFlagKey());
        if (entity == null) {
            return add(param);
        }
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setFlagVal(param.getFlagVal());
        boolean result = flagDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(FlagUtil.toVo(entity));
    }

    public ResultVo<FlagVo> getOne(FlagQryOneParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        FlagEntity entity = flagDao.getOneByFlagNameAndkey(param.getParentId(), param.getType(), param.getFlagName(), param.getFlagKey());
        return ResponseCode.SUCCESS.build(FlagUtil.toVo(entity));
    }

    public ResultVo<FlagVo> add(FlagAddParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        FlagEntity entity = new FlagEntity();
        entity.setFlagName(param.getFlagName());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setType(param.getType());
        entity.setParentId(param.getParentId());
        entity.setFlagKey(param.getFlagKey());
        entity.setFlagVal(param.getFlagVal());
        boolean result = flagDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(FlagUtil.toVo(entity));
    }

    public ResultVo<FlagVo> edit(FlagEditParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        FlagEntity entity = flagDao.getById(param.getId(), FlagEntity.class);
        if (entity == null || !param.getType().equals(entity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        entity.setFlagName(param.getFlagName());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setFlagKey(param.getFlagKey());
        entity.setFlagVal(param.getFlagVal());
        boolean result = flagDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(FlagUtil.toVo(entity));
    }

    public ResultVo delete(FlagDeleteParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        FlagEntity entity = flagDao.getById(param.getId(), FlagEntity.class);
        if (entity == null || !param.getType().equals(entity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterType(param.getUserType());
        boolean result = flagDao.deleteById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo<PageDataVo<FlagVo>> list(FlagQryParam param) {
        param.setType(FlagTypeEnum.META_DATA.getCode());
        PageDataVo<FlagVo> pageData = param.buildPageDataVo();
        List<FlagEntity> entityList = flagDao.getListByPage(param.getParentId(), param.getType(), param.getFlagName(), param.getFlagKey(), pageData);
        pageData.setList(FlagUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

}
