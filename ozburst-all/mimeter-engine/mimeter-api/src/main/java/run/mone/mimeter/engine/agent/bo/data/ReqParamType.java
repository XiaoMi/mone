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

public enum ReqParamType {

    Http_Get(1),
    Http_Post_Form(2),
    Http_Post_Json(3),
    Dubbo(4);

    public final int code;

    private ReqParamType(int code) {
        this.code = code;
    }
}
