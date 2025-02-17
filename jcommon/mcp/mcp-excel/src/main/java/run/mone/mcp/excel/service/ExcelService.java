package run.mone.mcp.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;

@Slf4j
@Service
public class ExcelService {

    public String createBarChart(
            String filePath,
            String sheetName,
            String chartTitle,
            String categoryAxisTitle,
            String valueAxisTitle,
            List<String> categories,
            List<Number> values
    ) throws Exception {
        // 验证输入数据
        if (categories == null || values == null) {
            throw new IllegalArgumentException("categories and values cannot be null");
        }
        if (categories.size() != values.size()) {
            throw new IllegalArgumentException("categories and values must have the same size");
        }
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("categories and values cannot be empty");
        }

        if(StringUtils.isEmpty(sheetName)){
            sheetName = "default";
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // 创建工作表
            XSSFSheet sheet = workbook.createSheet(sheetName);

            // 写入数据
            int rowCount = 0;
            Row headerRow = sheet.createRow(rowCount++);
            headerRow.createCell(0).setCellValue("Category");
            headerRow.createCell(1).setCellValue("Value");

            for (int i = 0; i < categories.size(); i++) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(categories.get(i));
                row.createCell(1).setCellValue(values.get(i).doubleValue());
            }

            // 创建图表
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 4, 0, 15, 20);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText(chartTitle);
            chart.setTitleOverlay(false);

            // 创建轴
            XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            categoryAxis.setTitle(categoryAxisTitle);
            XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
            valueAxis.setTitle(valueAxisTitle);

            // 设置数据范围
            XDDFDataSource<String> categoriesData = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                    new CellRangeAddress(1, categories.size(), 0, 0));
            XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, values.size(), 1, 1));

            // 创建柱状图
            XDDFBarChartData barChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            XDDFBarChartData.Series series = (XDDFBarChartData.Series) barChart.addSeries(categoriesData, valuesData);
            series.setTitle("Data", null);

            // 绘制图表
            chart.plot(barChart);

            // 保存文件
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            return "Successfully created bar chart in " + filePath;
        }
    }
} 