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

package com.xiaomi.mone.monitor.service.extension.impl;

import com.xiaomi.mone.monitor.bo.Pair;
import com.xiaomi.mone.monitor.bo.PlatForm;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shanwb
 * @date 2023-04-20
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class PlatFormTypeExtensionServiceImpl implements PlatFormTypeExtensionService {

    @Override
    public boolean belongPlatForm(Integer typeCode, PlatForm platForm) {
        for (PlatFormType pft : PlatFormType.values()) {
            if (pft.getPlatForm() == platForm) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Integer getMarketType(Integer typeCode) {
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values){
            if(value.getCode().equals(typeCode)){
                //通过code 给marketCode
                return value.getMarketCode();
            }
        }

        return -1;
    }

    @Override
    public List<Pair> getPlatFormTypeDescList() {
        List <Pair> list = new ArrayList<>();
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values) {
            Pair pair = new Pair(value.getCode(), value.getDesc());
            list.add(pair);
        }
        return list;
    }

    @Override
    public String getGrafanaDirByTypeCode(Integer typeCode) {
        PlatFormType[] values = PlatFormType.values();
        for (PlatFormType value : values) {
            if (value.getCode().equals(typeCode)) {
                return value.getGrafanaDir();
            }
        }

        return null;
    }

    @Override
    public boolean checkTypeCode(Integer typeCode) {
        PlatFormType[] values = PlatFormType.values();
        for(PlatFormType value : values) {
            if(value.getCode().equals(typeCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getTypeCodeByName(String typeName) {
        PlatFormType[] values = PlatFormType.values();
        for (PlatFormType value : values) {
            if(value.getName().equals(typeName)) {
                return value.getCode();
            }
        }
        return null;
    }

}
