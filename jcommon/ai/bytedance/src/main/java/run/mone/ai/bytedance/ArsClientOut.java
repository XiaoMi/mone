package run.mone.ai.bytedance;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

@Slf4j
public class ArsClientOut {

    public static String callArsClient(ArsRequest request) {
        String response = "";
        AsrClient asr_client = null;
        try {
            asr_client = AsrClient.build();
            asr_client.setAppid(request.getAppId());
            asr_client.setToken(request.getToken());
            asr_client.setCluster(request.getCluster());
            asr_client.setFormat(request.getAudio_format());
            asr_client.setShow_utterances(true);
            asr_client.asr_sync_connect();

            AsrResponse asr_response = asr_response = asr_client.asr_send(request.getAudio(), true);

            // get asr text
//            AsrResponse response = asr_client.getAsrResponse();
            for (AsrResponse.Result result: asr_response.getResult()) {
                response = response + result.getText();
            }
        } catch (Exception e) {
            log.error("callArsClient error", e);
        } finally {
            if (asr_client != null) {
                asr_client.asr_close();
            }
        }
        return response;
    }
}
