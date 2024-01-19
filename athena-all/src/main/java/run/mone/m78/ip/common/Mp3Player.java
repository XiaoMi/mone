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

import com.intellij.openapi.diagnostic.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/1 13:49
 */
public class Mp3Player {

    private int point;
    private Clip clip;

    private static final Logger log = Logger.getInstance(Mp3Player.class);


    public void playMp3Url(String url) throws UnsupportedAudioFileException, IOException {
    }

    public void playMp3File(String path) throws UnsupportedAudioFileException, IOException {
    }

    private void playMp3File(AudioInputStream stream) throws IOException {
    }

    public void pause() {
        if (Objects.nonNull(clip)) {
            // 获取当前播放中继点
            point = clip.getFramePosition();
            // 停止播放
            clip.stop();
        }
    }

    public void play() {
        if (isNewBegin()) {
            // 重新播放一首歌
            try {
                playMp3Url();
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        } else {
            // 暂停继续播放
            // 设置播放点
            clip.setFramePosition(point);
            // 继续播放
            clip.start();
        }
    }

    public boolean isNewBegin() {
        return Objects.isNull(clip);
    }

    public void playMp3Url() throws UnsupportedAudioFileException, IOException {
        playMp3Url("");
    }


}
