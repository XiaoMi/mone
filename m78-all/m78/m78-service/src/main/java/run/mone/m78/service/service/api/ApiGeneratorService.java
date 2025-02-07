package run.mone.m78.service.service.api;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.infra.rpc.Result;
import org.springframework.stereotype.Service;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/21 14:35
 */
@Service
public class ApiGeneratorService {


    @Resource
    private ChatgptService chatgptService;

    @Resource
    private UserService userService;


    /**
     * 根据业务需求生成代码
     *
     * @param params 生成代码所需的参数
     * @param userName 用户名，用于获取用户配置
     * @return 生成的代码和参数的映射
     */
	//直接根据业务需求生成代码
    public Map<String, String> generateCodeUsingChatGptService(Map<String, String> params, String userName) {
        //调用接口生成代码
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
        Result<Map<String, String>> res = chatgptService.call("m78_groovy", params, Lists.newArrayList("code", "params"), model);
        return res.getData();
    }

    /**
     * 调用m78_sql_script_decide接口，决定是使用SQL还是脚本
     *
     * @param params 参数列表
     * @param userName 用户名
     * @return 包含决定结果的Map
     */
	//调用m78_sql_script_decide 看看是使用sql还是script(class)
    public Map<String, String> decideUseSqlOrScript(Map<String, String> params, String userName) {
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
        Result<Map<String, String>> res = chatgptService.call("m78_sql_script_decide", params, Lists.newArrayList("decide", "sql", "type"), model);
        return res.getData();
    }

    /**
     * 调用m78_groovy_comment生成这个Groovy脚本的买书信息
     *
     * @param params 参数信息
     * @param userName 用户名
     * @return 包含买书信息的Map
     */
	//调用m78_groovy_comment 生成这个groovy脚本的买书信息(class)
    public Map<String, String> generateGroovyScriptPurchaseInfo(Map<String, String> params, String userName) {
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
        Result<Map<String, String>> res = chatgptService.call("m78_groovy_comment", params, Lists.newArrayList("description", "parameters", "code"), model);
        return res.getData();
    }




}
