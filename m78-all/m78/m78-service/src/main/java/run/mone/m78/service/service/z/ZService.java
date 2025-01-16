package run.mone.m78.service.service.z;

import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import run.mone.ai.z.dto.PageReq;
import run.mone.ai.z.dto.ZDataBaseListReq;
import run.mone.ai.z.dto.ZDataBaseSimple;
import run.mone.ai.z.service.ZDubboService;

import java.util.ArrayList;
import java.util.List;

@Service

/**
 * ZService类是一个Spring服务类，提供与ZDubboService交互的功能。
 * 该类通过Dubbo引用了ZDubboService，并提供了以下主要功能：
 *
 * 1. 获取提示名称列表：根据分页请求参数和认证令牌，返回提示名称的列表。
 * 2. 根据名称和令牌获取提示信息：根据提供的名称和令牌，返回对应的提示信息。
 * 3. 根据zToken获取用户名：根据用户的令牌，返回对应的用户名。
 *
 * 该类的主要职责是封装对ZDubboService的调用，并处理返回结果。
 */

public class ZService {
    @DubboReference(check = false, group = "${ref.ai.z.service.group}", interfaceClass = ZDubboService.class, version = "${ref.ai.z.service.version}")
    private ZDubboService zDubboService;

    /**
     * 获取提示名称列表
     *
     * @param req    分页请求参数
     * @param zToken 认证令牌
     * @return 提示名称列表，如果请求失败则返回空列表
     */
    public List<String> getPromptNames(PageReq req, String zToken) {
        ZDataBaseListReq param = new ZDataBaseListReq();
        param.setType(64);
        param.setPageNum(req.getPageNum());
        param.setPageSize(req.getPageSize());
        param.setToken(zToken);
        List<String> ret = new ArrayList<>();
        Result<List<ZDataBaseSimple>> res = zDubboService.listWithPermission(param);
        if (res.getCode() != 0) {
            return ret;
        }
        res.getData().forEach(it -> {
            ret.add(it.getName());
        });
        return ret;
    }

    /**
     * 根据名称和令牌获取提示信息
     *
     * @param name   名称
     * @param zToken 令牌
     * @return 对应名称的提示信息，如果未找到或发生错误则返回空字符串
     */
    public String getPromptByName(String name, String zToken) {
        ZDataBaseListReq param = new ZDataBaseListReq();
        param.setType(64);
        param.setName(name);
        param.setToken(zToken);
        Result<List<ZDataBaseSimple>> res = zDubboService.listWithPermission(param);
        if (res.getCode() != 0) {
            return "";
        }
        String data = "";
        for (ZDataBaseSimple it : res.getData()) {
            if (name.equals(it.getName())) {
                data = it.getData();
                break;
            }
        }
        return data;
    }

    /**
     * 根据zToken获取用户名
     *
     * @param zToken 用户的令牌
     * @return 用户名
     */
    public String getUserName(String zToken) {
        return zDubboService.getUserName(zToken);
    }


}
