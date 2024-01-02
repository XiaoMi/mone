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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Data
public class HttpData extends BaseData implements Serializable,Cloneable{

    private String url;

    private String method;

    private String contentType;

    private int timeout;

    private ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();

    private TspAuthInfoDTO tspAuthInfoDTO;

    private ApiX5InfoDTO apiX5InfoDTO;

    private volatile String postParamJson;

    private boolean enableTraffic;

    private int trafficConfId;

    public Map<String, String> httpGetParams() {
        Map<String, String> map = new HashMap<>();
        IntStream.range(0, this.getParamNum()).forEach(i -> map.put(this.getTypes().get(i).getName(), this.params.get(i).toString()));
        return map;
    }

    public Map<String, String> httpGetTmpParams(List<Object> params) {
        Map<String, String> map = new HashMap<>();
        IntStream.range(0, this.getParamNum()).forEach(i -> map.put(this.getTypes().get(i).getName(), params.get(i).toString()));
        return map;
    }

    @Override
    public HttpData clone() throws CloneNotSupportedException {
        return (HttpData) super.clone();
    }
}
