package run.mone.mcp.word.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.beetl.ext.fn.StringUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class WordService {
    public String createWord(
            String filePath,
            String fileName,
            String textTitle
    ) throws Exception {
        // 验证输入数据
        log.info("Create Word");

        if (StringUtils.isBlank(fileName)) {
            fileName = "default_name";
        }

        if (StringUtils.isBlank(textTitle)) {
            textTitle = "default_title";
        }
        // 确保文件路径存在
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 构造完整的文件路径
        String fullPath = filePath + File.separator + fileName + ".docx";

        try (XWPFDocument document = new XWPFDocument()) {
            //将textTitle作为标题，居中显示
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(textTitle);

            // 保存文件
            try (FileOutputStream fileOut = new FileOutputStream(fullPath)) {
                document.write(fileOut);
            }

            return "Successfully created Word document at " + fullPath;
        } catch (Exception e) {
            log.error("Error creating Word document", e);
            throw e;
        }
    }


    /**
     * 统计word文档中的总字符数，以及字母、数字和其他字符的数量
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public String countWords(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            int totalChars = 0;
            int letterCount = 0;
            int digitCount = 0;
            int otherCount = 0;

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                totalChars += text.length();
                for (char c : text.toCharArray()) {
                    if (Character.isLetter(c)) {
                        letterCount++;
                    } else if (Character.isDigit(c)) {
                        digitCount++;
                    } else {
                        otherCount++;
                    }
                }
            }

            return String.format("Total characters: %d, Letters: %d, Digits: %d, Others: %d", totalChars, letterCount, digitCount, otherCount);
        } catch (Exception e) {
            log.error("Error counting words in Word document: " + filePath, e);
            throw new Exception("Failed to count words in Word document", e);
        }


    }
    /**
     * 在word文档中输入文本
     *
     * @param filePath
     * @param text
     * @return
     * @throws Exception
     */
    public String inputText(String filePath, String text) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                document.write(fileOut);
            }

            return "Successfully inputted text into Word document at " + filePath;
        } catch (Exception e) {
            log.error("Error inputting text into Word document: " + filePath, e);
            throw new Exception("Failed to input text into Word document", e);
        }
    }


    /**
     * 清空word文档中的文本
     * @param filePath 文档的路径
     * @return 操作结果信息
     * @throws Exception 如果发生错误
     */
    public String deleteText(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setText("", 0);
                }
            }
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                document.write(fileOut);
            }
            return "Successfully deleted all text from Word document at " + filePath;
        } catch (Exception e) {
            log.error("Error deleting text from Word document: " + filePath, e);
            throw new Exception("Failed to delete text from Word document", e);
        }
    }
}