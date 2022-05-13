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

package run.mone.raft.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
@Data
public class RaftReq implements Serializable {

    public static final String BEAT = "beat";

    public static final String VOTE = "vote";

    public static final String PEER = "peer";

    private String cmd;

    /**
     * 投票的内容
     */
    private String vote;

    /**
     * 心跳的内容
     */
    private String beat;


    public static final String CLIENT = "client";
    public static final String DISTRO_FILTER = "distroFilter";

    private String from = CLIENT;

}
