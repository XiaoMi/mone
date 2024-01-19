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

package run.mone.m78.ip.common;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;


/**
 * @Author goodjava@qq.com
 * @Date 2021/10/30 20:27
 */
public class OggPlayer {

    public static void main(String[] args) {
        OggPlayer player = new OggPlayer();
        player.play("/tmp/abc.ogg");
    }

    public void play(String filePath) {
        final File file = new File(filePath);

        try {
//            final AudioInputStream in = new VorbisAudioFileReader().getAudioInputStream(file);
            final AudioInputStream in = null;
            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            if (line != null) {
                line.open(outFormat);
                line.start();
                stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                line.drain();
                line.stop();
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }


}
