package run.mone.mcp.song.function;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.mcp.spec.McpSchema;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SongFunctionTest {

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        SongFunction songFunction = new SongFunction();

        // 创建测试用的音频文件
        File instrumental = new File("/opt/workspace_all/workspace_ozx/ai-singer-py/final_merge/001/instrument_zdsjjt.mp3_10.flac");
        File vocals = new File("/opt/workspace_all/workspace_ozx/ai-singer-py/final_merge/001/audio.wav");
        Path tempDir = Path.of("/opt/workspace_all/workspace_ozx/ai-singer-py/final_merge/001") ;

        File output = new File(tempDir.toFile(), "output.wav");

        // 准备测试参数
        Map<String, Object> parms = new HashMap<>();
        parms.put("command", "merge");
        parms.put("instrumentalPath", instrumental.getAbsolutePath());
        parms.put("vocalsPath", vocals.getAbsolutePath());
        parms.put("outputPath", output.getAbsolutePath());
        parms.put("instrumentalVolume", 1.0);
        parms.put("vocalsVolume", "10dB");

        // 执行合并操作
        McpSchema.CallToolResult result = songFunction.apply(parms);
        System.out.println(new Gson().toJson(result));
        // 验证结果
        //assertFalse(result.error());
        assertTrue(output.exists());
        assertTrue(output.length() > 0);

        // 验证输出文件是否是有效的音频文件
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(output);
        assertNotNull(audioInputStream);

        // 验证音频格式
        AudioFormat format = audioInputStream.getFormat();
        assertEquals(44100.0f, format.getSampleRate());
        assertEquals(16, format.getSampleSizeInBits());
        assertEquals(2, format.getChannels());
    }

} 