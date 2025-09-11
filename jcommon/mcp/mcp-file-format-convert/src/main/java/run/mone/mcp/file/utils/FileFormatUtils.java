package run.mone.mcp.file.utils;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;


import java.io.*;
import java.util.List;


@Slf4j
public class FileFormatUtils {


    //将excel文件转化为csv文件，输入为输入和输出路径
    @SneakyThrows
    public static Boolean excelToCsv(String inputFilePath, String outputFilePath) {
        try (InputStream is = new FileInputStream(inputFilePath);
             Workbook workbook = WorkbookFactory.create(is);
             // 写入 UTF-8 BOM 头防止中文乱码
             OutputStream fos = new FileOutputStream(outputFilePath);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
             BufferedWriter writer = new BufferedWriter(osw)) {

            // 写入 UTF-8 BOM 头
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0); // 默认处理第一个工作表

            for (Row row : sheet) {
                StringBuilder sb = new StringBuilder();
                int lastCellNum = row.getLastCellNum();

                for (int cn = 0; cn < lastCellNum; cn++) {
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = formatter.formatCellValue(cell);

                    // 处理 CSV 特殊字符转义
                    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                        value = value.replace("\"", "\"\"");
                        sb.append("\"").append(value).append("\"");
                    } else {
                        sb.append(value);
                    }

                    if (cn < lastCellNum - 1) {
                        sb.append(",");
                    }
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }



    @SneakyThrows
    public static Boolean csvToExcel(String inputFilePath, String outputFilePath) {
        try (CSVReader reader = new CSVReader(new FileReader(inputFilePath));
             Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {

            Sheet sheet = workbook.createSheet("Sheet1");
            List<String[]> allData = reader.readAll();
            for (int i = 0; i < allData.size(); i++) {
                String[] rowData = allData.get(i);
                Row row = sheet.createRow(i);
                for (int j = 0; j < rowData.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData[j]);
                }
            }

            for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
