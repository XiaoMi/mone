package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.util.List;
@Data
public class UpdateIndexTemplateCommand {
    private String indexTemplateName;
    private Integer indexShards;
    private Integer indexReplicas;
    private String lifecycle;

    private List<CreateIndexTemplatePropertyCommand> propertyList;

}
