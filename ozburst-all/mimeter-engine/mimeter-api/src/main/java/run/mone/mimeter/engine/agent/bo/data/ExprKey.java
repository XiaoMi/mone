/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/6/2
 */
@Data
public class ExprKey implements Serializable {

    /**
     * 从那个顶点取值
     */
    private int index;

    /**
     * 类型 1 txt 2 json
     */
    private int origin;

    /**
     * 取完值的名字
     */
    private String name;

    /**
     * 取值表达式
     */
    private String expr;

    /**
     * 放入task data中的表达式规则  demoData->0->i
     */
    private List<String> putValueExpr;

    public ExprKey() {
    }

    public ExprKey(int index, int origin, String name, String expr, List<String> putValueExpr) {
        this.index = index;
        this.origin = origin;
        this.name = name;
        this.expr = expr;
        this.putValueExpr = putValueExpr;
    }
}
