package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
public interface NodeHelper {

    ResultVo<NodeVo> add(boolean force, NodeAddParam param);

    ResultVo<NodeVo> realAdd(NodeAddParam param, NodeEntity parentNode);

    boolean isMgrOrSuperMgr(Long userId, NodeEntity parentNode);

    boolean isMgrOrSuperMember(Long userId, NodeEntity node);

    boolean isMemberOrSuperMember(Long userId, NodeEntity node);

    boolean isMgr(Long userId, Long nodeId);

    boolean isTopMgr(Long userId);

    List<Long> getparentNodeIdList(String content);

    NodeEntity buildCurNode(NodeAddParam param, NodeEntity parentNode, List<Long> allParentIds);

    ResultVo<NodeVo> realEdit(NodeEditParam param, NodeEntity curNode);

    ResultVo cascadeDelete(boolean force, BaseParam param, NodeEntity curNode);

    ResultVo<NodeVo> get(BaseParam param, NodeEntity nodeEntity, boolean needParent);

    ResultVo move(boolean force, NodeMoveParam param);

    String rebuildContentForPids(String content, List<Long> parentNodeIdList);

}
