package run.mone.ai.codegen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.ai.codegen.util.TemplateUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CodeGenerator {

    private final static Gson gson = new Gson();

    private static String className = "E";

    private static String testName = "T";

    private static String serviceName = "s";

    private static String basePath = "/Users/wodiwudi/java/nr-car-account";

    private static boolean createPojo = false;

    private static boolean createVo = false;

    private static boolean createTransfer = false;

    private static boolean createService = false;

    private static boolean createTest = true;

    private static boolean createController = false;

    /**
     * 主方法
     * 判断输入参数长度是否大于 0，调用方法处理参数并提取数据
     * 如果类名字符串为空则返回
     * 创建数据映射，放入相关数据
     * 根据模板文件和数据渲染并写入到相应文件（pojo.java、vo.java 等）
     */
    public static void main(String[] args) {
        Map<String, String> map = null;
        //方便ai调用的时候,设置表名
        if (args.length > 0) {
            map = parseArgsAndExtractData(args);
            className = map.get("pojoName");
            testName = map.get("testName");
            serviceName = map.get("serviceName");
        }
        if (StringUtils.isEmpty(className)) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("className", className);
        data.put("author", "goodjava@qq.com");
        data.put("serviceName", serviceName);
        Optional<String> first = Arrays.stream(testName.split("\\.")).findFirst();
        data.put("testName", first.get());
        // 调用方法并获取结果
        if (createPojo) {
            String pojo = TemplateUtils.renderTemplateFromFile("tlp/pojo.java", data);
            TemplateUtils.writeStringToFile(pojo, basePath + "/your project/src/main/java/run/mone/model/po/" + className + ".java");
        }

        if (createVo) {
            String vo = TemplateUtils.renderTemplateFromFile("tlp/vo.java", data);
            TemplateUtils.writeStringToFile(vo, basePath + "/your project/src/main/java/run/mone/api/vo/" + className + "VO.java");
        }

        if (createTransfer) {
            String transfer = TemplateUtils.renderTemplateFromFile("tlp/transfer.java", data);
            TemplateUtils.writeStringToFile(transfer, basePath + "/your project/src/main/java/run/mone/model/transfer/" + className + "Transfer.java");
        }

        if (createService) {
            String service = TemplateUtils.renderTemplateFromFile("tlp/service.java", data);
            TemplateUtils.writeStringToFile(service, basePath + "/your project/src/main/java/run/mone/service/" + className + "Service.java");
        }

        if (createTest) {
            String cn = testName;
            System.out.println("create test:" + cn);
            String test = TemplateUtils.renderTemplateFromFile("tlp/test.java", data);
            TemplateUtils.writeStringToFile(test, basePath + "/your project/src/test/java/com/xiaomi/nr/car/account/constant/" + cn);

        }

        if (createController) {
            String controller = TemplateUtils.renderTemplateFromFile("tlp/controller.java", data);
            TemplateUtils.writeStringToFile(controller, basePath + "/your project/src/main/java/run/mone/controller/" + className + "Controller.java");
        }
    }


    private static Map<String, String> parseArgsAndExtractData(String[] args) {
        String jsonStr = args[0];
        Type typeOfT = new TypeToken<Map<String, String>>() {
        }.getType();

        jsonStr = new String(Base64.getDecoder().decode(jsonStr));
        log.info("jsonStr:{}", jsonStr);

        Map<String, String> map = gson.fromJson(jsonStr, typeOfT);
        log.info("map:{}", map);
        return map;
    }
}
