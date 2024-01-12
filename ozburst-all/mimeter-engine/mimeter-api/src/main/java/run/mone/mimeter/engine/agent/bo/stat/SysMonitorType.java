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

package run.mone.mimeter.engine.agent.bo.stat;

public enum SysMonitorType {
    Cpu_Usage("cpuUsage"),
    Mem_Used("memUsage"),
    Load_Avg("load_avg"),
    Load_Max("load_max"),

    ;

    public final String name;

    SysMonitorType(String name) {
        this.name = name;
    }
}
