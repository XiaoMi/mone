package run.mone.m78.test;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.code.CodeDTO;
import run.mone.m78.service.bo.code.Code;
import run.mone.m78.service.dao.entity.M78Code;
import run.mone.m78.service.service.code.M78CodeService;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INVALID_ARGUMENT;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author goodjava@qq.com
 * @date 2024/3/9 22:01
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class M78CodeServiceTest {

    @Resource
    private M78CodeService m78CodeService;


    @Test
    public void testCreateM78Code() {
        // 测试创建一个非空的M78Code对象
        M78Code m78Code = M78Code.builder().ctime(1L).utime(1L).code(Code.builder().code("code").build())
                .type(0)
                .name("n")
                .creator("a").desc("1").build();
        Result<Boolean> result = m78CodeService.createM78Code(m78Code);
        System.out.println(result);
    }

    @Test
    public void testGenerateCodeByPrompt() {
//        String comment = "计算两数和(a和b)";
        String comment = "计算两数(a和b)之差和两数之和，返回一个对象分别记录这两个值";
        String creator = "testCreator";
        Result<M78Code> result = m78CodeService.generateCodeByPrompt(comment, "gpt4_1106_2", creator, true);
        System.out.println(result);
    }

    @Test
    public void testGenerateSimpleDescription() {
        String code = "int add(int a, int b) { return a + b; }";
        Result<String> result = m78CodeService.generateSimpleDescription(code);
        System.out.println(result);
    }

    @Test
    public void testGetCodeDetailById() {
        Long validId = 5L; // 假设这是一个有效的ID
        Long invalidId = null; // 无效的ID
        Long notFoundId = -1L; // 假设这是一个数据库中不存在的ID

        // 测试无效ID的情况
        Result<CodeDTO> invalidResult = m78CodeService.getCodeDetailById(invalidId);
        assertEquals(STATUS_INVALID_ARGUMENT.getCode(), invalidResult.getCode());
        assertEquals("Invalid ID!", invalidResult.getMessage());

        // 测试数据库中不存在的ID的情况
        Result<CodeDTO> notFoundResult = m78CodeService.getCodeDetailById(notFoundId);
        assertEquals(STATUS_NOT_FOUND.getCode(), notFoundResult.getCode());
        assertEquals("M78 code not found", notFoundResult.getMessage());

        // 测试有效ID的情况
        // 这里需要确保数据库中有一个ID为validId的M78Code记录
        Result<CodeDTO> validResult = m78CodeService.getCodeDetailById(validId);
        assertEquals(GeneralCodes.OK.getCode(), validResult.getCode());
        assertNotNull(validResult.getData());
        assertEquals(validId, validResult.getData().getId());
    }


}
