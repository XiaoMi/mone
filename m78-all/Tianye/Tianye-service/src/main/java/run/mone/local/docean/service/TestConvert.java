package run.mone.local.docean.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import run.mone.local.docean.po.AgentInfoPo;
import run.mone.local.docean.po.Message;

/**
 * @author wmin
 * @date 2024/3/1
 */
@Mapper
public interface TestConvert {
    TestConvert INSTANCE = Mappers.getMapper(TestConvert.class);

    AgentInfoPo toPo(Message dto);
}
