package run.mone.mimeter.dashboard.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.xiaomi.youpin.ks3.KsyunService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.UploadFileRes;
import run.mone.mimeter.dashboard.pojo.Dataset;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static run.mone.mimeter.dashboard.bo.common.Constants.TRUE;
import static run.mone.mimeter.dashboard.exception.CommonError.*;

@Service
@Slf4j
public class UploadService {

    public static final int PreviewFileRowNum = 8;

    private static final int MaxFileSizeMB = 100;

    private static final int MaxFileSizeB = MaxFileSizeMB * 1024 * 1024;


    private static final int MaxFileRows = 50000;


    @Value("${upload.save.base}")
    private String basePath;

    private KsyunService ksyunService;

    @NacosValue("${ks3.AccessKeyID}")
    private String accesskey;

    @NacosValue("${ks3.AccessKeySecret}")
    private String accessSecret;

    @PostConstruct
    private void init() {
        ksyunService = new KsyunService();
        ksyunService.setAccessKeyID(accesskey);
        ksyunService.setAccessKeySecret(accessSecret);
        ksyunService.init();
    }

    public Result<UploadFileRes> save(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        if (fileName == null){
            fileName = "tmpFileName";
        }
        String url;
        String ksKey;
        long rows;
        long size = file.getSize();
        List<String> previewFileRows = new ArrayList<>();
        String firstRow = "";

        try {
            File tmpFile = File.createTempFile("upload-mimeter-file-", file.getOriginalFilename());
            OutputStream os = new FileOutputStream(tmpFile);
            os.write(file.getBytes());
            os.flush();

            FileReader fileReader = new FileReader(tmpFile);
            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
            lineNumberReader.skip(Long.MAX_VALUE);
            rows = lineNumberReader.getLineNumber();
            fileReader.close();
            lineNumberReader.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            for (int i = 0; i < PreviewFileRowNum; i++) {
                String str = bufferedReader.readLine();
                if (i == 0) {
                    firstRow = str;
                }
                if (str == null) {
                    break;
                }
                previewFileRows.add(str);
            }

            Pair<Integer, String> checkRes = checkParam(fileName, rows, size, firstRow);
            if (checkRes.getKey() != 0) {
                return Result.fail(checkRes.getKey(), checkRes.getValue());
            }
            ksKey = basePath + System.currentTimeMillis() + "/" + fileName;
            url = ksyunService.uploadFile(ksKey, tmpFile, 60 * 60 * 24 * 23999);
            boolean success = tmpFile.delete();
            if (!success) {
                log.info("[UploadService.save] file {} upload successfully: {}", fileName, url);
                return Result.fail(UploadFileError.code, UploadFileError.message);
            }

        } catch (IOException e) {
            log.error("[UploadService.save] failed to save file {}, err: {}", fileName, e);
            return Result.fail(UploadFileError.code, UploadFileError.message);
        }

        //这一行有问题 todo
        List<String> firstRowList = Arrays.asList(firstRow.split(","));

        UploadFileRes uploadFileRes = new UploadFileRes();
        uploadFileRes.setFileName(fileName);
        uploadFileRes.setFileKsKey(ksKey);
        uploadFileRes.setFileRows(rows);
        uploadFileRes.setFileSize(size);
        uploadFileRes.setFileUrl(url);
        uploadFileRes.setFileColumns(firstRowList.size());
        uploadFileRes.setFirstRow(firstRowList);
        uploadFileRes.setPreviewFileRows(previewFileRows);

        return Result.success(uploadFileRes);
    }


    public Result<List<String>> loadFileByRange(Dataset dataset, int from, int to) {

        List<String> lines = new ArrayList<>();
        try {
            byte[] data = ksyunService.getFileByKey(dataset.getFileKsKey());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF8"));
            for (int i = 0; i < to; i++) {
                String line = bufferedReader.readLine();
                if (i == 0 && dataset.getIgnoreFirstRow() == TRUE) {
                    to++;
                    continue;
                }
                if (i >= from) {
                    lines.add(line);
                }
                if (line == null) {
                    break;
                }
            }
        } catch (IOException e) {
            return Result.fail(UploadFileError.code, LoadFileError.message);
        }
        return Result.success(lines);
    }

    public Result<List<String[]>> loadStringArrByRange(Dataset dataset, int from, int to) {

        List<String[]> lines = new ArrayList<>();
        try {
            byte[] data = ksyunService.getFileByKey(dataset.getFileKsKey());
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(new ByteArrayInputStream(data), "UTF8")).withSkipLines(from).build();

            if (dataset.getIgnoreFirstRow() == TRUE) {
                to++;
            }
            String[] rawLine;
            int i = 0;
            while((rawLine = csvReader.readNext()) != null && i < to - from) {
                if (from == 0 && i == 0 && dataset.getIgnoreFirstRow() == TRUE) {
                    i++;
                    continue;
                }
                lines.add(rawLine);
                i++;
            }
        } catch (IOException e) {
            return Result.fail(UploadFileError.code, LoadFileError.message);
        }
        return Result.success(lines);
    }


    private Pair<Integer, String> checkParam(String fileName, long rows, long size, String firstRow) {
        if (!fileName.endsWith(".csv")) {
            return Pair.of(WrongFileTypeError.code, WrongFileTypeError.message);
        }
        if (rows > MaxFileRows) {
            return Pair.of(OverMaxFileRowsError.code, "上传文件行数不得超过" + MaxFileRows + "行");
        }
        if (size > MaxFileSizeB) {
            return Pair.of(OverMaxFileSizeError.code, "上传文件大小不得超过" + MaxFileSizeMB + "M");
        }
        if (StringUtils.isEmpty(firstRow)) {
            return Pair.of(FileFirstRowEmptyError.code, FileFirstRowEmptyError.message);
        }
        return Pair.of(0, "success");
    }

}
