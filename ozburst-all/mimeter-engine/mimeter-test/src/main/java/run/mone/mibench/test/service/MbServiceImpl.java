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

package run.mone.mibench.test.service;


import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 2022/6/4
 */
@Service(interfaceClass = MBService.class)
public class MbServiceImpl implements MBService{
    @Override
    public int sum(int a, int b) {
        return a+b;
    }

    @Override
    public MBRes call(MBReq req) {
        MBRes res = new MBRes();
        res.setId(req.getId());
        return res;
    }
}
