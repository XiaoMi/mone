/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.service.PartnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dongzhenxing on 2023/2/9 2:16 PM
 */
@Service
public class PartnerServiceImpl implements PartnerService {
    @Override
    public Map<String, List<Map<Integer, String>>> getPartnerList(Integer projectID) {
        return null;
    }

    @Override
    public Map<String, List<Map<Integer, String>>> getGroupPartnerList(Integer groupID) {
        return null;
    }

    @Override
    public Result<List<Map<Integer, String>>> getAllPartnerList() {
        return null;
    }
}
