/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.service.service.fileserver;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.enums.ImageTypeEnum;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Random;

@Slf4j
public class FileUtils {

    public static final String IMAGE_TYPE_PREFIX = "data:image/png;base64,";

    private static final int randomStringLength = 10;

    private static final Random random = new Random();
    private static char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static File convertBase64ToFile(String base64Image, String filePath) {

        // 解码Base64字符串为字节数组
        String imageBase64 = removeBase64Prefix(base64Image);

        byte[] decodedBytes = Base64.getDecoder().decode(imageBase64);

        return convertByteToFile(decodedBytes, filePath);
    }

    public static File convertByteToFile(byte[] decodedBytes, String filePath) {
        // 创建文件对象
        File file = new File(filePath);
        // 创建文件目录
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }

        // 使用FileOutputStream将字节数组写入文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decodedBytes);
            return file;
        } catch (Throwable t) {
            log.error("convert base64 to file error , ", t);
        }
        return null;
    }


    public static String getFileName(ImageTypeEnum avatarType, String fileType) {

        // 创建一个字符数组，用于存放随机字符串
        char[] randomStringArray = new char[randomStringLength];

        // 生成随机字符串
        for (int i = 0; i < randomStringLength; i++) {
            randomStringArray[i] = characters[random.nextInt(characters.length)];
        }

        // 将字符数组转换为字符串
        String randomFileName = new String(randomStringArray);
        randomFileName = avatarType.getDesc() + "-" + randomFileName + "-" + System.currentTimeMillis();
        // 添加文件后缀.jpeg
        String fileName = randomFileName + "." + fileType;
        return fileName;
    }

    //去掉base64前面的图片标识，图片标识的格式是data:image/jpeg;base64, 其中jpeg可能是任何常见的图片类型
    public static String removeBase64Prefix(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return base64Image;
        }
        int commaIndex = base64Image.indexOf(",");
        if (commaIndex != -1) {
            return base64Image.substring(commaIndex + 1);
        }
        return base64Image;
    }


    public static boolean isBase64Type(String base64WithPrefix) {
        String base64 = removeBase64Prefix(base64WithPrefix);
        byte[] b = Base64.getDecoder().decode(base64);
        if (0x424D == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            // bmp
            return true;
        } else if (0x8950 == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            // png
            return true;
        } else if (0xFFD8 == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            // jpg
            return true;
        } else if (0x49492A00 == ((b[0] & 0xff) << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff))) {
            // tif
            return true;
        }
        return false;
    }

    public static boolean isPDF64Type(String base64WithPrefix) {
        String base64 = removeBase64Prefix(base64WithPrefix);
        byte[] b = Base64.getDecoder().decode(base64);
        // pdf
        return 0x25504446 == ((b[0] & 0xff) << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff));
    }

    public static String resizeImageBase64(String base64Original, int width, int height) {
        try {
            // 解码Base64字符串到字节数组
            base64Original = removeBase64Prefix(base64Original);
            byte[] originalBytes = Base64.getDecoder().decode(base64Original);
            // 将字节数组转换为 BufferedImage 对象
            ByteArrayInputStream inputStream = new ByteArrayInputStream(originalBytes);
            BufferedImage originalImage = ImageIO.read(inputStream);

            // 创建一个新的 BufferedImage 对象，具有指定的宽度和高度
            BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
            // 使用绘图对象来绘制调整大小的图像
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();

            // 将新的 BufferedImage 对象写入 ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", outputStream);

            // 将字节数组编码为 Base64 字符串
            byte[] resizedBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(resizedBytes);
        } catch (Throwable t) {
            log.error("resize image base64 error, ", t);
        }
        return null;
    }

    //获取文件类型，入参是一个图片的url，返回结果是String类型，图片类型从url中提取，url举例是https://test.com/1.png
    public static String getFileTypeFromUrl(String imageUrl) {
        String type = "";
        if (imageUrl == null || imageUrl.isEmpty()) {
            return type;
        }
        int lastDotIndex = imageUrl.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return type;
        }

        int queryIndex = imageUrl.indexOf('?', lastDotIndex);
        if (queryIndex == -1) {
            type = imageUrl.substring(lastDotIndex + 1);
        } else {
            type = imageUrl.substring(lastDotIndex + 1, queryIndex);
        }

        //兼容jpg到jpeg
        if ("jpg".equals(type)) {
            type = "jpeg";
        }
        return type;
    }

    //从base64中获取图片类型，入参是一个String，类似data:image/jpeg;base64,/9j/4QC8RXhpZgAASUkqAAgAAAAGABIBAwABAAAA，获取逗号前面的值，再提取jpeg
    public static String getImageTypeFromBase64(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return "";
        }
        int commaIndex = base64Image.indexOf(",");
        if (commaIndex == -1) {
            return "";
        }
        String prefix = base64Image.substring(0, commaIndex);
        int slashIndex = prefix.indexOf("/");
        int semicolonIndex = prefix.indexOf(";");
        if (slashIndex == -1 || semicolonIndex == -1) {
            return "";
        }
        return prefix.substring(slashIndex + 1, semicolonIndex);
    }

}
