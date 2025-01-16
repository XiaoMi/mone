package run.mone.m78.service.service.multiModal.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;

@Slf4j
@Component
public class SilenceDetector {

    private double silenceThreshold = -70.0;

    private int silenceDurationThreshold = 3000;

    private AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);

    public boolean isSilence(byte[] audioData) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromByteArray(audioData, format, 1024, 0);
            be.tarsos.dsp.SilenceDetector silenceDetector = new be.tarsos.dsp.SilenceDetector(silenceThreshold, false);
            dispatcher.addAudioProcessor(silenceDetector);
            dispatcher.run();
            return silenceDetector.currentSPL() < silenceThreshold;
        } catch (Exception e) {
            log.error("Error in silence detection", e);
            return false;
        }
    }

    public boolean isSentenceEnd(long lastUpdateTime) {
        return System.currentTimeMillis() - lastUpdateTime > silenceDurationThreshold;
    }
}