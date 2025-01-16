package run.mone.m78.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.bo.multiModal.audio.AudioParam;
import run.mone.m78.service.service.multiModal.audio.AudioService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;

/**
 * @author wmin
 * @date 2024/1/16
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    /**
     * Converts the provided text to audio in MP3 format and returns it as a byte array in the response entity with appropriate HTTP headers.
     */
    @PostMapping("/textToAudio")
    public ResponseEntity<byte[]> textToAudio(@RequestBody AudioParam audioParam, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        return new ResponseEntity<>(audioService.textToAudio(audioParam), headers, HttpStatus.OK);
    }


    /**
     * This method is a POST mapping to "/audioToText" that takes an audio file as a parameter, checks if the file is empty, and if not, it uses the audioService to convert the audio to text and returns the result.
     */
    @PostMapping(value = "/audioToText")
    public Result<String> audioToText(HttpServletRequest request,
                                      @RequestParam("audioFile") MultipartFile audioFile) {
        if (audioFile.getSize() == 0) {
            return Result.fail(STATUS_BAD_REQUEST, "audio is empty");
        }
        return Result.success(audioService.audioToText(audioFile));
    }
}
