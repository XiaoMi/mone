package run.mone.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.knowledge.api.IKnowledgeVectorProvider;
import run.mone.knowledge.api.dto.VectorData;
import run.mone.knowledge.api.enums.KnowledgeTypeEnum;
import run.mone.knowledge.api.dto.KnowledgeVectorDetailDto;
import run.mone.knowledge.api.dto.KnowledgeVectorDto;
import run.mone.knowledge.api.dto.SimilarKnowledgeVectorQry;
import run.mone.knowledge.server.PrivateKnowledgeBootstrap;
import run.mone.knowledge.service.RedisVectorService;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author wmin
 * @date 2024/2/19
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PrivateKnowledgeBootstrap.class)
public class VectorProviderTest {
    @Resource
    private IKnowledgeVectorProvider knowledgeVectorProvider;

    @Autowired
    private RedisVectorService redisVectorService;

    @Test
    public void getGroupFromRedis() {
        System.out.println(redisVectorService.listByGroup(Arrays.asList(VectorData.makeGroupKey(KnowledgeTypeEnum.project_code.getTypeName(),"tag21"),
                VectorData.makeGroupKey(KnowledgeTypeEnum.project_code.getTypeName(),"tag22"))));
    }

    @Test
    public void insertKnowledgeVector() {
        KnowledgeVectorDto param = new KnowledgeVectorDto();
        param.setType(KnowledgeTypeEnum.project_code.getTypeName());
        KnowledgeVectorDetailDto detailDto = new KnowledgeVectorDetailDto();
        detailDto.setTag1("tag1");
        detailDto.setTag2("tag21");
        detailDto.setTag3("tag31");
        detailDto.setContent("tomato-番茄");
        KnowledgeVectorDetailDto detailDto2 = new KnowledgeVectorDetailDto();
        detailDto2.setTag1("tag1");
        detailDto2.setTag2("tag22");
        detailDto2.setTag3("tag32");
        detailDto2.setContent("banana香蕉");
        param.setKnowledgeVectorDetailDtoList(Arrays.asList(detailDto, detailDto2));
        System.out.println(knowledgeVectorProvider.insertOrUpdateKnowledgeVector(param));
    }

    @Test
    public void deleteKnowledgeVector() {
        KnowledgeVectorDto param = new KnowledgeVectorDto();
        param.setType(KnowledgeTypeEnum.project_code.getTypeName());
        KnowledgeVectorDetailDto detailDto = new KnowledgeVectorDetailDto();
        detailDto.setTag1("tag1");
        detailDto.setTag2("tag21");
        detailDto.setTag3("tag31");
//        KnowledgeVectorDetailDto detailDto2 = new KnowledgeVectorDetailDto();
//        detailDto2.setTag1("tag1");
//        detailDto2.setTag2("tag22");
//        detailDto2.setTag3("tag32");
        param.setKnowledgeVectorDetailDtoList(Arrays.asList(detailDto));
        System.out.println(knowledgeVectorProvider.deleteKnowledgeVector(param));
    }

    @Test
    public void qrySimilarKnowledgeVector() {
        SimilarKnowledgeVectorQry param = new SimilarKnowledgeVectorQry();
        param.setType(KnowledgeTypeEnum.project_code.getTypeName());
        //param.setTag1("tag1");
        //param.setTag2("tag22");
        //param.setTag3("tag32");
        param.setQuestionContent("banana");
        System.out.println(knowledgeVectorProvider.qrySimilarKnowledgeVector(param));
    }
}

