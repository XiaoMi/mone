package com.xiaomi.youpin.prometheus.agent.test;

import com.xiaomi.youpin.prometheus.agent.util.FileUtil;
import org.junit.jupiter.api.Test;

public class CommonTest {

    @Test
    public void testRenameFile() {
        String oldPath = "/tmp/fileTest/old";
        String newPath = "/tmp/fileTest/new";
        boolean b = FileUtil.RenameFile(oldPath, newPath);
        System.out.println(b);
    }

    @Test
    public void testDeleteFile() {
        String path = "/tmp/fileTest/toDelete";
        boolean b = FileUtil.DeleteFile(path);
        System.out.println(b);
    }

    @Test
    public void testReadFile() {
        String path = "/tmp/fileTest/readFile";
        String s = FileUtil.LoadFile(path);
        System.out.println("res: " + s);
    }

    @Test
    public void testWriteFile() {
        String path = "/tmp/fileTest/writefile";
        String test = FileUtil.WriteFile(path, "test");
        System.out.println(test);
    }

    @Test
    public void testGenerateFile() {
        String path = "/tmp/fileTest/generateFile";
        FileUtil.GenerateFile(path);
    }

}
