package run.mone.m78.service.service.contact;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.contact.ContactParam;
import run.mone.m78.service.dao.mapper.M78ContactInfoMapper;
import run.mone.m78.service.dao.entity.M78ContactInfoPo;

import javax.annotation.Resource;

/**
 * @author dp
 * @date 2024/1/17
 */
@Service
@Slf4j
public class ContactService {

    @Resource
    private M78ContactInfoMapper m78ContactInfoMapper;

    /**
     * 提交联系信息
     *
     * @param contactParam 联系信息参数
     * @return 包含插入结果的Result对象
     */
	public Result<Integer> submit(ContactParam contactParam) {
        int res = m78ContactInfoMapper.insertSelective(M78ContactInfoPo.builder()
                .contactContent(contactParam.getContactContent())
                .contactEmail(contactParam.getContactEmail())
                .contactName(contactParam.getContactName())
                .contactSubject(contactParam.getContactSubject())
                .userName(contactParam.getUserName())
                .build());
        return Result.success(res);
    }


}
