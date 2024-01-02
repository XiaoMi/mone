package run.mone.mimeter.dashboard.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.sla.GetSlaListReq;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;
import run.mone.mimeter.dashboard.bo.sla.SlaList;
import run.mone.mimeter.dashboard.bo.sla.SlaRuleDto;
import run.mone.mimeter.dashboard.mapper.SlaMapper;
import run.mone.mimeter.dashboard.mapper.SlaRuleMapper;
import run.mone.mimeter.dashboard.service.SceneService;
import run.mone.mimeter.dashboard.service.SlaService;
import run.mone.mimeter.dashboard.pojo.Sla;
import run.mone.mimeter.dashboard.pojo.SlaExample;
import run.mone.mimeter.dashboard.pojo.SlaRule;
import run.mone.mimeter.dashboard.pojo.SlaRuleExample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.DEFAULT_PAGE_SIZE;
import static run.mone.mimeter.dashboard.exception.CommonError.SlaRuleAtLeastOneError;

@Service
public class SlaServiceImpl implements SlaService {

    @Autowired
    SlaMapper slaMapper;

    @Autowired
    SlaRuleMapper slaRuleMapper;

    @Autowired
    private SceneService sceneService;

    @Override
    public Result<Integer> newSla(SlaDto param) {
        Pair<Integer, String> checkRes = checkParam(param);
        if (checkRes.getKey() != 0) {
            return Result.fail(checkRes.getKey(), checkRes.getValue());
        }

        Sla sla = toSla(param);
        slaMapper.insert(sla);
        List<SlaRule> slaRules = toSlaRule(sla.getId(), param.getSlaRuleDtos());
        slaRuleMapper.batchInsert(slaRules);
        return Result.success(sla.getId());
    }

    @Override
    public Result<Boolean> updateSla(SlaDto param) {
        Pair<Integer, String> checkRes = checkParam(param);
        if (checkRes.getKey() != 0) {
            return Result.fail(checkRes.getKey(), checkRes.getValue());
        }

        int slaId = param.getId();
        Sla sla = toSla(param);
        sla.setId(slaId);
        slaMapper.updateByPrimaryKey(sla);
        SlaRuleExample slaRuleExample = new SlaRuleExample();
        slaRuleExample.createCriteria().andSlaIdEqualTo(slaId);
        slaRuleMapper.deleteByExample(slaRuleExample);
        List<SlaRule> slaRules = toSlaRule(slaId, param.getSlaRuleDtos());
        slaRuleMapper.batchInsert(slaRules);
        return Result.success(true);
    }

    @Override
    public Result<SlaList> getSlaList(GetSlaListReq req) {

        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        SlaExample totalSlaExample = new SlaExample();
        SlaExample.Criteria totalCriteria = totalSlaExample.createCriteria();
        SlaExample slaExample = new SlaExample();
        SlaExample.Criteria criteria = slaExample.createCriteria();

        if (StringUtils.isNotEmpty(req.getSlaName())) {
            totalCriteria.andNameLike("%" + req.getSlaName() + "%");
            criteria.andNameLike("%" + req.getSlaName() + "%");
        }
        if (StringUtils.isNotEmpty(req.getCreator())) {
            totalCriteria.andCreatorLike("%" + req.getCreator() + "%");
            criteria.andCreatorLike("%" + req.getCreator() + "%");
        }

        slaExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        SlaList slaList = new SlaList();
        slaList.setPage(req.getPage());
        slaList.setPageSize(req.getPageSize());
        List<Sla> slas = slaMapper.selectByExample(slaExample);
        if (slas == null || slas.size() == 0) {
            return Result.success(slaList);
        }
        List<SlaDto> slaDtos = slas.stream().map(it -> {
            SlaDto slaDto = toSlaDto(it);
            SlaRuleExample slaRuleExample = new SlaRuleExample();
            slaRuleExample.createCriteria().andSlaIdEqualTo(it.getId());
            List<SlaRuleDto> slaRuleDtos = toSlaRuleDto(slaRuleMapper.selectByExample(slaRuleExample));
            slaDto.setSlaRuleDtos(slaRuleDtos);
            return slaDto;
        }).collect(Collectors.toList());
        slaList.setList(slaDtos);
        slaList.setTotal(slaMapper.countByExample(totalSlaExample));

        return Result.success(slaList);
    }

    @Override
    public Result<SlaDto> getSlaById(int id) {
        Sla sla = slaMapper.selectByPrimaryKey(id);
        SlaDto slaDto = toSlaDto(sla);
        SlaRuleExample slaRuleExample = new SlaRuleExample();
        slaRuleExample.createCriteria().andSlaIdEqualTo(id);
        List<SlaRule> slaRules = slaRuleMapper.selectByExample(slaRuleExample);
        List<SlaRuleDto> slaRuleDtos = toSlaRuleDto(slaRules);
        slaDto.setSlaRuleDtos(slaRuleDtos);
        return Result.success(slaDto);
    }

    @Override
    public Result<Boolean> delSla(int id) {
        slaMapper.deleteByPrimaryKey(id);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> multiDelSla(List<Integer> ids) {
        SlaExample slaExample = new SlaExample();
        slaExample.createCriteria().andIdIn(ids);
        slaMapper.deleteByExample(slaExample);
        return Result.success(true);
    }

    private Sla toSla(SlaDto param) {
        Sla sla = new Sla();
        BeanUtils.copyProperties(param, sla);
        long now = System.currentTimeMillis();
        if (sla.getCtime() == null || sla.getCtime() == 0) {
            sla.setCtime(now);
        }
        sla.setUtime(now);
        return sla;
    }

    private SlaDto toSlaDto(Sla sla) {
        SlaDto slaDto = new SlaDto();
        BeanUtils.copyProperties(sla, slaDto);
        return slaDto;
    }

    private List<SlaRule> toSlaRule(int id, List<SlaRuleDto> slaRuleDtoList) {
        if (slaRuleDtoList == null || slaRuleDtoList.size() == 0) {
            return new ArrayList<>();
        }
        long now = System.currentTimeMillis();
        List<SlaRule> slaRules = slaRuleDtoList.stream().map(it -> {
            SlaRule slaRule = new SlaRule();
            BeanUtils.copyProperties(it, slaRule);
            slaRule.setCtime(now);
            slaRule.setSlaId(id);
            slaRule.setUtime(now);
            slaRule.setCompareValue(it.getValue());
            slaRule.setActionLevel(it.getAction());
            slaRule.setCompareCondition(it.getCondition());
            return slaRule;
        }).collect(Collectors.toList());

        return slaRules;
    }

    private List<SlaRuleDto> toSlaRuleDto(List<SlaRule> slaRuleList) {
        if (slaRuleList == null || slaRuleList.size() == 0) {
            return new ArrayList<>();
        }
        List<SlaRuleDto> slaRuleDtos = slaRuleList.stream().map(it -> {
            SlaRuleDto slaRuleDto = new SlaRuleDto();
            BeanUtils.copyProperties(it, slaRuleDto);
            slaRuleDto.setValue(it.getCompareValue());
            slaRuleDto.setAction(it.getActionLevel());
            slaRuleDto.setCondition(it.getCompareCondition());
            return slaRuleDto;
        }).collect(Collectors.toList());

        return slaRuleDtos;
    }

    private Pair<Integer, String> checkParam(SlaDto param) {
        List<SlaRuleDto> slaRuleDtos = param.getSlaRuleDtos();
        if (slaRuleDtos == null || slaRuleDtos.size() == 0) {
            return Pair.of(SlaRuleAtLeastOneError.code, SlaRuleAtLeastOneError.message);
        }

        return Pair.of(0, "success");
    }

}
