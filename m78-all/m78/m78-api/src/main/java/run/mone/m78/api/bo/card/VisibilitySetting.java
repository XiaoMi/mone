package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VisibilitySetting {

    //显隐类型：始终显示、满足条件时显示、满足条件时隐藏
    private String visibilityType;

    //左值
    private String key;

    private String operator;

    //右值类型：常量、变量
    private String valueType;

    private String value;

}