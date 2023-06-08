package com.xiaomi.mone.app.api.model.project.group;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/2 10:54 上午
 */
@Data
public class ProjectGroupTreeNode implements Serializable {

    private Integer id;
    private Integer type;
    private Integer relationObjectId;
    private String name;
    private String cnName;
    private List<ProjectGroupTreeNode> children;

    public ProjectGroupTreeNode(Integer id, Integer type, Integer relationObjectId, String name, String cnName) {
        this.id = id;
        this.type = type;
        this.relationObjectId = relationObjectId;
        this.name = name;
        this.cnName = cnName;
        this.children = new ArrayList<>();
    }

    public ProjectGroupTreeNode(HeraProjectGroupModel projectGroup) {
        this.id = projectGroup.getId();
        this.type = projectGroup.getType();
        this.relationObjectId = projectGroup.getRelationObjectId();
        this.name = projectGroup.getName();
        this.cnName = projectGroup.getCnName();
        this.children = new ArrayList<>();
    }
}
