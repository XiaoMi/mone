package run.mone.z.desensitization.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.z.desensitization.dto.DesensitizeRsp;
import run.mone.z.desensitization.mapper.ZDesensitizationRecordMapper;
import run.mone.z.desensitization.pojo.ZDesensitizationRecord;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author wmin
 * @date 2024/1/31
 */
@Slf4j
@Service
public class RecordService {

    @Resource
    private ZDesensitizationRecordMapper recordMapper;

    public void saveRecord(DesensitizeRsp rsp) {
        try {
            ZDesensitizationRecord record = new ZDesensitizationRecord();
            record.setGmtCreate(new Date());
            record.setCreator(rsp.getUsername());
            record.setTextBefore(rsp.getTextBefore());
            record.setTextAfter(rsp.getTextAfter());
            record.setStatus(rsp.getStatus());
            record.setDurationTime(rsp.getDurationTime());
            log.info("saveRecord :{}", recordMapper.insert(record));
        } catch (Exception e) {
            log.error("saveRecord", e);
        }
    }
}
