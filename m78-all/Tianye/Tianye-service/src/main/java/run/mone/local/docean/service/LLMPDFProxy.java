package run.mone.local.docean.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.CollectionUtils;

import run.mone.local.docean.fsm.flow.LLMFlow;
import run.mone.local.docean.service.dto.VisionContent;
import run.mone.local.docean.service.dto.VisionMsg;
import run.mone.local.docean.service.dto.VisionReq;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.PDFUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.nio.file.Files;

/**
 * @author zhangxiaowei6
 * @Date 2024/12/5 17:12
 */

@Slf4j
public class LLMPDFProxy {
    private LLMFlow llmFlow;

    public LLMPDFProxy(LLMFlow llmFlow) {
        this.llmFlow = llmFlow;
    }

    // 计算不会太精准，近似128k
    private static final int MAX_TOKEN = 128 * 850;

    public String pdfProxy(String prompt, String model, String rstFormatDefinition, boolean generateCode) {
        if (isLLMPDFUnderstand()) {
            PDFService pdfService = Ioc.ins().getBean(PDFService.class);
            VisionReq visionReq = getPDFVisionReq(prompt, model, rstFormatDefinition, generateCode);
            log.info("pdfProxy.visionReq:{}", visionReq);
            return pdfService.PDFUnderstand(visionReq, llmFlow.getTimeout());
        }
        // todo
        return "";
    }

    public Boolean isLLMPDFUnderstand() {
        return llmFlow.getValueFromInputMapWithDefault(CommonConstants.TY_LLM_PDF_UNDERSTAND_MARK, false,
                Boolean.class);
    }

    public VisionReq getPDFVisionReq(String prompt, String model, String rstFormatDefinition, boolean generateCode) {
        List<VisionContent> contents = new ArrayList<>();
        llmFlow.getInputMap().forEach((key, value) -> {
            String url = value.getValue().getAsString();
            PDFUtils.PDFData imageData = PDFUtils.downloadPDFAsBase64(url);
            // 下载之后，转换格式再发给z
            try {
                List<PageContent> mapList = extractContent(imageData.getBase64String());
                // 遍历mapList，将每个map转换为VisionContent
                for (PageContent page : mapList) {
                    VisionContent pdfContent = VisionContent.builder().type("text").text(page.getText()).build();
                    contents.add(pdfContent);
                    log.info("getPDFVisionReq VisionContent found: key={}, value={}", key, value);
                    // 图片不为空，遍历图片
                    if (!page.imageContexts.isEmpty()) {
                        for (ImageContext imageContext : page.imageContexts) {
                            VisionContent pdfImgContent = VisionContent.builder().type("image").source(
                                            ImmutableMap.of(
                                                    "type", "base64",
                                                    "media_type", "image/png",
                                                    "data", imageContext.getBase64Content()))
                                    .build();
                            contents.add(pdfImgContent);
                            log.info("getPDFVisionReq VisionContent found: key={}, value={}", key, value);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("LLMPDFProxy.getPDFVisionReq error:{}", e);
            }
        });
        VisionReq req = VisionReq.builder()
                .model(model)
                .cmd("PDFUnderstanding")
                .temperature(llmFlow.getTemperature())
                .promptName(generateCode ? "yingjie" : "json4")
                .params(ImmutableMap.of("input", prompt, "rst_format_definition", rstFormatDefinition))
                .zzToken(llmFlow.getToken())
                .msgs(Lists.newArrayList(VisionMsg.builder().role("user").content(contents).build()))
                .build();
        return req;
    }

    public List<PageContent> extractContent(byte[] pdfBytes) throws IOException {
        List<PageContent> contents = new ArrayList<>();
        PDDocument document = null;
        PDFTextStripper stripper = null;
        PDFRenderer renderer = null;

        try {
            document = PDDocument.load(new ByteArrayInputStream(pdfBytes));
            stripper = new PDFTextStripper();
            renderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PageContent pageContent = new PageContent();
                pageContent.setPageNumber(i + 1);

                // Extract text
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document);
                pageContent.setText(text);

                // Extract images only if they exist on the page
                List<ImageContext> imageContexts = new ArrayList<>();
                // Assuming a method extractImagesFromPage exists to handle image extraction
                List<BufferedImage> images = extractImagesFromPage(document, i);
                for (BufferedImage image : images) {
                    ImageContext imageContext = new ImageContext();
                    // 图片裁剪
                    BufferedImage optimizedImage = optimizeImage(image);
                    // Convert the optimized image to JPEG and set it in the ImageContext
                    String base64Content = imageToBase64(optimizedImage);
                    imageContext.setBase64Content(base64Content);
                    imageContext.setImage(optimizedImage);

                    // Set beforeText and afterText based on the context
                    imageContext.setBeforeText(""); // Implement logic to find text before the image
                    imageContext.setAfterText(""); // Implement logic to find text after the image
                    imageContexts.add(imageContext);
                }
                pageContent.setImageContexts(imageContexts);

                contents.add(pageContent);
            }
            return contents;//calculatePDFTokens(contents);
        } finally {
            // 关闭所有资源
            if (document != null) {
                document.close();
            }
        }
    }

    private List<BufferedImage> extractImagesFromPage(PDDocument document, int pageIndex) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        PDPage page = document.getPage(pageIndex);
        PDResources resources = page.getResources();

        for (COSName xObjectName : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(xObjectName);

            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;

                // 获取原始图片格式
                String imageFormat = image.getSuffix();
                BufferedImage bImage = image.getImage();

                // 处理色彩空间转换
                BufferedImage convertedImage = new BufferedImage(
                        bImage.getWidth(),
                        bImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                // 使用更好的颜色渲染
                Graphics2D g2d = convertedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2d.drawImage(bImage, 0, 0, null);
                g2d.dispose();

                if (isValidImage(convertedImage)) {
                    images.add(convertedImage);
                }
            }
        }
        return images;
    }

    private boolean isValidImage(BufferedImage image) {
        // 增强图片验证逻辑
        int minWidth = 120;
        int minHeight = 120;
        int minPixels = 14400; // 120x120

        if (image == null) {
            return false;
        }

        // 基本尺寸检查
        if (image.getWidth() < minWidth || image.getHeight() < minHeight) {
            return false;
        }

        // 检查总像素数
        if (image.getWidth() * image.getHeight() < minPixels) {
            return false;
        }

        // 检查图片是否全白或全黑
        boolean isBlankImage = isBlankImage(image);
        if (isBlankImage) {
            return false;
        }

        return true;
    }

    private boolean isBlankImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int samplingStep = 10; // 每10个像素采样一次

        int whiteCount = 0;
        int blackCount = 0;
        int totalSamples = 0;

        for (int x = 0; x < width; x += samplingStep) {
            for (int y = 0; y < height; y += samplingStep) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // 检查是否接近白色或黑色
                if (red > 250 && green > 250 && blue > 250) {
                    whiteCount++;
                } else if (red < 5 && green < 5 && blue < 5) {
                    blackCount++;
                }
                totalSamples++;
            }
        }

        // 如果90%以上的采样点都是白色或黑色，认为是空白图片
        if (whiteCount > totalSamples * 0.9 || blackCount > totalSamples * 0.9) {
            return true;
        }
        return false;
    }

    public List<PageContent> calculatePDFTokens(List<PageContent> contents) throws IOException {
        List<PageContent> newContents = new ArrayList<>();
        int totalTokens = 0;
        // 构建包含图文的消息内容
        for (LLMPDFProxy.PageContent page : contents) {
            // 文本token计算
            int textTokens = SimpleTokenCounter.calculateTokens(page.getText());
            totalTokens += textTokens;
            if (totalTokens > MAX_TOKEN) {
                log.warn("calculatePDFTokens text request token exceed! {}", totalTokens);
                break;
            }

            // 图片token计算
            if (!CollectionUtils.isEmpty(page.getImageContexts())) {
                for (LLMPDFProxy.ImageContext imageContext : page.getImageContexts()) {
                    String base64Image = imageContext.getBase64Content();
                    int imageTokens = SimpleTokenCounter.getImageTokens(base64Image);
                    totalTokens += imageTokens;
                    if (totalTokens > MAX_TOKEN) {
                        log.warn("request token exceed! {}", totalTokens);
                        break;
                    }
                }
            }
            newContents.add(page);
        }
        return newContents;
    }

    /**
     * 将BufferedImage转换为Base64字符串
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        // 首先优化图片大小
        BufferedImage optimizedImage = optimizeImage(image);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 将图片写入字节数输出流
            ImageIO.write(optimizedImage, "PNG", outputStream);
            // 转换为Base64字符串
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("LLM PDF PROXY imageToBase64 error :{}", e);
            return null;
        } finally {
            outputStream.close();
        }
    }

    /**
     * 优化图片大小
     */
    private BufferedImage optimizeImage(BufferedImage original) {
        Graphics2D g = null;
        try {
            // 如果图片太大，进行压缩
            if (original.getWidth() > 2000 || original.getHeight() > 2000) {
                double scale = Math.min(2000.0 / original.getWidth(),
                        2000.0 / original.getHeight());
                int newWidth = (int) (original.getWidth() * scale);
                int newHeight = (int) (original.getHeight() * scale);

                BufferedImage resized = new BufferedImage(newWidth, newHeight,
                        original.getType());
                g = resized.createGraphics();
                try {
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(original, 0, 0, newWidth, newHeight, null);
                    return resized;
                } finally {
                    g.dispose();
                }
            }
            return original;
        } finally {
            if (g != null) {
                g.dispose(); // 确保释放Graphics2D资源
            }
        }
    }

    private static final int DPI = 300; // 图片提取的DPI值

    @Data
    public static class PageContent {
        private String text;
        private BufferedImage image;
        private int pageNumber;
        private List<ImageContext> imageContexts;
    }

    @Data
    public static class ImageContext {
        private BufferedImage image;
        private String beforeText;
        private String afterText;
        private String base64Content;
    }

    public static void main(String[] args) {
        String path = "/Users/wodiwudi/Downloads/客服IM系统概要设计V2.pdf";
        File file = new File(path);
        byte[] pdfBytes;
        try {
            pdfBytes = Files.readAllBytes(file.toPath());
            try {
                List<PageContent> pageContents = new LLMPDFProxy(null).extractContent(pdfBytes);
                log.info("contentParts:{}", pageContents);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
