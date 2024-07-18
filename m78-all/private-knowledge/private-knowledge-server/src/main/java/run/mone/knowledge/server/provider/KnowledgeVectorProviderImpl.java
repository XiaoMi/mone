package run.mone.knowledge.server.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import run.mone.knowledge.api.IKnowledgeVectorProvider;
import run.mone.knowledge.api.enums.KnowledgeTypeEnum;
import run.mone.knowledge.api.dto.*;
import run.mone.knowledge.service.EmbeddingService;
import run.mone.knowledge.service.OzVectorDbService;
import run.mone.knowledge.service.RedisVectorService;
import run.mone.knowledge.service.dao.entity.VKnowledgeVectorDetailPo;
import run.mone.knowledge.service.dao.entity.VKnowledgeVectorMetaPo;
import run.mone.knowledge.service.dao.mapper.VKnowledgeVectorDetailMapper;
import run.mone.knowledge.service.dao.mapper.VKnowledgeVectorMetaMapper;
import run.mone.knowledge.service.exceptions.ExCodes;
import run.mone.knowledge.service.impl.VectorDtoTruncateService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/2/5
 */
@Slf4j
@DubboService(interfaceClass = IKnowledgeVectorProvider.class, group = "${dubbo.group}", version = "1.0")
public class KnowledgeVectorProviderImpl implements IKnowledgeVectorProvider {

    public static Gson gson = new Gson();

    @Resource
    private VKnowledgeVectorDetailMapper detailMapper;
    @Resource
    private VKnowledgeVectorMetaMapper metaMapper;
    @Autowired
    private RedisVectorService redisVectorService;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private OzVectorDbService ozVectorDbService;

    @Autowired
    private VectorDtoTruncateService vectorDtoTruncateService;


    @Transactional
    @Override
    public Result<Boolean> insertOrUpdateKnowledgeVector(KnowledgeVectorDto param) {
        Pair<Boolean, String> checkRst = checkKnowledgeVectorDto(param, "insertOrUpdate");
        if (!checkRst.getKey()) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, checkRst.getValue());
        }
        List<VectorData> vectorDataList = new ArrayList<>();

        List<KnowledgeVectorDetailDto> knowledgeVectorDetailDtoList = param.getKnowledgeVectorDetailDtoList();
        KnowledgeTypeEnum typeEnum = KnowledgeTypeEnum.getEnumByTypeName(param.getType());

        Pair<Boolean, String> tagsCheckRst = Pair.of(true, "");

        for (KnowledgeVectorDetailDto detailDto : knowledgeVectorDetailDtoList){
            KnowledgeVectorDetailFullDto fullDto = vectorDtoTruncateService.getFullDto(detailDto, typeEnum);
            log.info("insertOrUpdateKnowledgeVector fullDto: {}", fullDto.getFullTags());
            if (StringUtils.isBlank(fullDto.getGroupTag()) || StringUtils.isBlank(fullDto.getLeafTag())){
                log.error("tagsCheckRst failed, tags:{}", fullDto.getFullTags());
                tagsCheckRst = Pair.of(false, "groupTag/leafTag is empty. tags:"+fullDto.getFullTags());
                break;
            }

            VKnowledgeVectorMetaPo vectorMeta = vectorDtoTruncateService.convertVectorMetaDtoToPo(fullDto);
            Integer metaId = insertOrGetMeta(vectorMeta);
            log.info("insertOrGetMeta id:{}", metaId);
            if (metaId == -1){
                continue;
            }
            VKnowledgeVectorDetailPo vectorDetail = vectorDtoTruncateService.convertVectorDetailDtoToPo(fullDto);
            vectorDetail.setMetaId(metaId);
            //查询是否已有
            VKnowledgeVectorDetailPo dbVectorDetail = detailMapper.selectOneByQuery(
                    QueryWrapper.create().eq("type", vectorDetail.getType()).eq("meta_id", metaId).eq("leaf_tag", vectorDetail.getLeafTag())
            );
            Date currentTime = new Date();
            if (dbVectorDetail!=null){
                VKnowledgeVectorDetailPo updatePO = UpdateEntity.of(VKnowledgeVectorDetailPo.class, dbVectorDetail.getId());
                updatePO.setContent(vectorDetail.getContent());
                updatePO.setVector(vectorDetail.getVector());
                vectorDetail.setGmtModified(currentTime);
                detailMapper.update(updatePO);
                log.info("detail update :{}", dbVectorDetail.getId());
            } else {
                vectorDetail.setGmtCreate(currentTime);
                vectorDetail.setGmtModified(currentTime);
                vectorDetail.setEmbeddingStatus(2);
                detailMapper.insert(vectorDetail);
                log.info("detail insert :{}", vectorDetail.getId());
            }

            VectorData vectorData = VectorData.builder().
                    type(vectorMeta.getType()).
                    group(vectorMeta.getGroupTag()).
                    leaf(vectorDetail.getLeafTag()).
                    vector(detailDto.getVector()).build();
            vectorDataList.add(vectorData);
        }
        if (tagsCheckRst.getKey()){
            redisVectorService.updateVectorByLeaf(vectorDataList);
            return Result.success(true);
        }
        return Result.fail(ExCodes.STATUS_BAD_REQUEST, tagsCheckRst.getValue());
    }

    private Integer insertOrGetMeta(VKnowledgeVectorMetaPo vectorMeta){
        QueryWrapper queryWrapper = QueryWrapper.create().
                eq("type", vectorMeta.getType()).
                eq("tag1", vectorMeta.getTag1()).
                eq("group_tag", vectorMeta.getGroupTag());
        List<VKnowledgeVectorMetaPo> metas = metaMapper.selectListByQuery(queryWrapper);
        if (CollectionUtils.isEmpty(metas)){
            log.info("empty meta db,{},{},{}", vectorMeta.getType(), vectorMeta.getTag1(), vectorMeta.getGroupTag());
            vectorMeta.setGmtCreate(new Date());
            vectorMeta.setGmtModified(new Date());
            metaMapper.insert(vectorMeta);
            return vectorMeta.getId();
        } else {
            if (metas.get(0).getDeleted() == 1){
                log.warn("meta is deleted. id:{}", metas.get(0).getId());
                return -1;
            }
            return metas.get(0).getId();
        }
    }


    @Transactional
    @Override
    public Result<Boolean> deleteKnowledgeVector(KnowledgeVectorDto param) {
        Pair<Boolean, String> checkRst = checkKnowledgeVectorDto(param, "delete");
        if (!checkRst.getKey()) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, checkRst.getValue());
        }

        List<KnowledgeVectorDetailDto> knowledgeVectorDetailDtoList = param.getKnowledgeVectorDetailDtoList();
        KnowledgeTypeEnum typeEnum = KnowledgeTypeEnum.getEnumByTypeName(param.getType());

        List<VKnowledgeVectorMetaPo> vectorMetas = new ArrayList<>();
        for (KnowledgeVectorDetailDto detailDto : knowledgeVectorDetailDtoList) {
            KnowledgeVectorDetailFullDto fullDto = vectorDtoTruncateService.getFullDto(detailDto, typeEnum);

            //删除leafTag
            if (StringUtils.isNotBlank(fullDto.getLeafTag())){
                VKnowledgeVectorDetailPo vectorDetail = detailMapper.selectOneByQuery(
                        QueryWrapper.create().eq("type", param.getType()).eq("leaf_tag", fullDto.getLeafTag())
                );
                if (vectorDetail!=null){
                    log.info("delete vectorDetail id:{}, count:{}", vectorDetail.getId(), detailMapper.deleteById(vectorDetail.getId()));
                }
                VectorData vectorData = VectorData.builder().
                        type(fullDto.getType()).
                        group(fullDto.getGroupTag())
                        .leaf(fullDto.getLeafTag()).build();
                boolean redisDel = redisVectorService.deleteByLeaf(Arrays.asList(vectorData));
                log.info("delete redis leaf group_leaf:{}, rst:{}", fullDto.getGroupTag()+"_"+fullDto.getLeafTag(), redisDel);
            }
            //删除单个groupTag
            else if (StringUtils.isNotBlank(fullDto.getGroupTag())){
                VKnowledgeVectorMetaPo vectorMeta = metaMapper.selectOneByQuery(
                        QueryWrapper.create().eq("type", fullDto.getType()).eq("group_tag", fullDto.getGroupTag()).eq("deleted", 0)
                );
                if (vectorMeta==null){
                    log.warn("can not find groupTag:{}", fullDto.getGroupTag());
                    continue;
                }
                vectorMetas.add(vectorMeta);
            } else {
                StringBuilder qryTags = new StringBuilder(detailDto.getTag1());
                //从meta表中查询所有符合条件的group
                QueryWrapper queryWrapper = QueryWrapper.create().
                        eq("type", fullDto.getType()).
                        eq("deleted", 0).
                        eq("tag1", detailDto.getTag1());
                if (StringUtils.isNotBlank(detailDto.getTag2())){
                    queryWrapper.eq("tag2", detailDto.getTag2());
                    qryTags.append("_"+detailDto.getTag2());
                }
                if (StringUtils.isNotBlank(detailDto.getTag3())){
                    queryWrapper.eq("tag3", detailDto.getTag3());
                    qryTags.append("_"+detailDto.getTag3());
                }
                if (StringUtils.isNotBlank(detailDto.getTag4())){
                    queryWrapper.eq("tag4", detailDto.getTag4());
                    qryTags.append("_"+detailDto.getTag4());
                }

                List<VKnowledgeVectorMetaPo> metaList = metaMapper.selectListByQuery(queryWrapper);
                if (CollectionUtils.isEmpty(metaList)){
                    log.warn("can not find qryTags:{}", qryTags);
                    continue;
                }
                log.info("qryTags:{}, ids:{}", qryTags, metaList.stream().map(i ->i.getId()).collect(Collectors.toList()));
                vectorMetas.addAll(metaList);
            }
        }
        if (CollectionUtils.isEmpty(vectorMetas)){
            log.info("There is no vectorMetas to delete");
            return Result.success(true);
        }
        List<Integer> metaIds = vectorMetas.stream().map(i -> i.getId()).collect(Collectors.toList());
        log.info("preparing to delete from db metaIds:{}", metaIds);
        int detailCount = detailMapper.deleteByQuery(QueryWrapper.create().eq("type", param.getType()).in("meta_id", metaIds));
        int metaCount = param.isForceDelete() ? metaMapper.deleteBatchByIds(metaIds) : metaMapper.softDelete(metaIds);
        log.info("delete from db detailCount:{},metaCount:{},isForceDelete:{}", detailCount, metaCount, param.isForceDelete());
        List<String> groupKeyList = vectorMetas.stream().map(i -> VectorData.makeGroupKey(i.getType(), i.getGroupTag())).collect(Collectors.toList());
        log.info("delete from redis groupKeyList:{}, rst:{}", groupKeyList, redisVectorService.deleteByGroup(groupKeyList));
        return Result.success(true);
    }


    @Override
    public Result<List<SimilarKnowledgeVectorRsp>> qrySimilarKnowledgeVector(SimilarKnowledgeVectorQry qry) {
        Pair<Boolean, String> checkRst = checkSimilarKnowledgeVectorQry(qry);
        if (!checkRst.getKey()) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, checkRst.getValue());
        }

        KnowledgeTypeEnum typeEnum = KnowledgeTypeEnum.getEnumByTypeName(qry.getType());

        List<VectorData> vectorDataList = new ArrayList<>();

        List<TagsInfo> tagsInfoList = qry.getTagsInfoList();

        for (TagsInfo tagsInfo : tagsInfoList){
            TagsFullInfo fullQry = vectorDtoTruncateService.getSimilarKnowledgeVectorFullQry(tagsInfo, typeEnum);
            //是否为leafTag范围的查询
            if (StringUtils.isNotBlank(fullQry.getLeafTag())){
                VKnowledgeVectorDetailPo vectorDetail = detailMapper.selectOneByQuery(
                        QueryWrapper.create().eq("type", qry.getType()).eq("leaf_tag", fullQry.getLeafTag())
                );
                if (vectorDetail==null){
                    return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Can't find leafTag[" + fullQry.getLeafTag() + "]");
                }
                VKnowledgeVectorMetaPo vectorMeta = metaMapper.selectOneByQuery( QueryWrapper.create().eq("id", vectorDetail.getMetaId()).eq("deleted", 0));
                vectorDataList.add(
                        VectorData.builder().
                                group(vectorMeta.getGroupTag()).
                                leaf(vectorDetail.getLeafTag()).
                                vector(gson.fromJson(vectorDetail.getVector(), new TypeToken<double[]>() {}.getType())).build()
                );

            } //是否为单个groupTag的查询
            else if (StringUtils.isNotBlank(fullQry.getGroupTag())){
                VKnowledgeVectorMetaPo vectorMeta = metaMapper.selectOneByQuery(
                        QueryWrapper.create().eq("type", qry.getType()).eq("group_tag", fullQry.getGroupTag()).eq("deleted", 0)
                );
                if (vectorMeta==null){
                    log.warn("Can't find groupTag[{}]", fullQry.getGroupTag());
                    continue;
                }
                log.info("db meta id:{},groupTag:{}", vectorMeta.getId(), vectorMeta.getGroupTag());
                vectorDataList.addAll(redisVectorService.listByGroup(Arrays.asList(VectorData.makeGroupKey(vectorMeta.getType(), vectorMeta.getGroupTag()))));

            } else {
                StringBuilder qryTags = new StringBuilder(tagsInfo.getTag1());
                //从meta表中查询所有符合条件的group
                QueryWrapper queryWrapper = QueryWrapper.create().
                        eq("deleted", 0).
                        eq("type", qry.getType()).
                        eq("tag1", tagsInfo.getTag1());
                if (StringUtils.isNotBlank(tagsInfo.getTag2())){
                    queryWrapper.eq("tag2", tagsInfo.getTag2());
                    qryTags.append("_"+tagsInfo.getTag2());
                }
                if (StringUtils.isNotBlank(tagsInfo.getTag3())){
                    queryWrapper.eq("tag3", tagsInfo.getTag3());
                    qryTags.append("_"+tagsInfo.getTag3());
                }
                if (StringUtils.isNotBlank(tagsInfo.getTag4())){
                    queryWrapper.eq("tag4", tagsInfo.getTag4());
                    qryTags.append("_"+tagsInfo.getTag4());
                }

                List<VKnowledgeVectorMetaPo> metaList = metaMapper.selectListByQuery(queryWrapper);
                if (CollectionUtils.isEmpty(metaList)){
                    log.warn("Can't find tag[{}]", qryTags);
                    continue;
                }
                List<String> groupList = metaList.stream().map(i -> VectorData.makeGroupKey(i.getType(), i.getGroupTag())).collect(Collectors.toList());
                vectorDataList.addAll(redisVectorService.listByGroup(groupList));
            }
        }
        if (CollectionUtils.isEmpty(vectorDataList)){
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Can't find any tag");
        }
        log.info("qry from vectorDataList.size:{}", vectorDataList.size());


        //questionContent向量化
        double[] queryVector = StringUtils.isBlank(qry.getQuestionContent())?qry.getQuestionVector():embeddingService.getEmbeddingArr(qry.getQuestionContent());
        if (queryVector == null){
            return Result.fail(ExCodes.STATUS_INTERNAL_ERROR, "questionContent embedding error");
        }

        //cosineSimilarity
        VectorLimits limits = VectorLimits.builder().similarity(qry.getSimilarity()).topN(qry.getTopN()).build();
        List<VectorData> topNData = ozVectorDbService.cosineSimilarity(queryVector, vectorDataList, limits);

        List<SimilarKnowledgeVectorRsp> rstList = topNData.stream().map(i ->
            SimilarKnowledgeVectorRsp.builder().group(i.getGroup()).leaf(i.getLeaf()).similarity(i.getSimilarity()).build()
        ).collect(Collectors.toList());

        log.info("qryKnowledgeVector rst:{}", rstList);
        return Result.success(rstList);
    }


    private Pair<Boolean, String> checkSimilarKnowledgeVectorQry(SimilarKnowledgeVectorQry param) {
        if (null == param || CollectionUtils.isEmpty(param.getTagsInfoList())) {
            return Pair.of(false, "param is invalid");
        }
        if (!KnowledgeTypeEnum.isEnumValueValid(param.getType())) {
            return Pair.of(false, "type is invalid");
        }
        if (null == param.getQuestionVector() && StringUtils.isBlank(param.getQuestionContent())) {
            return Pair.of(false, "question is empty");
        }
        List<TagsInfo> tagsInfoList = param.getTagsInfoList();
        for (TagsInfo tagsInfo : tagsInfoList){
            if (null == tagsInfo || StringUtils.isBlank(tagsInfo.getTag1())){
                return Pair.of(false, "tagsInfoList is invalid");
            }
        }
        return Pair.of(true, "");
    }

    private Pair<Boolean, String> checkKnowledgeVectorDto(KnowledgeVectorDto param, String operateType) {
        if (null == param || CollectionUtils.isEmpty(param.getKnowledgeVectorDetailDtoList())) {
            return Pair.of(false, "param is empty");
        }

        if (!KnowledgeTypeEnum.isEnumValueValid(param.getType())) {
            return Pair.of(false, "type is invalid");
        }
        List<KnowledgeVectorDetailDto> knowledgeVectorDetailDtoList = param.getKnowledgeVectorDetailDtoList();

        for (KnowledgeVectorDetailDto detailDto : knowledgeVectorDetailDtoList){
            if (StringUtils.isBlank(detailDto.getTag1())){
                return Pair.of(false, "tag1 is empty");
            }
            if ("insertOrUpdate".equals(operateType)){
                if (param.isNeedEmbedding() && StringUtils.isBlank(detailDto.getContent())){
                    return Pair.of(false, "content is empty");
                }
                if (!param.isNeedEmbedding() && null == detailDto.getVector()){
                    return Pair.of(false, "vector is empty");
                }
            }
        }
        return Pair.of(true, "");
    }

}
