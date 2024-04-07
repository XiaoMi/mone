package run.mone.local.docean.service.tool;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.dto.ExecCommandResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TerminalExecutorService implements ToolService {

    public Result<ExecCommandResult> execCommand(String command, String workPath) {

        ExecCommandResult execCommandResult = new ExecCommandResult();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            // 设置命令和参数
            builder.command("bash", "-c", command);

            // 设置工作目录（可选）
            if (StringUtils.isNotBlank(workPath)) {
                File workPathDir = new File(workPath);
                if (workPathDir.isDirectory()) {
                    builder.directory(workPathDir);
                } else {
                    builder.directory();
                }

            }

            // 启动进程
            Process process = builder.start();

            // 读取命令执行结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> resultData = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                resultData.add(line);
            }

            log.info("execCommand result data：{}", new Gson().toJson(resultData));

            // 等待命令执行完成
            int exitCode = process.waitFor();
            execCommandResult.setResultCode(exitCode);
            execCommandResult.setResultData(resultData);

            log.info("execCommand command ：{} , workPath : {} , exitCode : {}, resultData : {}", command, workPath, exitCode, resultData);
            return Result.success(execCommandResult);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail(GeneralCodes.ServerIsBuzy, "系统异常");
        }
    }

}
