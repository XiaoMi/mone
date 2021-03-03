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

package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

/**
 * 群详情
 */
@Data
public class GroupDetail {
    /**
     * 群头像
     */
    private String avatar;

    /**
     * 群描述
     */
    private String description;

    /**
     * 群ID
     */
    private String chat_id;

    /**
     * 群名称
     */
    private String name;

    /**
     * 群主的 open_id
     */
    private String owner_open_id;

    /**
     * 群主的 user_id（机器人是群主的时候没有这个字段）
     */
    private String owner_user_id;

}
