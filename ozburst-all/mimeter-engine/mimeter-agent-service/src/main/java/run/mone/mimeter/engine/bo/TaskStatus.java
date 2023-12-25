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

package run.mone.mimeter.engine.bo;

/**
 *
 * @author zhangzhiyong
 * @date 29/05/2018
 */
public enum TaskStatus {
    Init(0),
    Success(1),
    Failure(2),
    Retry(3),
    Running(4),
    ;

    public int code;

    private TaskStatus(int code) {
        this.code = code;
    }
}
