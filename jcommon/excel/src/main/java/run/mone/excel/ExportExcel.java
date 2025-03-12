package run.mone.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ExportExcel {
    /**
     * 将对象集合导出到excel
     * @param list
     * @return
     */
    public static HSSFWorkbook HSSFWorkbook(List<T> list) {

        // 1、创建一个excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 2、创建文档摘要
        workbook.createInformationProperties();

        // 5、创建样式
        // 创建标题行的样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex()); // 背景颜色

        HSSFSheet sheet = workbook.createSheet();// 不传name 默认为sheet1
        // 6、创建标题行 第一行数据
        // 只循环一次目的是将对象名写入到excel标题上
        for (T t : list) {
            HSSFRow row = sheet.createRow(0);
            String[] fieldNames = getFiledNames(t);
            for (int i=0; i<fieldNames.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(fieldNames[i]);
                cell.setCellStyle(headerStyle);
            }
            break;
        }

        // 7、创建后面行
        for (int j=0; j<list.size(); j++) {
            T t = list.get(j);
            String[] fieldValues = getFieldValues(t);
            // 由于第一行已经写入了标题，所以这里从第二行开始写
            HSSFRow rows = sheet.createRow(j + 1);
            for (int i=0; i<fieldValues.length; i++) {
                rows.createCell(i).setCellValue(fieldValues[i]);
            }
        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        workbook.write(baos);
        return workbook;
    }

    public static HSSFWorkbook HSSFWorkbook4Map(List<Map<String, Object>> list, String title) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        // title
        if (title != null && !"".equals(title)) {
            HSSFRow titleRow = sheet.createRow(0);
            HSSFCell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            HSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            HSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            cell.setCellStyle(titleStyle);
            titleRow.setHeight((short) 450);

            int endMergeCellIndex = list == null || list.isEmpty() ? 5 : list.get(0).size() - 1;
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, endMergeCellIndex));
        }
        if (list == null || list.isEmpty()) {
            return workbook;
        }
        // header
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM_DASHED);
        int headRowIndex = title == null || "".equals(title) ? 0 : 1;
        HSSFRow headRow = sheet.createRow(headRowIndex);
        int i = 0;
        for (Map.Entry<String, Object> entry : list.get(0).entrySet()) {
            HSSFCell cell = headRow.createCell(i++);
            cell.setCellValue(entry.getKey());
            cell.setCellStyle(headerStyle);
        }

        // data
        int dataBeginRow = title == null || "".equals(title) ? 1 : 2;
        for (int j = 0; j < list.size(); j++) {
            HSSFRow rows = sheet.createRow(dataBeginRow++);
            int k = 0;
            for (Map.Entry<String, Object> entry : list.get(j).entrySet()) {
                HSSFCell cell = rows.createCell(k++);
                cell.setCellValue(entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        workbook.write(baos);
        return workbook;
    }

    /**
     * 判断字符串首字母是否为大写，如果不是转化为大写
     * @param str
     * @return
     */
    public static String returnFirstCapital(String str) {
        if (str.charAt(0) >= 'A' && str.charAt(0) <= 'Z') {
            return str;
        }
        char[] ch = str.toCharArray();
        ch[0] -= 32;
        return String.valueOf(ch);
    }

    /**
     * 获取所有对象属性名称
     * @param o
     * @return
     */
    public static String[] getFiledNames(Object o) {
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 获取对象属性值
     * @param o
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static String[] getFieldValues(Object o) {
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        String[] fieldValues = new String[fieldNames.length];
        for(int i=0;i<fields.length;i++){
            fieldNames[i]=fields[i].getName();
        }
        try {
            for (int i=0; i<fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                String field = o.getClass().getMethod("get"+ returnFirstCapital(fieldName)).invoke(o).toString();
                fieldValues[i] = field;
            }
        } catch(Exception e) {

        }
        return fieldValues;
    }


}
