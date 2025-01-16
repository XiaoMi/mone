package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 绝对定位
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AbsolutePosition {

    //水平位置 0：水平居中，1：距离左侧固定值，2：距离右侧固定值
    private Integer horizontalPosition;

    //水平位置固定值，当水平位置为1或2时输入数值
    private BigDecimal horizontalFixedValue;

    //垂直位置 0：垂直居中，1：距离顶部固定值，2：距离底部固定值
    private Integer verticalPosition;

    //垂直位置固定值，当垂直位置为1或2时输入数值
    private BigDecimal verticalFixedValue;

}
