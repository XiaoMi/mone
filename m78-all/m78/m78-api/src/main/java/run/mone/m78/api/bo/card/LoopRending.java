package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 循环渲染
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoopRending {

    //是否循环渲染
    private Integer enableLoopRending;

    //绑定数组类型变量
    private String boundArrayVariable;

}
