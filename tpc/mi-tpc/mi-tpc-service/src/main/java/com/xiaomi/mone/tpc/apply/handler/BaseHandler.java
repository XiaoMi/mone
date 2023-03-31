package com.xiaomi.mone.tpc.apply.handler;

import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.common.enums.ApplyStatusEnum;
import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.param.ArgCheck;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 资源处理基类
 * @param <T>
 */
@Slf4j
public abstract class BaseHandler<T> {

    private static final Map<ApplyTypeEnum, BaseHandler> handlerMap = Maps.newConcurrentMap();

    public static final BaseHandler getHandler(ApplyTypeEnum applyTypeEnum) {
        if (applyTypeEnum == null) {
            return null;
        }
        return handlerMap.get(applyTypeEnum);
    }


    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeHelper nodeHelper;
    private final Class<T> cls;
    private boolean nolyCurNodeApproval;//仅需要当前节点审批
    private NodeTypeEnum stopNodeType;//审批流停止节点类型
    private final Map<Class<?>, Map<Class<?>, Field>> clsFields = Maps.newConcurrentMap();

    public BaseHandler(ApplyTypeEnum applyTypeEnum, boolean nolyCurNodeApproval) {
        this.cls = (Class<T>)applyTypeEnum.getCls();
        this.nolyCurNodeApproval = nolyCurNodeApproval;
        handlerMap.put(applyTypeEnum, this);
    }

    public BaseHandler(ApplyTypeEnum applyTypeEnum, NodeTypeEnum stopNodeType) {
        this.cls = (Class<T>)applyTypeEnum.getCls();
        this.stopNodeType = stopNodeType;
        handlerMap.put(applyTypeEnum, this);
    }

    public ResultVo applyHandler(NodeEntity curNode, ArgCheck arg, ApplyEntity applyEntity) {
        //项目类型下的子类型没有管理员，都上推到项目节点
        NodeEntity realNode = curNode;
        if (!NodeTypeEnum.supportMemberNode(curNode.getType())) {
            List<Long> parentIds = nodeHelper.getparentNodeIdList(curNode.getContent());
            List<NodeEntity> nodeEntities = nodeDao.getByIds(parentIds);
            if (CollectionUtils.isEmpty(nodeEntities)) {
                log.error("工单申请操作非法 curNode={}", curNode);
                return ResponseCode.OPER_ILLEGAL.build();
            }
            curNode = nodeEntities.stream().sorted(new Comparator<NodeEntity>() {
                @Override
                public int compare(NodeEntity o1, NodeEntity o2) {
                    return o2.getId().compareTo(o1.getId());
                }
            }).filter(e -> NodeTypeEnum.supportMemberNode(e.getType())).findFirst().get();
            applyEntity.setCurNodeId(curNode.getId());
            applyEntity.setCurNodeType(curNode.getType());
        }
        return applyHandlerImpl(realNode, curNode, (T)arg, applyEntity);
    }

    protected abstract ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, T arg, ApplyEntity applyEntity);

    public ResultVo approvalHandler(NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity){
        if (!nolyCurNodeApproval && !NodeTypeEnum.TOP_TYPE.getCode().equals(curNode.getType()) && !curNode.getType().equals(stopNodeType.getCode())) {
            applyEntity.setCurNodeId(curNode.getParentId());
            applyEntity.setCurNodeType(curNode.getParentType());
            return ResponseCode.SUCCESS.build();
        }
        T t = GsonUtil.gsonToBean(applyEntity.getContent(), cls);
        log.info("执行用户{}的工单{}资源申请", applyEntity.getApplyAccount(), applyEntity.getId());
        ResultVo resultVo = approvalHandlerImpl(t, curNode, applyApprovalEntity, applyEntity);
        if (resultVo != null && resultVo.success()) {
            applyEntity.setCurNodeId(applyEntity.getApplyNodeId());
            applyEntity.setCurNodeType(applyEntity.getApplyNodeType());
            applyEntity.setStatus(ApplyStatusEnum.FINSH.getCode());
        }
        return resultVo;
    }

    protected abstract ResultVo approvalHandlerImpl(T arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity);

    public Field getField(Object param) {
        Class<?> pramCls = param.getClass();
        Map<Class<?>, Field> fieldMap = clsFields.get(pramCls);
        if (fieldMap == null) {
            synchronized (pramCls) {
                fieldMap = clsFields.get(pramCls);
                if (fieldMap == null) {
                    fieldMap = Maps.newHashMap();
                    for (Field field : pramCls.getDeclaredFields()) {
                        field.setAccessible(true);
                        Class<?> fieldCls = field.getType();
                        if (!ArgCheck.class.isAssignableFrom(fieldCls)) {
                            continue;
                        }
                        fieldMap.put(fieldCls, field);
                    }
                    clsFields.put(pramCls, fieldMap);
                }
            }
        }
        return fieldMap.get(cls);
    }

}
