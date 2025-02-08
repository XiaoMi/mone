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
package run.mone.mcp.playwright.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;

import java.util.UUID;

@Service
@Slf4j
public class AudioService {

    private LLM asrLLM;
    private LLM ttsLLM;

    @PostConstruct
    private void init(){
        asrLLM = new LLM(LLMConfig.builder().llmProvider(LLMProvider.STEPFUN_ASR).build());
        ttsLLM = new LLM(LLMConfig.builder().llmProvider(LLMProvider.STEPFUN_TTS).build());
    }

    public String asr(String base64) {
        try {
            String filePath = System.getProperty("user.home") + "/" + UUID.randomUUID() + ".mp3";
            base64 = base64.substring(base64.indexOf(",") + 1);
            return asrLLM.transcribeAudio(filePath, base64);
        } catch (Exception e) {
            log.error("asr error , ", e);
            return null;
        }
    }

    public byte[] tts(String text) {
        try {
            return ttsLLM.generateSpeech(text);
        } catch (Exception e) {
            log.error("tts error , ", e);
            return null;
        }
    }
}
