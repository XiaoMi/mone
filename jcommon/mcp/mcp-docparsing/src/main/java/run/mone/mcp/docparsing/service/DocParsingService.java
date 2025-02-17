package run.mone.mcp.docparsing.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import run.mone.mcp.docparsing.model.DocParsingResult;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
@Service
public class DocParsingService {

    public DocParsingResult parsePdf(String path) {
        try (PDDocument document = PDDocument.load(new File(path))) {
            // 检查文档是否成功加载
            if (document == null) {
                log.error("Failed to load PDF: {}", path);
                return DocParsingResult.failure("Error:Failed to load PDF");
            }

            // 检查文档是否加密
            if (document.isEncrypted()) {
                log.warn("PDF document is encrypted: {}", path);
                return DocParsingResult.failure("WARNING: PDF document is encrypted");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(document);
            return DocParsingResult.success(content);
        } catch (Exception e) {
            log.error("Failed to parse PDF: {}", path, e);
            return DocParsingResult.failure("Error:" + e.getMessage());
        }
    }

    public DocParsingResult parseDoc(String path) {
        try (FileInputStream fis = new FileInputStream(new File(path));
             HWPFDocument doc = new HWPFDocument(fis)) {
            WordExtractor extractor = new WordExtractor(doc);
            String content = extractor.getText();
            return DocParsingResult.success(content);
        } catch (Exception e) {
            log.error("Failed to parse DOC: {}", path, e);
            return DocParsingResult.failure(e.getMessage());
        }
    }

    public DocParsingResult parseDocx(String path) {
        try (FileInputStream fis = new FileInputStream(new File(path));
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            String content = extractor.getText();
            return DocParsingResult.success(content);
        } catch (Exception e) {
            log.error("Failed to parse DOCX: {}", path, e);
            return DocParsingResult.failure(e.getMessage());
        }
    }
}