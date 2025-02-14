package run.mone.mcp.feishu.model;

public enum BlockType {
    PAGE(1, "页面 Block"),
    TEXT(2, "文本 Block"),
    HEADING_1(3, "标题 1 Block"),
    HEADING_2(4, "标题 2 Block"),
    HEADING_3(5, "标题 3 Block"),
    HEADING_4(6, "标题 4 Block"),
    HEADING_5(7, "标题 5 Block"),
    HEADING_6(8, "标题 6 Block"),
    HEADING_7(9, "标题 7 Block"),
    HEADING_8(10, "标题 8 Block"),
    HEADING_9(11, "标题 9 Block"),
    BULLET_LIST(12, "无序列表 Block"),
    ORDERED_LIST(13, "有序列表 Block"),
    CODE(14, "代码块 Block"),
    QUOTE(15, "引用 Block"),
    TODO(17, "待办事项 Block"),
    BITABLE(18, "多维表格 Block"),
    CALLOUT(19, "高亮块 Block"),
    CHAT_CARD(20, "会话卡片 Block"),
    DIAGRAM(21, "流程图 & UML Block"),
    DIVIDER(22, "分割线 Block"),
    FILE(23, "文件 Block"),
    GRID(24, "分栏 Block"),
    GRID_COLUMN(25, "分栏列 Block"),
    IFRAME(26, "内嵌网页 Block"),
    IMAGE(27, "图片 Block"),
    WIDGET(28, "开放平台小组件 Block"),
    MINDNOTE(29, "思维笔记 Block"),
    SHEET(30, "电子表格 Block"),
    TABLE(31, "表格 Block"),
    TABLE_CELL(32, "表格单元格 Block"),
    VIEW(33, "视图 Block"),
    QUOTE_CONTAINER(34, "引用容器 Block"),
    TASK(35, "任务 Block"),
    OKR(36, "OKR Block"),
    OKR_OBJECTIVE(37, "OKR Objective Block"),
    OKR_KEY_RESULT(38, "OKR Key Result Block"),
    OKR_PROGRESS(39, "OKR 进展 Block"),
    DOC_WIDGET(40, "文档小组件 Block"),
    JIRA_ISSUE(41, "Jira 问题 Block"),
    WIKI_CATALOG(42, "Wiki 子目录 Block"),
    BOARD(43, "画板 Block"),
    AGENDA(44, "议程 Block"),
    AGENDA_ITEM(45, "议程项 Block"),
    AGENDA_ITEM_TITLE(46, "议程项标题 Block"),
    AGENDA_ITEM_CONTENT(47, "议程项内容 Block"),
    LINK_PREVIEW(48, "链接预览 Block"),
    UNSUPPORTED(999, "未支持 Block");

    private final int value;
    private final String description;

    BlockType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static BlockType fromValue(int value) {
        for (BlockType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNSUPPORTED;
    }
} 