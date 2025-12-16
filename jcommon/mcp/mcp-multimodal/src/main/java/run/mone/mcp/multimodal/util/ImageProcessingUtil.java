package run.mone.mcp.multimodal.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class ImageProcessingUtil {

    /**
     * 图片压缩阈值：1MB
     */
    private static final long COMPRESSION_THRESHOLD = 1024 * 1024;

    /**
     * 图片压缩结果，包含 Base64 和元信息
     */
    public static class CompressionResult {
        private final String base64;
        private final boolean compressed;
        private final String imageType;
        private final long originalSize;
        private final long resultSize;

        public CompressionResult(String base64, boolean compressed, String imageType, long originalSize, long resultSize) {
            this.base64 = base64;
            this.compressed = compressed;
            this.imageType = imageType;
            this.originalSize = originalSize;
            this.resultSize = resultSize;
        }

        public String getBase64() { return base64; }
        public boolean isCompressed() { return compressed; }
        public String getImageType() { return imageType; }
        public long getOriginalSize() { return originalSize; }
        public long getResultSize() { return resultSize; }
    }

    /**
     * 默认压缩比例：保持原尺寸（1.0），只做格式压缩
     * 如果需要降采样可以改为 0.5 或 0.7
     */
    private static final double DEFAULT_SCALE = 1.0;

    /**
     * JPEG 压缩质量（0.0-1.0）
     * 0.85 是高质量，文件较小
     * 0.90 是接近无损，文件稍大
     */
    private static final float JPEG_QUALITY = 0.85f;

    /**
     * Convert image file to base64 string with MIME type prefix
     *
     * @param imagePath Path to the image file
     * @return Base64 encoded string with MIME type prefix
     * @throws IOException If file cannot be read
     */
    public static String imageToBase64(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        String fileName = path.getFileName().toString().toLowerCase();
        String mimeType = getMimeType(fileName);
        
        byte[] fileContent = Files.readAllBytes(path);
        String base64Content = Base64.getEncoder().encodeToString(fileContent);
        
//        return "data:" + mimeType + ";base64," + base64Content;
        return base64Content;
    }
    
    /**
     * Determine MIME type based on file extension
     */
    private static String getMimeType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileName.endsWith(".tiff")) {
            return "image/tiff";
        } else if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "image/png"; // Default
        }
    }
    
    /**
     * Convert a base64 image string back to a BufferedImage
     *
     * @param base64Image Base64 encoded image string (with or without MIME type prefix)
     * @return BufferedImage object
     * @throws IOException If image cannot be decoded
     */
    public static BufferedImage base64ToImage(String base64Image) throws IOException {
        // Remove data:image/xxx;base64, prefix if present
        String base64Data = base64Image;
        if (base64Image.contains(",")) {
            base64Data = base64Image.split(",")[1];
        }
        
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }
    
    /**
     * Converts relative coordinates [0,1000] to absolute pixel coordinates
     *
     * @param relativeBox Relative coordinates [x1, y1, x2, y2] (range 0-1000)
     * @param imgSize Image dimensions [width, height]
     * @return Absolute coordinates [x1, y1, x2, y2] (in pixels)
     */
    public static int[] coordinatesConvert(int[] relativeBox, Dimension imgSize) {
        if (relativeBox == null || relativeBox.length != 4) {
            return null;
        }
        
        int imgWidth = imgSize.width;
        int imgHeight = imgSize.height;
        
        int[] absBox = new int[4];
        absBox[0] = (int) (relativeBox[0] * imgWidth / 1000.0);
        absBox[1] = (int) (relativeBox[1] * imgHeight / 1000.0);
        absBox[2] = (int) (relativeBox[2] * imgWidth / 1000.0);
        absBox[3] = (int) (relativeBox[3] * imgHeight / 1000.0);
        
        return absBox;
    }
    
    /**
     * Convert image coordinates to macOS screen coordinates
     * 
     * @param imageCoords Coordinates [x, y] in the image
     * @param imgSize Image dimensions [width, height]
     * @return Screen coordinates [x, y] for macOS
     */
    public static Point imageToScreenCoordinates(Point imageCoords, Dimension imgSize) {
        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Calculate the scale factor between the image and screen
        double scaleX = screenSize.getWidth() / imgSize.getWidth();
        double scaleY = screenSize.getHeight() / imgSize.getHeight();
        
        // Convert image coordinates to screen coordinates
        int screenX = (int) (imageCoords.x * scaleX);
        int screenY = (int) (imageCoords.y * scaleY);
        
        return new Point(screenX, screenY);
    }
    
    /**
     * Convert bounding box in image to corresponding screen coordinates
     * 
     * @param imageBox Bounding box in image coordinates [x1, y1, x2, y2]
     * @param imgSize Image dimensions [width, height]
     * @return Bounding box in screen coordinates [x1, y1, x2, y2]
     */
    public static int[] imageBoxToScreenCoordinates(int[] imageBox, Dimension imgSize) {
        if (imageBox == null || imageBox.length != 4) {
            return null;
        }
        
        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Calculate the scale factor between the image and screen
        double scaleX = screenSize.getWidth() / imgSize.getWidth();
        double scaleY = screenSize.getHeight() / imgSize.getHeight();
        
        // Convert image coordinates to screen coordinates
        int[] screenBox = new int[4];
        screenBox[0] = (int) (imageBox[0] * scaleX);
        screenBox[1] = (int) (imageBox[1] * scaleY);
        screenBox[2] = (int) (imageBox[2] * scaleX);
        screenBox[3] = (int) (imageBox[3] * scaleY);
        
        return screenBox;
    }
    
    /**
     * Draw boxes and arrows on an image
     *
     * @param image Original image to draw on
     * @param startBox Starting box coordinates [x1, y1, x2, y2] or null
     * @param endBox Ending box coordinates [x1, y1, x2, y2] or null
     * @param direction Direction string ("up", "down", "left", "right") or null
     * @return Image with drawn elements
     */
    public static BufferedImage drawBoxAndArrow(BufferedImage image, int[] startBox, int[] endBox, String direction) {
        // Create a copy of the image to draw on
        BufferedImage result = new BufferedImage(
                image.getWidth(), 
                image.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        
        // Define colors
        Color boxColor = Color.RED;
        Color arrowColor = Color.BLUE;
        int boxWidth = 5;
        int arrowLength = 150;
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(boxWidth));
        
        // Draw start box if provided
        if (startBox != null) {
            g2d.setColor(boxColor);
            g2d.drawRect(startBox[0], startBox[1], startBox[2] - startBox[0], startBox[3] - startBox[1]);
            
            // Calculate center of start box
            int startCenterX = (startBox[0] + startBox[2]) / 2;
            int startCenterY = (startBox[1] + startBox[3]) / 2;
            Point startCenter = new Point(startCenterX, startCenterY);
            
            if (endBox != null) {
                // Calculate center of end box
                int endCenterX = (endBox[0] + endBox[2]) / 2;
                int endCenterY = (endBox[1] + endBox[3]) / 2;
                Point endCenter = new Point(endCenterX, endCenterY);
                
                // Draw end box
                g2d.setColor(boxColor);
                g2d.drawRect(endBox[0], endBox[1], endBox[2] - endBox[0], endBox[3] - endBox[1]);
                
                // Draw line between boxes
                g2d.setColor(arrowColor);
                g2d.drawLine(startCenterX, startCenterY, endCenterX, endCenterY);
                
                // Draw arrow head
                drawArrowHead(g2d, startCenter, endCenter, arrowColor, boxWidth * 3);
            } else if (direction != null) {
                // Draw arrow in specified direction
                Point endPoint = calculateDragEndpoint(startCenter, direction, arrowLength);
                
                // Draw line
                g2d.setColor(arrowColor);
                g2d.drawLine(startCenterX, startCenterY, endPoint.x, endPoint.y);
                
                // Draw arrow head
                drawArrowHead(g2d, startCenter, endPoint, arrowColor, boxWidth * 3);
            }
        }
        
        g2d.dispose();
        return result;
    }
    
    /**
     * Draw an arrow head at the end point
     */
    private static void drawArrowHead(Graphics2D g2d, Point start, Point end, Color color, int size) {
        g2d.setColor(color);
        
        // Calculate angle
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        
        // Calculate points for arrow head
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        // Tip of the arrow
        xPoints[0] = end.x;
        yPoints[0] = end.y;
        
        // Left side of the arrow head
        xPoints[1] = (int) (end.x - size * Math.cos(angle + Math.PI / 6));
        yPoints[1] = (int) (end.y - size * Math.sin(angle + Math.PI / 6));
        
        // Right side of the arrow head
        xPoints[2] = (int) (end.x - size * Math.cos(angle - Math.PI / 6));
        yPoints[2] = (int) (end.y - size * Math.sin(angle - Math.PI / 6));
        
        // Draw the arrow head
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Calculate endpoint for drag operations based on direction
     */
    private static Point calculateDragEndpoint(Point startPoint, String direction, int length) {
        if (direction == null) {
            return startPoint;
        }
        
        switch (direction.toLowerCase()) {
            case "up":
                return new Point(startPoint.x, startPoint.y - length);
            case "down":
                return new Point(startPoint.x, startPoint.y + length);
            case "left":
                return new Point(startPoint.x - length, startPoint.y);
            case "right":
                return new Point(startPoint.x + length, startPoint.y);
            default:
                return startPoint;
        }
    }
    
    /**
     * Convert a BufferedImage to a base64 string
     */
    public static String imageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 将图片文件转换为 Base64，如果超过 1MB 则自动压缩
     * 返回包含 Base64 和元信息的结果对象
     * 压缩后的图片会异步保存到磁盘（用于校验）
     *
     * @param imagePath 图片文件路径
     * @return CompressionResult 包含 base64、是否压缩、图片类型等信息
     * @throws IOException 如果文件读取失败
     */
    public static CompressionResult compressAndEncode(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        File file = path.toFile();
        long fileSize = file.length();
        String originalType = getImageTypeFromPath(imagePath);

        // 如果文件小于 1MB，直接返回原图的 Base64
        if (fileSize <= COMPRESSION_THRESHOLD) {
            log.debug("图片大小 {}KB，无需压缩", fileSize / 1024);
            String base64 = imageToBase64(imagePath);
            return new CompressionResult(base64, false, originalType, fileSize, fileSize);
        }

        // 文件超过 1MB，进行压缩
        log.info("图片大小 {}KB 超过 1MB，进行 JPEG 压缩", fileSize / 1024);

        BufferedImage originalImage = ImageIO.read(file);
        if (originalImage == null) {
            throw new IOException("无法读取图片: " + imagePath);
        }

        // 压缩图片
        byte[] compressedBytes = compressImage(originalImage, DEFAULT_SCALE, JPEG_QUALITY);
        long resultSize = compressedBytes.length;

        log.info("压缩完成: {}KB -> {}KB (压缩率 {:.1f}%)",
                fileSize / 1024,
                resultSize / 1024,
                (1 - (double) resultSize / fileSize) * 100);

        // 异步保存压缩后的图片到磁盘（用于校验）
        saveCompressedImageAsync(imagePath, compressedBytes);

        String base64 = Base64.getEncoder().encodeToString(compressedBytes);
        return new CompressionResult(base64, true, "jpeg", fileSize, resultSize);
    }

    /**
     * 异步保存压缩后的图片到磁盘
     * 文件名格式：原文件名_compressed.jpg
     *
     * @param originalPath    原始图片路径
     * @param compressedBytes 压缩后的字节数组
     */
    private static void saveCompressedImageAsync(String originalPath, byte[] compressedBytes) {
        CompletableFuture.runAsync(() -> {
            try {
                // 生成压缩后的文件路径
                String compressedPath = generateCompressedFilePath(originalPath);

                // 保存到磁盘
                try (FileOutputStream fos = new FileOutputStream(compressedPath)) {
                    fos.write(compressedBytes);
                }

                log.info("压缩图片已保存: {}", compressedPath);
            } catch (Exception e) {
                log.warn("异步保存压缩图片失败: {}", e.getMessage());
            }
        });
    }

    /**
     * 生成压缩后的文件路径
     * 例如：/path/to/screenshot.png -> /path/to/screenshot_compressed.jpg
     *
     * @param originalPath 原始文件路径
     * @return 压缩后的文件路径
     */
    private static String generateCompressedFilePath(String originalPath) {
        Path path = Paths.get(originalPath);
        String fileName = path.getFileName().toString();

        // 移除原扩展名
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;

        // 添加 _compressed 后缀和 .jpg 扩展名
        String compressedFileName = baseName + "_compressed.jpg";

        // 返回完整路径
        Path parent = path.getParent();
        if (parent != null) {
            return parent.resolve(compressedFileName).toString();
        }
        return compressedFileName;
    }

    /**
     * 将图片文件转换为 Base64，如果超过 1MB 则自动压缩
     * 简化版本，只返回 Base64 字符串
     *
     * @param imagePath 图片文件路径
     * @return Base64 编码的字符串
     * @throws IOException 如果文件读取失败
     */
    public static String imageToBase64WithCompression(String imagePath) throws IOException {
        return compressAndEncode(imagePath).getBase64();
    }

    /**
     * 从文件路径获取图片类型
     */
    private static String getImageTypeFromPath(String imagePath) {
        String lower = imagePath.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "jpeg";
        } else if (lower.endsWith(".gif")) {
            return "gif";
        } else if (lower.endsWith(".webp")) {
            return "webp";
        }
        return "png";
    }

    /**
     * 压缩 BufferedImage
     *
     * @param image       原始图片
     * @param scale       缩放比例 (0.0-1.0)
     * @param jpegQuality JPEG 压缩质量 (0.0-1.0)
     * @return 压缩后的字节数组
     * @throws IOException 如果压缩失败
     */
    public static byte[] compressImage(BufferedImage image, double scale, float jpegQuality) throws IOException {
        // 1. 计算新尺寸
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        // 2. 创建缩放后的图片（使用 RGB 类型，JPEG 不支持透明通道）
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // 设置高质量缩放
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充白色背景（处理 PNG 透明背景）
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // 绘制缩放后的图片
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 3. JPEG 压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("没有可用的 JPEG 编码器");
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(jpegQuality);

            writer.write(null, new IIOImage(resizedImage, null, null), param);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }

    /**
     * 压缩 BufferedImage 并返回 Base64
     *
     * @param image       原始图片
     * @param scale       缩放比例 (0.0-1.0)
     * @param jpegQuality JPEG 压缩质量 (0.0-1.0)
     * @return Base64 编码的字符串
     * @throws IOException 如果压缩失败
     */
    public static String compressImageToBase64(BufferedImage image, double scale, float jpegQuality) throws IOException {
        byte[] compressedBytes = compressImage(image, scale, jpegQuality);
        return Base64.getEncoder().encodeToString(compressedBytes);
    }

    /**
     * 根据文件大小判断是否需要压缩，并返回适当的 Base64
     *
     * @param image    BufferedImage 对象
     * @param fileSize 原始文件大小（字节）
     * @return Base64 编码的字符串
     * @throws IOException 如果处理失败
     */
    public static String imageToBase64WithCompression(BufferedImage image, long fileSize) throws IOException {
        // 估算：如果原始文件超过 1MB，进行压缩
        if (fileSize > COMPRESSION_THRESHOLD) {
            log.info("图片大小 {}KB 超过 1MB，进行压缩", fileSize / 1024);
            return compressImageToBase64(image, DEFAULT_SCALE, JPEG_QUALITY);
        }

        // 否则直接转换
        return imageToBase64(image, "png");
    }
} 