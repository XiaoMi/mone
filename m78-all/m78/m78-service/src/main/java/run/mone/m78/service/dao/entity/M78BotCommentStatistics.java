/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Table(value = "m78_bot_comment")
@ToString
public class M78BotCommentStatistics {

    private Long itemId;

    private int totalCount;

    private double averageScore;

    private int score1;

    private int score2;

    private int score3;

    private int score4;

    private int score5;

    private int badCount;

    private int mediumCount;

    private int goodCount;
}
