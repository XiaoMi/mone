package run.mone.mcp.multimodal.android;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Android 设备连接和截图示例
 *
 * 使用前请确保：
 * 1. 已安装 Android SDK 并配置 ANDROID_HOME 环境变量
 * 2. 目标设备已开启 USB 调试或无线调试
 * 3. 对于无线调试，需要先通过 USB 连接并执行 adb tcpip 5555
 */
public class AndroidExample {

    public static void main(String[] args) {
        // 远程设备地址（根据实际情况修改）
        String deviceHost = "10.220.151.48";
        int devicePort = 33793;

        // 如果命令行传入参数
        if (args.length >= 1) {
            deviceHost = args[0];
        }
        if (args.length >= 2) {
            devicePort = Integer.parseInt(args[1]);
        }

        System.out.println("=== Android 设备连接和截图示例 ===\n");

        AndroidExample example = new AndroidExample();
        example.run(deviceHost, devicePort);
    }

    public void run(String host, int port) {
        AndroidDebugBridge bridge = null;

        try {
            // 1. 初始化 ADB
            System.out.println("1. 初始化 Android Debug Bridge...");
            AndroidDebugBridge.init(false);

            // 2. 查找 adb 路径
            String adbPath = findAdbPath();
            if (adbPath == null) {
                System.err.println("错误: 无法找到 adb，请确保 ANDROID_HOME 已设置");
                return;
            }
            System.out.println("   ADB 路径: " + adbPath);

            // 3. 创建 Bridge
            bridge = AndroidDebugBridge.createBridge(adbPath, false);
            waitForDeviceList(bridge);

            // 4. 连接远程设备
            String address = host + ":" + port;
            System.out.println("\n2. 连接到远程设备: " + address);

            ProcessBuilder pb = new ProcessBuilder(adbPath, "connect", address);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   " + line);
            }
            process.waitFor();

            // 等待设备连接
            Thread.sleep(2000);

            // 5. 获取设备
            System.out.println("\n3. 获取已连接的设备...");
            IDevice[] devices = bridge.getDevices();
            System.out.println("   找到 " + devices.length + " 个设备");

            IDevice targetDevice = null;
            for (IDevice device : devices) {
                System.out.println("   - " + device.getSerialNumber() +
                    " [" + device.getState() + "]" +
                    (device.isOnline() ? " (在线)" : " (离线)"));

                if (device.isOnline() && targetDevice == null) {
                    targetDevice = device;
                }
            }

            if (targetDevice == null) {
                System.err.println("\n错误: 没有找到在线的设备");
                System.out.println("\n提示: 请确保设备已开启无线调试:");
                System.out.println("   1. USB 连接设备");
                System.out.println("   2. 执行: adb tcpip 5555");
                System.out.println("   3. 断开 USB，获取设备 IP");
                System.out.println("   4. 运行此程序: java AndroidExample <设备IP> 5555");
                return;
            }

            System.out.println("\n   使用设备: " + targetDevice.getSerialNumber());

            // 6. 获取设备信息
            System.out.println("\n4. 获取设备信息...");
            printDeviceInfo(targetDevice);

            // 7. 截图
            System.out.println("\n5. 截取屏幕...");
            String screenshotPath = takeScreenshot(targetDevice);

            if (screenshotPath != null) {
                System.out.println("   截图保存成功: " + screenshotPath);
            } else {
                System.err.println("   截图失败");
            }

            System.out.println("\n=== 完成 ===");

        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理
            if (bridge != null) {
                AndroidDebugBridge.disconnectBridge();
                AndroidDebugBridge.terminate();
            }
        }
    }

    /**
     * 查找 adb 路径
     */
    private String findAdbPath() {
        // 检查 ANDROID_HOME
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            String adbPath = androidHome + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 检查 ANDROID_SDK_ROOT
        String androidSdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (androidSdkRoot != null) {
            String adbPath = androidSdkRoot + "/platform-tools/adb";
            if (new File(adbPath).exists()) {
                return adbPath;
            }
        }

        // 常见路径
        String[] commonPaths = {
            "/usr/local/bin/adb",
            System.getProperty("user.home") + "/Library/Android/sdk/platform-tools/adb",
            System.getProperty("user.home") + "/Android/Sdk/platform-tools/adb"
        };

        for (String path : commonPaths) {
            if (new File(path).exists()) {
                return path;
            }
        }

        return null;
    }

    /**
     * 等待设备列表加载
     */
    private void waitForDeviceList(AndroidDebugBridge bridge) {
        int timeout = 5000;
        int waited = 0;
        while (!bridge.hasInitialDeviceList() && waited < timeout) {
            try {
                Thread.sleep(100);
                waited += 100;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * 打印设备信息
     */
    private void printDeviceInfo(IDevice device) {
        try {
            // 获取屏幕尺寸
            ShellOutputReceiver sizeReceiver = new ShellOutputReceiver();
            device.executeShellCommand("wm size", sizeReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   屏幕尺寸: " + sizeReceiver.getOutput().trim());

            // 获取 Android 版本
            ShellOutputReceiver versionReceiver = new ShellOutputReceiver();
            device.executeShellCommand("getprop ro.build.version.release", versionReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   Android 版本: " + versionReceiver.getOutput().trim());

            // 获取设备型号
            ShellOutputReceiver modelReceiver = new ShellOutputReceiver();
            device.executeShellCommand("getprop ro.product.model", modelReceiver, 5000, TimeUnit.MILLISECONDS);
            System.out.println("   设备型号: " + modelReceiver.getOutput().trim());

        } catch (Exception e) {
            System.err.println("   获取设备信息失败: " + e.getMessage());
        }
    }

    /**
     * 截取屏幕
     */
    private String takeScreenshot(IDevice device) {
        try {
            // 使用 ddmlib 获取原始图像
            RawImage rawImage = device.getScreenshot();
            if (rawImage == null) {
                System.err.println("   无法获取屏幕数据");
                return null;
            }

            System.out.println("   屏幕分辨率: " + rawImage.width + " x " + rawImage.height);
            System.out.println("   色彩深度: " + rawImage.bpp + " bpp");

            // 转换为 BufferedImage
            BufferedImage image = new BufferedImage(
                rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB);

            int index = 0;
            int bytesPerPixel = rawImage.bpp >> 3;
            for (int y = 0; y < rawImage.height; y++) {
                for (int x = 0; x < rawImage.width; x++) {
                    int value = rawImage.getARGB(index);
                    image.setRGB(x, y, value);
                    index += bytesPerPixel;
                }
            }

            // 生成文件名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = "android_screenshot_" + timestamp + "_" +
                             UUID.randomUUID().toString().substring(0, 8) + ".png";

            // 保存到用户目录的 Screenshots 文件夹
            String userHome = System.getProperty("user.home");
            File screenshotDir = new File(userHome, "Screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File outputFile = new File(screenshotDir, fileName);
            ImageIO.write(image, "png", outputFile);

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            System.err.println("   截图异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Shell 输出接收器
     */
    private static class ShellOutputReceiver implements IShellOutputReceiver {
        private final StringBuilder output = new StringBuilder();

        @Override
        public void addOutput(byte[] data, int offset, int length) {
            output.append(new String(data, offset, length));
        }

        @Override
        public void flush() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getOutput() {
            return output.toString();
        }
    }
}