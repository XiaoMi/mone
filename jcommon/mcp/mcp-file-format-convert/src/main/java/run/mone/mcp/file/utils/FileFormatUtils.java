package run.mone.mcp.file.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Slf4j
public class FileFormatUtils {

    @SneakyThrows
    public static Boolean convertWordToPdf2(String inputFilePath, String outputFilePath) {
        // 处理输出路径
        if(StringUtils.isEmpty(outputFilePath)) {
            File inputFile = new File(inputFilePath);
            String parentPath = inputFile.getParent();
            String fileName = inputFile.getName().replaceFirst("[.][^.]+$", "");
            outputFilePath = parentPath + File.separator + fileName + ".pdf";
        }

        try (FileInputStream fis = new FileInputStream(inputFilePath);

             XWPFDocument document = new XWPFDocument(fis);
             FileOutputStream fos = new FileOutputStream(outputFilePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document pdfDocument = new Document(pdfDoc)) {

            // 遍历 Word 文档的段落
            for (XWPFParagraph para : document.getParagraphs()) {
                String text = para.getText();
                // 将段落文本添加到 PDF 文档中
                pdfDocument.add(new Paragraph(text));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    public static Boolean convertWordToPdf(String inputFilePath, String outputFilePath) {
        // 处理输出路径
        if(StringUtils.isEmpty(outputFilePath)) {
            File inputFile = new File(inputFilePath);
            String parentPath = inputFile.getParent();
            String fileName = inputFile.getName().replaceFirst("[.][^.]+$", "");
            outputFilePath = parentPath + File.separator + fileName + ".pdf";
        }

        // 创建 Document 对象
        com.spire.doc.Document document = new com.spire.doc.Document();
        try {
            // 加载 Word 文档
            document.loadFromFile(inputFilePath);
            // 保存为 PDF 文件
            document.saveToFile(outputFilePath, com.spire.doc.FileFormat.PDF);
            log.info("Word 文件已成功转换为 PDF 文件。");
        } catch (Exception e) {
            return false;
        } finally {
            // 关闭文档
            document.close();
        }
        return true;
    }

    @SneakyThrows
    public static Boolean convertPdfToWord(String inputFilePath, String outputFilePath) {
        // 处理输出路径
        if(StringUtils.isEmpty(outputFilePath)) {
            File inputFile = new File(inputFilePath);
            String parentPath = inputFile.getParent();
            String fileName = inputFile.getName().replaceFirst("[.][^.]+$", "");
            outputFilePath = parentPath + File.separator + fileName + ".pdf";
        }
        try (PDDocument pdfDocument = PDDocument.load(new File(inputFilePath));
             XWPFDocument wordDocument = new XWPFDocument()) {

            // 提取 PDF 文本
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String text = pdfTextStripper.getText(pdfDocument);

            // 创建 Word 段落并添加文本
            XWPFParagraph paragraph = wordDocument.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);

            // 保存 Word 文档
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                wordDocument.write(out);
            }
            log.info("PDF 文件已成功转换为 Word 文件。");
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
