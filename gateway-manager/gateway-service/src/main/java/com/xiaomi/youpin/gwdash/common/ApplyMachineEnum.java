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
//package com.xiaomi.youpin.gwdash.common;
//
//import com.xiaomi.youpin.gwdash.service.factory.ApplyMachineParamsFactory;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//public enum ApplyMachineEnum {
//    ALIV2("ali_va2", "bjsali", "V2", ApplyMachineParamsFactory.ALIONLINE, Consts.APPLY_MACHINE_ENV_ONLINE, "bjsali"),
//    ALIV4("ali_va4", "bjsali", "V4", ApplyMachineParamsFactory.ALIONLINE, Consts.APPLY_MACHINE_ENV_ONLINE, "bjsali"),
//    KSCV3_2("ksc_va3.2", "ksybj","V3.2", ApplyMachineParamsFactory.KSCONLINE, Consts.APPLY_MACHINE_ENV_ONLINE, "ksybj"),
//    KSVG3_2("ksc_vg3.2", "ksybj","VG3.2", ApplyMachineParamsFactory.KSCONLINE, Consts.APPLY_MACHINE_ENV_ONLINE, "ksybj"),
//    KSCVA2_4("ksc_va2.4", "ksywq","V2.4", ApplyMachineParamsFactory.KSCStaging, Consts.APPLY_MACHINE_ENV_STAGING, "ksywq"),
//    KSCVA1_4("ksc_va1.4", "ksywq","V1.4", ApplyMachineParamsFactory.KSCStaging, Consts.APPLY_MACHINE_ENV_STAGING, "ksywq"),
//    KSCVA4_2("ksc_va4.2", "ksywq","V4.2", ApplyMachineParamsFactory.KSCStaging, Consts.APPLY_MACHINE_ENV_STAGING, "ksywq");
//
//    private String suitId;
//    private String siteId;
//    private String desc;
//    private String param;
//    private String env;
//    private String provider;
//
//    public String getSuitId() { return suitId; }
//
//    public String getDesc() { return desc; }
//
//    public String getParam() { return param; }
//
//    public String getEnv() { return env; }
//
//    public String getProvider() { return provider; }
//
//    public String getSiteId() { return siteId; }
//
//    ApplyMachineEnum(String suitId, String siteId, String desc, String param, String env, String provider) {
//        this.suitId = suitId;
//        this.siteId = siteId;
//        this.desc = desc;
//        this.param = param;
//        this.env = env;
//        this.provider = provider;
//    }
//
//    public static boolean isValidity(String suitId, String siteId, String env) {
//        Optional<ApplyMachineEnum> optional = Arrays.stream(ApplyMachineEnum.values())
//                .filter(it -> it.getSuitId().equals(suitId) && it.getSiteId().equals(siteId) && it.getEnv().equals(env))
//                .findFirst();
//        if (optional.isPresent()) {
//            return true;
//        }
//        return false;
//    }
//
//    public static Optional<ApplyMachineEnum> getApplyMachineEnum(String suitId, String siteId, String env) {
//        Optional<ApplyMachineEnum> optional = Arrays.stream(ApplyMachineEnum.values())
//                .filter(it -> it.getSuitId().equals(suitId) && it.getSiteId().equals(siteId) && it.getEnv().equals(env))
//                .findFirst();
//        return optional;
//    }
//
//}
