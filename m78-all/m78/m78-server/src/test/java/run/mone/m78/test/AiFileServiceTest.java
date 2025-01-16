package run.mone.m78.test;

import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.service.fileserver.AiFileService;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author goodjava@qq.com
 * @date 2024/8/9 11:22
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class AiFileServiceTest {

    @Resource
    private AiFileService aiFileService;



    @Test
    public void testUploadFileToMoonshot() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "This is a test file".getBytes());
//        Result<String> result = aiFileService.uploadFileToMoonshot(mockFile);
//        assertNotNull(result);
//        assertNotNull(result.getData());
    }

    @Test
    public void testUploadFileToMoonshotWithEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/plain", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> {
            aiFileService.uploadFileToMoonshot(emptyFile);
        });
    }



}
