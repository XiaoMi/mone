package run.mone.ultraman;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2023/6/7 18:20
 */
public class DemoService {


    //ai:截取当前桌面,保存到指定文件(filePath参数),如果截取成功,则返回"ok"
    public String captureDesktop(String filePath) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage image = robot.createScreenCapture(screenRect);
            File file = new File(filePath);
            ImageIO.write(image, "png", file);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    //ai:根据提供的的文件地址,读取文件内容,用String返回
    public String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }



}
