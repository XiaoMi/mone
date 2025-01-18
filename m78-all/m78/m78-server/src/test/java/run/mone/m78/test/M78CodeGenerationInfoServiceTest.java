package run.mone.m78.test;

import com.mybatisflex.core.paginate.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.entity.M78CodeGenerationInfo;
import run.mone.m78.service.service.code.generation.info.M78CodeGenerationInfoService;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author goodjava@qq.com
 * @date 2024/6/12 09:13
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class M78CodeGenerationInfoServiceTest {

    @Resource
    private M78CodeGenerationInfoService service;


    @Test
    public void testUploadCodeInfo() {
        M78CodeGenerationInfo codeInfo = new M78CodeGenerationInfo();
        // 假设M78CodeGenerationInfo类有一个设置属性的方法setSomeProperty
        // 你需要根据实际情况来设置对象的属性，以下只是示例
        long now = System.currentTimeMillis();
        codeInfo.setCtime(now);
        codeInfo.setUtime(now);
        codeInfo.setState(0);
        codeInfo.setProjectName("dingtao-test");
        codeInfo.setClassName("Test");
        codeInfo.setCodeLinesCount(20);
        codeInfo.setMethodName("a");
        codeInfo.setUsername("dingtao");
        codeInfo.setAction(77);
        codeInfo.setAnnotation("// 测试");
        codeInfo.setType(1);
        codeInfo.setSource(1);
        codeInfo.setPluginVersion("IDEA:2024.06.01.1");
        codeInfo.setIdeVersion("idea-1");
        codeInfo.setIp("127.0.0.1");

        // 假设save方法返回true表示上传成功
        boolean expected = true;
        boolean actual = service.uploadCodeInfo(codeInfo);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTotalCodeLinesByUser() {
        String userName = "zzy";
        long expectedTotalCodeLines = 10L; // 假设预期的代码行数为100行
        long actualTotalCodeLines = service.getTotalCodeLinesByUser(userName);
        assertEquals(expectedTotalCodeLines, actualTotalCodeLines);
    }

    @Test
    public void testGetUserCodeLinesGroupedByUser() {
        int currentPage = 1;
        int pageSize = 10;
        Page<M78CodeGenerationInfo> resultPage = service.getUserCodeLinesGroupedByUser(currentPage, pageSize);
        assertNotNull(resultPage);
    }

    @Test
    public void testGetTodayCodeLinesByUser() {
        String userName = "zzy";
        long expectedCodeLinesToday = 10L; // 假设预期今天alice生成的代码行数为50行

        // 以下代码假设有一个方法来模拟今天alice生成的代码行数
        // 这通常涉及到数据库操作，比如插入或更新记录来反映alice今天的代码行数
        // 由于这是一个单元测试，你可能需要使用Mockito等工具来模拟数据库操作的结果
        // 以下仅为示例，具体实现取决于你的测试环境和需求
        simulateUserCoding(userName, expectedCodeLinesToday);

        long actualCodeLinesToday = service.getTodayCodeLinesByUser(userName);
        assertEquals(expectedCodeLinesToday, actualCodeLinesToday);
    }

    // 这个simulateUserCoding方法是一个假设的辅助方法，用于模拟用户的代码生成行为
    // 实际测试中，你需要根据你的应用程序的具体情况来实现这个方法
    private void simulateUserCoding(String userName, long codeLines) {
        // 模拟数据库操作，插入或更新记录以反映用户今天的代码行数
        // 例如，使用Mockito来模拟service层或repository层的行为
    }


}
