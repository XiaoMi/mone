package run.mone.local.docean.service.tool;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.dto.ExecCommandResult;
import run.mone.local.docean.service.api.OSStrategy;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzhidong
 * @author goodjava@qq.com
 * @date 2024/2/27 14:02
 * <p>
 * 操作操作系统
 */
@Slf4j
@Service
public class OSXService implements ToolService, OSStrategy {
    private final String searchPath = "/Applications/:/System/Library/CoreServices/:" + System.getenv("HOME") + "/Applications/";
    @Resource
    private TerminalExecutorService executorService;

    public JsonElement execute(JsonElement params) {
        log.info("execute os X service params:{}", params);
        if (params.isJsonObject()) {
            JsonObject obj = (JsonObject) params;
            String cmd = obj.get("cmd").getAsString();
            if ("open".equals(cmd)) {
                open(obj.get("param").getAsString());
            }else if ("notify".equals(cmd)) {
                notify(obj.get("title").getAsString(),obj.get("param").getAsString());
            }
        }
        return null;
    }

    public void open(String path) {
        String fPath = path;
        File file = new File(fPath);
        if (!file.exists()) {
            for (String pre : searchPath.split(":")) {
                file = new File(pre + path);
                if (file.exists()) {
                    fPath = pre + path;
                    break;
                }
            }
        }
        exec("open", fPath);
    }

    public void notify(String title, String msg) {
        if (StringUtils.isEmpty(title)) {
            title = "Tianye";
        }
        exec("osascript", "-e", String.format("display notification \"%s\" with title \"%s\"", msg, title));
    }

    public ExecCommandResult dialog(String title, String msg) {
        if (StringUtils.isEmpty(title)) {
            title = "Tianye";
        }
        Result<ExecCommandResult> res= exec("osascript", "-e", String.format("display dialog \"%s\" with title \"%s\"", msg, title));
        return res.getData();
    }

    private Result<ExecCommandResult> exec(String... command) {
        ExecCommandResult execCommandResult = new ExecCommandResult();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            // 设置命令和参数
            builder.command(command);

            // 启动进程
            Process process = builder.start();

            // 读取命令执行结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            List<String> resultData = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                resultData.add(line);
            }
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                resultData.add(line);
            }
            log.info("exec result data：{}", new Gson().toJson(resultData));

            // 等待命令执行完成
            int exitCode = process.waitFor();
            execCommandResult.setResultCode(exitCode);
            execCommandResult.setResultData(resultData);

            log.info("exec command ：{} , exitCode : {}, resultData : {}", command, exitCode, resultData);
            return Result.success(execCommandResult);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail(GeneralCodes.ServerIsBuzy, "系统异常");
        }
    }
}
