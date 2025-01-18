package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 属性
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Property {

    /**
     * 样式
     */
    private Form form;

    private Background background;

    private Operate operate;

    private Content content;

    //循环渲染
    private LoopRending loopRending;

    //显隐类型
    private VisibilitySetting visibilitySetting;

    private List<Slot> slots;

    //格数类型：0：固定格数，1：动态格数
    private Integer layoutType;

    //绑定数组变量
    private Integer boundArrayVariable;

    //每行展示数目
    private Integer itemsPerRow;



}
