package run.mone.mimeter.dashboard.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dingpei
 * @date 2022-07-18
 */
public class FileUtils {

    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    static {
        // images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        /*CAD*/
        mFileTypes.put("41433130", "dwg");
        mFileTypes.put("38425053", "psd");
        /* 日记本 */
        mFileTypes.put("7B5C727466", "rtf");
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        // 邮件
        mFileTypes.put("44656C69766572792D646174653A", "eml");
        mFileTypes.put("D0CF11E0", "doc");
        //excel2003版本文件
        mFileTypes.put("D0CF11E0", "xls");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("25504446", "pdf");
        mFileTypes.put("504B0304", "docx");
        //excel2007以上版本文件
        mFileTypes.put("504B0304", "xlsx");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
    }

    /**
     * <p>Title:getFileType </p>
     * <p>Description: 根据文件路径获取文件头信息</p>
     *
     * @param filePath 文件路径(非网络文件)
     * @return 文件头信息
     */
    public static String getFileType(String filePath) {
        //返回十六进制  如：504B0304
        return mFileTypes.get(getFileHeader(filePath));
    }

    /**
     * <p>Title:getFileTypeByFileInputStream </p>
     * <p>Description: 根据文件流获取文件头信息</p>
     *
     * @param is 文件流
     * @return 文件头信息
     */
    public static String getFileTypeByFileInputStream(InputStream is) {
        return mFileTypes.get(getFileHeaderByFileInputStream(is));
    }

    /**
     * <p>Title:getFileHeader </p>
     * <p>Description: 根据文件路径获取文件头信息 </p>
     *
     * @param filePath 文件路径
     * @return 十六进制文件头信息
     */
    private static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * <p>Title:getFileHeaderByFileInputStream </p>
     * <p>Description: 根据文件流获取文件头信息</p>
     *
     * @param is 文件流
     * @return 十六进制文件头信息
     */
    private static String getFileHeaderByFileInputStream(InputStream is) {
        String value = null;
        try {
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * <p>Title:bytesToHexString </p>
     * <p>Description: 将要读取文件头信息的文件的byte数组转换成string类型表示 </p>
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        //System.out.println(builder.toString());
        return builder.toString();
    }

    public static List<String> parseCsv(String line) {
        Pattern pattern = Pattern.compile("[^,\"]+|,,|(?:\"[^,\"]*\"[^\"]*\"[^\"]*)\"|\"(?:[^\"])*\"");

        List<String> list = new ArrayList<>();

        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String cell = matcher.group();
            list.add(cell);
        }
        return list;
    }

}