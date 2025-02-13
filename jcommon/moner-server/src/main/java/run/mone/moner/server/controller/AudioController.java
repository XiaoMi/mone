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
package run.mone.moner.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import run.mone.moner.server.bo.Audio2TextParam;
import run.mone.moner.server.bo.Text2AudioParam;
import run.mone.moner.server.service.AudioService;

@Slf4j
@Controller
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @PostMapping("/textToAudio")
    public ResponseEntity<byte[]> textToAudio(@RequestBody Text2AudioParam audioParam, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        return new ResponseEntity<>(audioService.tts(audioParam.getText()), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/audioToText")
    @ResponseBody
    public Result<String> audioToText(@RequestBody Audio2TextParam audio) {
        if (audio == null || StringUtils.isEmpty(audio.getAudioBase64())) {
            return Result.fail(GeneralCodes.ParamError, "audio is empty");
        }
        try {
            return Result.success(audioService.asr(audio.getAudioBase64()));
        } catch (Exception e) {
            log.error("audio to text error, ", e);
            return Result.fail(GeneralCodes.InternalError, "audio to text error");
        }
    }
}
