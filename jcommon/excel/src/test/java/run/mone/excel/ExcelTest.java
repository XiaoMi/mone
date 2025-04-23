package run.mone.excel;

import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static run.mone.excel.ExportExcel.HSSFWorkbook4Map;

/**
 * @author goodjava@qq.com
 * @date 2024/4/2 21:06
 */
public class ExcelTest {


    @SneakyThrows
    @Test
    public void test1() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        data1.put("name", "j");
        data1.put("age", 11);
        dataList.add(data1);

        data2.put("name", "l");
        data2.put("age", 2);
        dataList.add(data2);

        HSSFWorkbook sheets = HSSFWorkbook4Map(dataList, "");
        FileOutputStream fos = new FileOutputStream("/tmp/abc.xls");
        sheets.write(fos);
    }


    @Test
    public void exportExcel() throws IOException {
        List<Map<String, Object>> exportData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "name" + i);
            map.put("age", i);
            map.put("sex", i % 2 == 0 ? "男" : "女");
            map.put("address", "address" + i);
            map.put("phone", "phone" + i);
            map.put("email", "email" + i);
            exportData.add(map);
        }
        HSSFWorkbook excel = ExportExcel.HSSFWorkbook4Map(exportData, "用户信息表");
        FileOutputStream fos = new FileOutputStream("/tmp/user.xls");
        excel.write(fos);
    }

}
