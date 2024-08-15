package run.mone.knowledge.api.enums;

/**
 * @author goodjava@qq.com
 * @date 2024/1/15 14:44
 */
public enum KnowledgeTypeEnum {

    //项目代码 project-module-class
    project_code("project_code", 1, 2),
    //普通文档 knowledgeBase-file-block
    normal_document("normal_document", 1, 2);

    private String typeName;
    private int groupTagIndex;
    private int leafTagIndex;

    KnowledgeTypeEnum(String typeName, int groupTagIndex, int leafTagIndex) {
        this.typeName = typeName;
        this.groupTagIndex = groupTagIndex;
        this.leafTagIndex = leafTagIndex;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getGroupTagIndex() {
        return groupTagIndex;
    }

    public int getLeafTagIndex() {
        return leafTagIndex;
    }

    //判断某个string是否属于当前enum(class)
    public static boolean isEnumValueValid(String value) {
        for (KnowledgeTypeEnum enumValue : KnowledgeTypeEnum.values()) {
            if (enumValue.typeName.equals(value)) {
                return true;
            }
        }
        return false;
    }

    //根据传入的typeName获取该enum(class)
    public static KnowledgeTypeEnum getEnumByTypeName(String typeName) {
        for (KnowledgeTypeEnum enumValue : KnowledgeTypeEnum.values()) {
            if (enumValue.typeName.equals(typeName)) {
                return enumValue;
            }
        }
        return null;
    }

}
