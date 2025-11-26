package run.mone.excel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/1/8
 */
public class CsvExcelConverter {
    //csv转excel
    public static void convertCsvToExcel(String csvFilePath, String excelFilePath) throws IOException {
        try (
                InputStream csvInputStream = new FileInputStream(csvFilePath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvInputStream));
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream excelOutputStream = new FileOutputStream(excelFilePath)
        ) {
            String line;
            Sheet sheet = workbook.createSheet("Sheet1");
            int rowNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                Row row = sheet.createRow(rowNumber++);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }
            workbook.write(excelOutputStream);
        }
    }

    //excel转csv
    public static void convertExcelToCsv(String excelFilePath, String csvFilePath) throws IOException, InvalidFormatException {
        try (Workbook workbook = WorkbookFactory.create(new File(excelFilePath));
             CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFilePath), CSVFormat.DEFAULT)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> csvValues = new ArrayList<>();
                row.forEach(cell -> {
                    String text = new DataFormatter().formatCellValue(cell);
                    csvValues.add(text);
                });
                csvPrinter.printRecord(csvValues);
            }
        }
    }

    //给定一个csv文件，将所有所有列名a=x的行的列名b的值修改为y
    public static void updateCsvColumnWhereAnotherColumnEquals(String csvFilePath, String updatedCsvFilePath, String targetColumnName, String conditionColumnName, String conditionValue, String newValue) throws IOException {
        try (
                Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
                Writer writer = Files.newBufferedWriter(Paths.get(updatedCsvFilePath));
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(parser.getHeaderMap().keySet().toArray(new String[0])))
        ) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            Integer targetColumnIndex = headerMap.get(targetColumnName);
            Integer conditionColumnIndex = headerMap.get(conditionColumnName);

            if (targetColumnIndex == null || conditionColumnIndex == null) {
                throw new IllegalArgumentException("Column name not found in the CSV file");
            }

            // Iterate through records and update the target column where condition matches
            for (CSVRecord record : parser) {
                List<String> updatedRecord = new ArrayList<>();
                for (String value : record) {
                    updatedRecord.add(value);
                }

                if (record.get(conditionColumnIndex).equals(conditionValue)) {
                    updatedRecord.set(targetColumnIndex, newValue);
                }

                printer.printRecord(updatedRecord);
            }
        }
    }

}
