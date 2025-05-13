package run.mone.mcp.multimodal.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageProcessingUtil {

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
} 