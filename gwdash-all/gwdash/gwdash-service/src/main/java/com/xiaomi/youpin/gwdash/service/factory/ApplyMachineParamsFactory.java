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

package com.xiaomi.youpin.gwdash.service.factory;

import com.xiaomi.youpin.gwdash.service.factory.impl.AliOnlineApplyMachineParam;
import com.xiaomi.youpin.gwdash.service.factory.impl.AwsEc2OnlineApplyMachineParam;
import com.xiaomi.youpin.gwdash.service.factory.impl.KscOnlineApplyMachineParam;
import com.xiaomi.youpin.gwdash.service.factory.impl.KscStagingApplyMachineParam;
import org.springframework.stereotype.Service;

@Service
public class ApplyMachineParamsFactory {

    public static final String ALIONLINE = "AliOnline";
    public static final String KSCStaging = "KscStaging";
    public static final String KSCONLINE = "KscOnline";
    public static final String AWS_EC2_ONLINE = "AwsEc2Online";

    public IApplyMachineParam getParamMake(String param) {
        if (ALIONLINE.equals(param)) {
            return new AliOnlineApplyMachineParam();
        } else if (KSCONLINE.equals(param)) {
            return new KscOnlineApplyMachineParam();
        } else if (KSCStaging.equals(param)) {
            return new KscStagingApplyMachineParam();
        } else if (AWS_EC2_ONLINE.equals(param)) {
            return new AwsEc2OnlineApplyMachineParam();
        }
        return null;
    }
}
