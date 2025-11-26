package run.mone.mcp.tts.service;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class PlaybackRunnable implements Runnable {
    // 设置音频格式，请根据实际自身设备，合成音频参数和平台选择配置
    // 这里选择24k、16bit、单通道，建议客户根据选用的模型采样率情况和自身设备兼容性选择其他采样率和格式
    private AudioFormat af;

    private DataLine.Info info;

    private SourceDataLine targetSource;

    private AtomicBoolean runFlag;

    private ConcurrentLinkedQueue<ByteBuffer> queue;

    public PlaybackRunnable(int sample_rate) {
        af = new AudioFormat(sample_rate, 16, 1, true, false);
        info = new DataLine.Info(SourceDataLine.class, af);
        targetSource = null;
        runFlag = new AtomicBoolean(true);
        queue = new ConcurrentLinkedQueue<>();
    }

    // 准备播放器
    public void prepare() throws LineUnavailableException {
        targetSource = (SourceDataLine) AudioSystem.getLine(info);
        targetSource.open(af, 4096);
        targetSource.start();
    }

    public void put(ByteBuffer buffer) {
        queue.add(buffer);
    }

    // 停止播放
    public void stop() {
        runFlag.set(false);
    }

    @Override
    public void run() {
        if (targetSource == null) {
            return;
        }
        while (runFlag.get()) {
            if (queue.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                continue;
            }
            ByteBuffer buffer = queue.poll();
            if (buffer == null) {
                continue;
            }
            byte[] data = buffer.array();
            targetSource.write(data, 0, data.length);
        }
        // 将缓存全部播放完
        if (!queue.isEmpty()) {
            ByteBuffer buffer = null;
            while ((buffer = queue.poll()) != null) {
                byte[] data = buffer.array();
                targetSource.write(data, 0, data.length);
            }
        }
        // 释放播放器
        targetSource.drain();
        targetSource.stop();
        targetSource.close();
    }
}
