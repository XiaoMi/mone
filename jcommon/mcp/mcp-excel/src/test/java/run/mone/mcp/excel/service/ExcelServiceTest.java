package run.mone.mcp.excel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelServiceTest {

    private ExcelService excelService;
    
    @BeforeEach
    void setUp() {
        excelService = new ExcelService();
    }

    @Test
    void testCreateBarChart() throws Exception {
        // 准备测试数据
        String filePath = "/Users/ericgreen/Desktop/test-chart.xlsx";
        String sheetName = "Sales Data";
        String chartTitle = "Monthly Sales 2024";
        String categoryAxisTitle = "Month";
        String valueAxisTitle = "Sales Amount (USD)";
        List<String> categories = Arrays.asList("January", "February", "March", "April", "May");
        List<Number> values = Arrays.asList(1000, 1500, 1200, 1800, 2000);

        // 执行创建图表操作
        String result = excelService.createBarChart(
                filePath,
                sheetName,
                chartTitle,
                categoryAxisTitle,
                valueAxisTitle,
                categories,
                values
        );

        // 验证结果
        assertTrue(result.contains("Successfully created"));
        assertTrue(result.contains(filePath));
        
    }

}