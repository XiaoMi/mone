import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import run.mone.mcp.file.function.FileFormatFunction;


import java.util.Map;


public class FileFormatTest {

    private static final Gson gson = new Gson();


    @Test
    public void testExcel2CSV() {
        FileFormatFunction fileFormatFunction = new FileFormatFunction();
        fileFormatFunction.apply(Map.of(
                "type", "excel_to_csv",
                "input_file", "C:/Users/wangling8/Desktop/test_result_CTQ3.xlsx",
                "output_file", "C:/Users/wangling8/Desktop/testcnnn.csv"
        ));
    }

    @Test
    public void testCSV2Excel() {
        FileFormatFunction fileFormatFunction = new FileFormatFunction();
        fileFormatFunction.apply(Map.of(
                "type", "csv_to_excel",
                "input_file", "C:/Users/wangling8/Desktop/qt3.csv",
                "output_file", "C:/Users/wangling8/Desktop/testwlll.xlsx"
        ));
    }

} 