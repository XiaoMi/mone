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

import com.google.gson.Gson;
import common.Replacer;
import common.Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author goodjava@qq.com
 * @date 2022/6/2
 */
@Data
@Slf4j
public class BaseData implements Serializable {

    private int paramNum;

    private List<ParamType> types = new CopyOnWriteArrayList<>();

    protected List<Object> params = new CopyOnWriteArrayList<>();

    /**
     * 源json参数体
     */
    private String originJsonParam = "";

    private AtomicReference<String> jsonParam = new AtomicReference<>("");


    private CopyOnWriteArrayList<OutputParam> outputParams;

    /**
     * 检查点
     */
    private CopyOnWriteArrayList<CheckPointInfo> checkPointInfoList;

    /**
     * 过滤条件
     */
    private CopyOnWriteArrayList<CheckPointInfo> filterCondition;

    private static final Gson gson = Util.getGson();

    public BaseData() {
    }

    public void initTypeList(List<ParamType> list) {
        this.types.addAll(list);
        this.paramNum = this.types.size();

        list.forEach(it -> {
            //若接口入参是对象
            if (it.getTypeEnum().equals(ParamTypeEnum.pojo)) {
                params.add(new ConcurrentHashMap<>());
            }
        });
    }

    /**
     * 用于上游节点接口动态更新本接口实际参数
     *
     * @ paramIndex 参数索引位置
     * @ paramName  参数名 若paramName 为例：${name} 类型，则直接替换
     * @ value      上游传下来的实际参数值
     */
    public void updateParam(List<Replacer> replacerList) {
        AtomicReference<String> body = new AtomicReference<>(getOriginJsonParam());

        replacerList.forEach(replacer -> {
            if (replacer.getParamName().startsWith("${")) {
                try {
                    //post请求 的 json参数
                    String valStr = replacer.getValue().toString();

                    if (!replacer.isForceStr()) {
                        if (NumberUtils.isNumber(valStr)) {
                            if (isNumeric(valStr)) {
                                replacer.setValue(Long.parseLong(valStr));
                            } else {
                                replacer.setValue(Double.parseDouble(valStr));
                            }
                        } else {
                            replacer.setValue(valStr);
                        }
                    }
//                    log.debug("updateParam param，name:{},body:{},value:{}", paramName, body, value);

                    body.set(Util.Parser.parse$(Util.getElKey(replacer.getParamName()).getKey(), body.get(), replacer.getValue()));
                } catch (Exception e) {
                    log.error("updateParam param after,body:{},time:{},error:{}", body.get(), System.currentTimeMillis(), e);
                }
                log.debug("updateParam param after,body:{},time:{}", body, System.currentTimeMillis());
            } else {
                ParamType type = types.get(replacer.getParamIndex());
                //对象类型
                if (type.getTypeEnum().equals(ParamTypeEnum.pojo)) {
                    ConcurrentHashMap<String, Object> params = (ConcurrentHashMap<String, Object>) this.params.get(replacer.getParamIndex());
                    params.put(replacer.getParamName(), replacer.getValue());
                }
                //基本类型
                if (type.getTypeEnum().equals(ParamTypeEnum.primary)) {
                    params.set(replacer.getParamIndex(), replacer.getValue());
                }
            }
        });
        getJsonParam().set(body.get());
    }

//    public synchronized void recoverJsonParam() {
//        setJsonParam(getOriginJsonParam());
//    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }
}
