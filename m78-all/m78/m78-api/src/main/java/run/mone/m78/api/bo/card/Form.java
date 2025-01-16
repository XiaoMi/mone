package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 样式
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Form {

    //尺寸：大、小
    private String size;

    //行间距
    private Integer rowGap;

    //内边距
    private Integer padding;

    //垂直对齐
    private String vertical;

    //水平对齐
    private String horizontal;

    //宽度比例：按权重分配weight、自适应内容auto
    private String width;

    private Integer weight;

    private Integer enableShrink;

    private Integer roundedSize;

    //是否显示边框： 0：不显示，1：显示
    private Integer enableShowBorder;

    //颜色
    private  String colour;

    //字体权重：0：常规，1：加粗
    private Integer fontWeight;

    //字体大小: 0：特小，1：小，2：中，3：大
    private Integer fontSize;

    //对齐方式： 0：居左， 1：居中， 2：居右
    private Integer alignment;

    //是否开启最大行数
    private Integer enableMaxRows;

    //最大行数
    private Integer maxRows;

    //是否开启固定行数
    private Integer enableFixedRows;

    //固定行数
    private Integer fixedRows;

    //定位方式： 0：自动定位， 1：绝对定位
    private Integer positionWay;

    //绝对定位参数
    private AbsolutePosition absolutePosition;

    //是否开启最大宽度
    private Integer enableMaxWidth;

    //最大宽度(只接受整型)
    private Integer maxWidth;








}
