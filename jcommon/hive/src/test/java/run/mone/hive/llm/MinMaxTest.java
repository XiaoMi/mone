package run.mone.hive.llm;

import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.hive.llm.impl.minmax.MiniMax;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2025/4/23 16:49
 */
public class MinMaxTest {


    @SneakyThrows
    @Test
    public void test() {
        byte[] data = new MiniMax().generateAudio(System.getenv("MINIMAX_GROUP_ID"), System.getenv("MINIMAX_API_KEY"), """
                莫听穿林打叶声，何妨吟啸且徐行。
                竹杖芒鞋轻胜马，谁怕？一蓑烟雨任平生。
                料峭春风吹酒醒，微冷，山头斜照却相迎。
                回首向来萧瑟处，归去，也无风雨也无晴。
                """);
        Files.write(Paths.get("/tmp/a.mp3"), data);
    }

}
