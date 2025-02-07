package run.mone.m78.api.enums;

import lombok.Getter;


@Getter
public enum WorkChartTypeEnum {

    FLOWCHART("flowchart", "流程图"),
    SEQUENCE_DIAGRAM("sequenceDiagram", "序列图"),
    CLASS_DIAGRAM("classDiagram", "类图"),
    STATE_DIAGRAM("stateDiagram", "状态图"),
    ENTITY_RELATIONSHIP_DIAGRAM("entityRelationshipDiagram", "实体关系图"),
    USER_JOURNEY_DIAGRAM("userJourneyDiagram", "用户旅程图"),
    GANTT_DIAGRAM("ganttDiagram", "甘特图"),
    PIE_CHART("pieChart", "饼图"),
    QUADRANT_CHART("quadrantChart", "象限图"),
    REQUIREMENT_DIAGRAM("requirementDiagram", "需求图"),
    GIT_DIAGRAM("gitDiagram", "Git图"),
    MIND_MAP("mindMap", "思维导图"),
    TIMELINE("timelineDiagram", "时间线图");


    private String name;
    private String desc;

    WorkChartTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}
