package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 背景
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Background {


    //背景类型：透明、颜色、图片
    private String backgroundType;

    //颜色
    private String backgroundColor;

    //背景图链接
    private String backgroundImageUrl;

    //背景图透明度
    private String backgroundImageTransparency;

    //背景图水平位置
    private String backgroundImageHorizontalPosition;

    //背景图垂直位置
    private String backgroundImageVerticalPosition;

}
