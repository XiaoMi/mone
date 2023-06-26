package com.xiaomi.mone.app.service.project.group;

import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupModel;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.dao.HeraProjectGroupDao;
import com.xiaomi.mone.app.enums.CommonError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author gaoxihui
 * @date 2023/6/2 11:24 上午
 */
@Slf4j
public class TreeQueryBuilder {

    @Autowired
    HeraProjectGroupDao projectGroupDao;

    private Set<HeraProjectGroupModel> data;
    private Map<Integer,List<HeraProjectGroupModel>> groupByParentId;

    public TreeQueryBuilder(Set<HeraProjectGroupModel> data){
        this.data = data;
        groupByParentId = new HashMap<>();
        groupDataByParentId();
    }

    private void groupDataByParentId() {
        for (HeraProjectGroupModel group : data) {
            int parentId = group.getParentGroupId();
            groupByParentId.computeIfAbsent(parentId, k -> new ArrayList<>()).add(group);
        }
    }

    private List<ProjectGroupTreeNode> buildTreeChildren(Integer parentId){

        List<ProjectGroupTreeNode> result = new ArrayList<>();
        List<HeraProjectGroupModel> groups = groupByParentId.getOrDefault(parentId, new ArrayList<>());

        for (HeraProjectGroupModel group : groups) {
            ProjectGroupTreeNode node = new ProjectGroupTreeNode(group.getId(), group.getType(), group.getRelationObjectId(),
                        group.getName(), group.getCnName(),group.getParentGroupId());
                List<ProjectGroupTreeNode> children = buildTreeChildren(group.getId());
                node.setChildren(children);
                result.add(node);
        }

        return result;
    }

    public Result<ProjectGroupTreeNode> getTreeByProjectGroup(HeraProjectGroupModel projectGroup){
        if(projectGroup == null || projectGroup.getId() == null){
            log.error("getTreeByProjectGroup error! param is invalid!");
            return Result.fail(CommonError.ParamsError);
        }
        ProjectGroupTreeNode treeNode = new ProjectGroupTreeNode(projectGroup);
        List<ProjectGroupTreeNode> projectGroupTreeNodes = buildTreeChildren(treeNode.getId());
        treeNode.setChildren(projectGroupTreeNodes);
        return Result.success(treeNode);
    }


}
