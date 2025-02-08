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
package run.mone.m78.common;

public class WebsocketMessageType {

    /**
     * 工作流执行状态的消息
     */
    public static final String FLOW_EXECUTE_STATUS = "FLOW_EXECUTE_STATUS";

    public static final String FLOW_EXECUTE_FAILURE = "FLOW_EXECUTE_FAILURE";

    public static final String FLOW_EXECUTE_MESSAGE = "FLOW_EXECUTE_MESSAGE";


    /**
     * bot返回最终结果的消息
     */
    public static final String BOT_RESULT = "BOT_RESULT";

    public static final String BOT_STATE_RESULT = "BOT_STATE_RESULT";

    public static final String IMAGE_STREAM_END = "IMAGE_STREAM_END";


    public static final String BOT_STREAM_RESULT = "BOT_STREAM_RESULT";

    public static final String BOT_STREAM_FAILURE = "BOT_STREAM_FAILURE";

    public static final String BOT_STREAM_BEGIN = "BOT_STREAM_BEGIN";

    public static final String BOT_STREAM_EVENT = "BOT_STREAM_EVENT";

    public static final String ANSWER_RESULT = "ANSWER_RESULT";

    public static final String MESSAGE_TYPE_KEY = "messageType";

    public static final String MULTI_MODAL_AUDIO_END = "END";

    public static final String MULTI_MODAL_AUDIO_BASE64 = "audioBase64";

    public static final String MULTI_MODAL_AUDIO_STREAM_FAIL_MESSAGE = "AUDIO_STREAM_FAIL_MESSAGE";

    public static final String MULTI_MODAL_AUDIO_STREAM_START = "AUDIO_STREAM_START";

    public static final String MULTI_MODAL_AUDIO_STREAM_RESULT = "AUDIO_STREAM_RESULT";

    public static final String MULTI_MODAL_AUDIO_STREAM_END = "AUDIO_STREAM_END";

}
