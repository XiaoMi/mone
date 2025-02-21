import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import run.mone.mcp.file.function.FileFormatFunction;


import java.util.Map;


public class FileFormatTest {

    private static final Gson gson = new Gson();

    // 测试word转PDF
    @Test
    public void testPDF2Word() {
        FileFormatFunction fileFormatFunction = new FileFormatFunction();
        fileFormatFunction.apply(Map.of(
                "type", "pdf_to_word",
                "input_file", "C:/Users/wangling8/Desktop/test3.pdf",
                "output_file", "C:/Users/wangling8/Desktop/test4.docx"
        ));
    }

    @Test
    public void testWord2PDF() {
        FileFormatFunction fileFormatFunction = new FileFormatFunction();
        fileFormatFunction.apply(Map.of(
                "type", "word_to_pdf",
                "input_file", "C:/Users/wangling8/Desktop/test3.docx",
                "output_file", "C:/Users/wangling8/Desktop/test4.pdf"
        ));
    }

} 