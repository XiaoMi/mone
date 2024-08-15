package run.mone.knowledge.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.knowledge.api.dto.*;
import run.mone.knowledge.api.enums.KnowledgeTypeEnum;
import run.mone.knowledge.service.EmbeddingService;
import run.mone.knowledge.service.dao.entity.VKnowledgeVectorDetailPo;
import run.mone.knowledge.service.dao.entity.VKnowledgeVectorMetaPo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/19
 */
@Service
@Slf4j
public class VectorDtoTruncateService {

    public static Gson gson = new Gson();
    @Autowired
    private EmbeddingService embeddingService;


    /**
     * 将qry入参转换为解析出leafTag和groupTag的fullDto
     */
    public TagsFullInfo getSimilarKnowledgeVectorFullQry(TagsInfo qry, KnowledgeTypeEnum typeEnum) {
        List<String> tags = Arrays.asList(qry.getTag1(), qry.getTag2(), qry.getTag3(), qry.getTag4(), qry.getTag5(), qry.getTag6(), qry.getTag7());
        TagsFullInfo fullQry = new TagsFullInfo();
        fullQry.setTags(tags);
        fullQry.setLeafTag(tags.get(typeEnum.getLeafTagIndex()));
        fullQry.setGroupTag(tags.get(typeEnum.getGroupTagIndex()));
        return fullQry;
    }

    /**
     * 将detail入参转换为解析出leafTag和groupTag的fullDto
     */
    public KnowledgeVectorDetailFullDto getFullDto(KnowledgeVectorDetailDto detailDto, KnowledgeTypeEnum typeEnum) {
        List<String> tags = Arrays.asList(detailDto.getTag1(), detailDto.getTag2(), detailDto.getTag3(), detailDto.getTag4(), detailDto.getTag5(), detailDto.getTag6(), detailDto.getTag7());
        KnowledgeVectorDetailFullDto fullDto = new KnowledgeVectorDetailFullDto();
        fullDto.setType(typeEnum.getTypeName());
        fullDto.setDetailDto(detailDto);
        fullDto.setFullTags(tags);
        fullDto.setLeafTag(tags.get(typeEnum.getLeafTagIndex()));
        fullDto.setGroupTag(tags.get(typeEnum.getGroupTagIndex()));
        fullDto.setGroupTagIndex(typeEnum.getGroupTagIndex());
        fullDto.setLeafTagIndex(typeEnum.getLeafTagIndex());
        return fullDto;
    }


    /**
     * 将VectorMeta dto转换为数据库PO
     */
    public VKnowledgeVectorMetaPo convertVectorMetaDtoToPo(KnowledgeVectorDetailFullDto fullDto) {
        KnowledgeVectorDetailDto detailDto = fullDto.getDetailDto();
        VKnowledgeVectorMetaPo vectorMeta = new VKnowledgeVectorMetaPo();
        vectorMeta.setType(fullDto.getType());
        vectorMeta.setTag1(detailDto.getTag1());
        int sum = fullDto.getGroupTagIndex() + 1;
        int count = 0;
        if (StringUtils.isNotBlank(detailDto.getTag2()) && ++count < sum) vectorMeta.setTag2(detailDto.getTag2());
        if (StringUtils.isNotBlank(detailDto.getTag3()) && ++count < sum) vectorMeta.setTag3(detailDto.getTag3());
        if (StringUtils.isNotBlank(detailDto.getTag4()) && ++count < sum) vectorMeta.setTag4(detailDto.getTag4());
        if (StringUtils.isNotBlank(detailDto.getTag5()) && ++count < sum) vectorMeta.setTag5(detailDto.getTag5());
        if (StringUtils.isNotBlank(detailDto.getTag6()) && ++count < sum) vectorMeta.setTag6(detailDto.getTag6());
        vectorMeta.setGroupTag(fullDto.getGroupTag());
        return vectorMeta;
    }

    /**
     * 将VectorDetail dto转换为数据库PO
     */
    public VKnowledgeVectorDetailPo convertVectorDetailDtoToPo(KnowledgeVectorDetailFullDto fullDto) {
        KnowledgeVectorDetailDto detailDto = fullDto.getDetailDto();
        VKnowledgeVectorDetailPo vectorMeta = new VKnowledgeVectorDetailPo();
        vectorMeta.setContent(detailDto.getContent());
        vectorMeta.setGmtCreate(new Date());
        vectorMeta.setLeafTag(fullDto.getLeafTag());
        vectorMeta.setType(fullDto.getType());
        if (detailDto.getVector() != null) {
            vectorMeta.setVector(gson.toJson(detailDto.getVector()));
        }
        //todo 异步
        if (StringUtils.isNotBlank(detailDto.getContent())) {
            String inputEmbedding = embeddingService.getEmbeddingStr(detailDto.getContent());
            vectorMeta.setVector(inputEmbedding);
            detailDto.setVector(gson.fromJson(inputEmbedding, new TypeToken<double[]>() {
            }.getType()));
        }
        return vectorMeta;
    }
}
