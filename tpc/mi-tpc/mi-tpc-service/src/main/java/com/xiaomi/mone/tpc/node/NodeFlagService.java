package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.param.FlagAddParam;
import com.xiaomi.mone.tpc.common.param.FlagDeleteParam;
import com.xiaomi.mone.tpc.common.param.FlagEditParam;
import com.xiaomi.mone.tpc.common.param.FlagQryParam;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.impl.FlagDao;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.node.util.FlagUtil;
import com.xiaomi.mone.tpc.node.util.NodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class NodeFlagService implements NodeFlagHelper {

    @Autowired
    private FlagDao flagDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private NodeDao nodeDao;

    /**
     * 获取id最小的数据
     * @param parentId
     * @param type
     * @return
     */
    @Override
    public FlagVo getFirstOneByParentId(Long parentId, Integer type) {
        List<FlagEntity> flagEntities = flagDao.getListByNodeId(parentId, type);
        if (CollectionUtils.isEmpty(flagEntities)) {
            return null;
        }
        return FlagUtil.toVo(flagEntities.get(flagEntities.size() -  1));
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<FlagVo>> list(FlagQryParam param) {
        PageDataVo<FlagVo> pageData = param.buildPageDataVo();
        List<FlagEntity> entityList = flagDao.getListByPage(param.getParentId(), param.getType(), param.getFlagName(), param.getFlagKey(), pageData);
        pageData.setList(FlagUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<FlagVo> get(FlagQryParam param) {
        FlagEntity entity = flagDao.getById(param.getId(), FlagEntity.class);
        if (!param.getType().equals(entity.getType())) {
            return ResponseCode.SUCCESS.build();
        }
        return ResponseCode.SUCCESS.build(FlagUtil.toVo(entity));
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<FlagVo> add(FlagAddParam param) {
        NodeEntity parentNode = nodeDao.getById(param.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (FlagTypeEnum.IAM.getCode().equals(param.getType())) {
            if (!NodeTypeEnum.supportIamNode(parentNode.getType())) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            List<FlagEntity> entityList = flagDao.getListByNodeId(param.getParentId(), FlagTypeEnum.IAM.getCode());
            if (!CollectionUtils.isEmpty(entityList)) {
                return ResponseCode.OPER_ILLEGAL.build("只允许添加一条IAM配置");
            }
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        FlagTypeEnum.IAM.getCode().equals(param.getType());
        return realAdd(param, NodeUtil.toVo(parentNode));
    }

    @Override
    public ResultVo<FlagVo> realAdd(FlagAddParam param, NodeVo parentNode) {
        FlagEntity existFlagEntity = flagDao.getOneByFlagKey(parentNode.getId(), param.getType(), param.getFlagKey());
        if (existFlagEntity != null) {
            return ResponseCode.OPER_ILLEGAL.build("数据重复");
        }
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

    /**
     * 编辑
     * @param param
     * @return
     */
    public ResultVo<FlagVo> edit(FlagEditParam param) {
        FlagEntity entity = flagDao.getById(param.getId(), FlagEntity.class);
        if (entity == null || !param.getType().equals(entity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity parentNode = nodeDao.getById(entity.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (FlagTypeEnum.IAM.getCode().equals(param.getType()) && !NodeTypeEnum.supportIamNode(parentNode.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        FlagEntity existFlagEntity = flagDao.getOneByFlagKey(parentNode.getId(), param.getType(), param.getFlagKey());
        if (existFlagEntity != null && !existFlagEntity.getId().equals(entity.getId())) {
            return ResponseCode.OPER_ILLEGAL.build("数据重复");
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
        return ResponseCode.SUCCESS.build();
    }



    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo<SystemVo> delete(FlagDeleteParam param) {
        FlagEntity entity = flagDao.getById(param.getId(), FlagEntity.class);
        if (entity == null || !param.getType().equals(entity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity parentNode = nodeDao.getById(entity.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (FlagTypeEnum.IAM.getCode().equals(param.getType()) && !NodeTypeEnum.supportIamNode(parentNode.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
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

}
