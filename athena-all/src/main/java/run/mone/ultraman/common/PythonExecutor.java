package run.mone.ultraman.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author goodjava@qq.com
 * @date 2023/4/18 14:18
 */
public class PythonExecutor {

    private static boolean open = false;


    public static void run(String path, String param) {
        if (!open) {
            return;
        }
        System.out.println("param:" + param);
        // 请将此路径替换为你的Python脚本路径
        String pythonScriptPath = path;
        // 构建ProcessBuilder，以运行Python脚本
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sh", pythonScriptPath, param);
            // 启动进程
            Process process = processBuilder.start();
            // 获取进程的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // 读取并打印进程的输出
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Python script finished with exit code: " + exitCode);
        } catch (IOException e) {
            System.out.println("Error executing Python script: " + e.getMessage());
        } catch (
                InterruptedException e) {
            System.out.println("Process interrupted: " + e.getMessage());
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
    }


}
