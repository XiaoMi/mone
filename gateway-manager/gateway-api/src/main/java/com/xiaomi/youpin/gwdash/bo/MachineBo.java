///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.bo;
//
//import lombok.Data;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Optional;
//
///**
// * @author tsingfu
// */
//@Data
//public class MachineBo implements Serializable {
//
//    private long id;
//
//    private String name;
//
//    private String ip;
//
//    private String hostname;
//
//    private String group;
//
//    private String desc;
//
//    private MachineLabels labels;
//
//    private MachineLabels prepareLabels;
//
//    private List<Integer> cpuCore;
//
//    /**
//     * order 越大,优先级越高
//     */
//    private Integer order = 0;
//
//    private Long utime;
//
//
//    /**
//     * 会获取前缀相同的值,然后加一起
//     *
//     * @param key
//     * @return
//     */
//    public Long getPrepareLabelValue(String key) {
//        if (null == prepareLabels) {
//            return 0L;
//        }
//
//        Optional<Long> v = this.prepareLabels.entrySet()
//                .stream()
//                .filter(it -> it.getKey().startsWith(key))
//                .map(it -> Long.valueOf(it.getValue()))
//                .reduce((a, b) -> a + b);
//
//        return v.orElse(0L);
//    }
//
//
//    public Long getLabelValue(String key) {
//        if (null == labels) {
//            return 0L;
//        }
//
//        String v = this.labels.get(key);
//        if (StringUtils.isEmpty(v)) {
//            return 0L;
//        }
//        return Long.valueOf(v);
//    }
//
//
//}
