package run.mone.m78.service.service.code;

import com.google.gson.JsonObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import run.mone.m78.api.CodeProvider;
import run.mone.m78.api.bo.code.CodeDTO;
import run.mone.m78.api.bo.code.ReqCodeListDto;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.api.constant.PromptConstant;
import run.mone.m78.service.bo.code.Code;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.MappingUtils;
import run.mone.m78.service.dao.entity.M78Code;
import run.mone.m78.service.dao.entity.table.M78CodeTableDef;
import run.mone.m78.service.dao.mapper.M78CodeMapper;
import run.mone.m78.service.service.base.ChatgptService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author goodjava@qq.com
 * @date 2024/3/9 21:33
 */
@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class M78CodeService extends ServiceImpl<M78CodeMapper, M78Code> implements CodeProvider {

    @Resource
    private ChatgptService chatgptService;

    //根据提示词生成代码(访问chatgpt),prompt(String)是参数(class)
    public Result<M78Code> generateCodeByPrompt(String comment, String model, String creator, Boolean saveDB) {
        Map<String, String> params = new HashMap<>();
        params.put("comment", comment);
        params.put("context", "");
        JsonObject result = chatgptService.callWithModel(PromptConstant.PROMPT_CODE_GENERATE_GROOVY_METHOD, params, model);
        Code code = GsonUtils.gson.fromJson(result, Code.class);

        long now = System.currentTimeMillis();
        M78Code m78Code = M78Code.builder().model(model).code(code).ctime(now).utime(now).type(0).creator(creator).name(comment).desc(comment).build();
        if (BooleanUtils.isTrue(saveDB)){
            this.createM78Code(m78Code);
        }
        return Result.success(m78Code);
    }

    //根据代码生成这个代码的简单描述(问chatgpt)(class)
    public Result<String> generateSimpleDescription(String code) {
        String key = "comment";
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        Result<String> result = chatgptService.call(PromptConstant.PROMPT_CODE_GENERATE_GROOVY_COMMENT, params, key);
        if (result.getCode() != 0) {
            return Result.fail(STATUS_INTERNAL_ERROR, result.getMessage());
        }
        return Result.success(result.getData());
    }

    //删除指定id的代码,参数有id和creator(class)
    public Result<Boolean> deleteCodeByIdAndCreator(Long id, String creator) {
        return Result.success(this.remove(M78CodeTableDef.M78_CODE.ID.eq(id).and(M78CodeTableDef.M78_CODE.CREATOR.eq(creator))));
    }

    //更新m78_code(class)
    public Result<Boolean> updateM78Code(M78Code m78Code) {
        if (m78Code == null || m78Code.getId() == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid M78 code or ID");
        }
        boolean updated = this.update(m78Code, M78CodeTableDef.M78_CODE.ID.eq(m78Code.getId()).and(M78CodeTableDef.M78_CODE.CREATOR.eq(m78Code.getCreator())));
        if (!updated) {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to update M78 code");
        }
        return Result.success(true);
    }

    //根据id和creator获取M78Code(class)
    public Result<M78Code> getM78CodeByIdAndCreator(Long id, String creator) {
        if (id == null || creator == null || creator.trim().isEmpty()) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid ID or creator");
        }
        M78Code m78Code = this.getOne(M78CodeTableDef.M78_CODE.ID.eq(id).and(M78CodeTableDef.M78_CODE.CREATOR.eq(creator)));
        if (m78Code == null) {
            return Result.fail(STATUS_NOT_FOUND, "M78 code not found");
        }
        return Result.success(m78Code);
    }

    //新建M78Code,参数就是M78Code(class)
    public Result<Boolean> createM78Code(M78Code m78Code) {
        if (m78Code == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "M78 code cannot be null");
        }
        boolean created = this.save(m78Code);
        if (!created) {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to create new M78 code");
        }
        return Result.success(true);
    }

    public Page<CodeDTO> listCode(ReqCodeListDto reqCodeListDto, String userName) {
        QueryWrapper wrapper = QueryWrapper.create();
        Page<CodeDTO> res = new Page<>();

        if (reqCodeListDto.getType() != null) {
            wrapper = wrapper.eq("type", reqCodeListDto.getType());
        }
        if (StringUtils.isNotEmpty(reqCodeListDto.getName())) {
            wrapper = wrapper.like("name", reqCodeListDto.getName());
        }
        wrapper = wrapper.eq("creator", userName);

        Page<M78Code> page = super.page(Page.of(reqCodeListDto.getPageNum(), reqCodeListDto.getPageSize()), wrapper);
        List<CodeDTO> boList = page.getRecords().stream().map(it -> MappingUtils.map(it, CodeDTO.class)).toList();

        res.setRecords(boList);
        res.setPageNumber(page.getPageNumber());
        res.setPageSize(page.getPageSize());
        res.setTotalPage(page.getTotalPage());
        res.setTotalRow(page.getTotalRow());
        return res;

    }



    @Override
    public Result<CodeDTO> getCodeDetailById(Long id) {
        log.info("try to get code detail by id:{}", id);
        if (id == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid ID!");
        }
        M78Code m78Code = this.getOne(M78CodeTableDef.M78_CODE.ID.eq(id));
        log.info("getting m78Code:{}", m78Code);
        if (m78Code == null) {
            return Result.fail(STATUS_NOT_FOUND, "M78 code not found");
        }
        CodeDTO code = MappingUtils.map(m78Code, CodeDTO.class);
        return Result.success(code);
    }

    public Result<List<M78Code>> getM78CodeByCreator(String userName) {
        if (StringUtils.isBlank(userName)) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid userName!");
        }
        List<M78Code> list = this.list(M78CodeTableDef.M78_CODE.CREATOR.eq(userName));
        if (list == null) {
            return Result.fail(STATUS_NOT_FOUND, "M78 code not found");
        }
        return Result.success(list);
    }
}
