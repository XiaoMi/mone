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
//import lombok.Builder;
//import lombok.Data;
//import org.apache.commons.lang3.tuple.Pair;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @author goodjava@qq.com
// */
//@Data
//@Builder
//public class DockerQueryParam {
//
//    /**
//     * type = docker
//     */
//    private Pair<String, String> pair;
//
//    /**
//     * 需要的cup数量
//     */
//    private int cpuNum;
//
//    /**
//     * 需要的内存大小(单位是b)
//     */
//    private long mem;
//
//    /**
//     * 需要占用的端口
//     */
//    private Set<Integer> ports;
//
//
//    private String appName;
//
//    private Long projectId;
//
//    /**
//     * 0 增加
//     * 1 减少
//     */
//    private int type;
//
//    /**
//     * 已经安装过的ip 列表
//     */
//    private List<String> installedIps;
//
//
//
//    /**
//     * 是否是扩容
//     */
//    private boolean expansion;
//
//
//    private long envId;
//
//    /**
//     * 副本数量
//     */
//    private long num;
//
//    /**
//     * 真实的副本数量
//     */
//    private int realNum;
//
//    private Map<String,String> labels;
//
//    /**
//     * 被删除的机器
//     */
//    private List<MachineBo> removeMachines;
//
//    /**
//     * 是否使用k8s发布
//     */
//    private String k8s;
//
//    /**
//     * k8s 副本数量
//     */
//    private long replicateNum;
//
//
//}
