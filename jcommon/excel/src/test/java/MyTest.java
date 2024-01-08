import org.junit.Test;
import run.mone.excel.CsvExcelConverter;

import java.io.IOException;

/**
 * @author wmin
 * @date 2024/1/8
 */
public class MyTest {

    @Test
    public void testConvertCsvToExcel() {
        try {
            CsvExcelConverter.convertCsvToExcel("/Users/wmin/Downloads/miline_scaleOrder.csv","/Users/wmin/Downloads/miline_scaleOrder_0.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConvertExcelToCsv() {
        try {
            CsvExcelConverter.convertExcelToCsv("/Users/wmin/Downloads/miline_scaleOrder.xlsx","/Users/wmin/Downloads/miline_scaleOrder_0.csv");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateCsvColumnWhereAnotherColumnEquals() {
        try {
            CsvExcelConverter.updateCsvColumnWhereAnotherColumnEquals("/Users/wmin/Downloads/miline_scaleOrder.csv",
                    "/Users/wmin/Downloads/miline_scaleOrder1.csv",
                    "env", "id","60267","online");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
