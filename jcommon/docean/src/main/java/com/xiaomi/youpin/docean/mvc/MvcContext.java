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

package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.mvc.session.HttpSession;
import com.xiaomi.youpin.docean.mvc.session.HttpSessionManager;
import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Data
public class MvcContext {

    private String traceId;

    private Map<String, String> attachments;

    private Map<String, String> headers;

    private boolean websocket;


    public HttpSession session() {
        return HttpSessionManager.getSession(this);
    }

    private String sessionId = "";

}
