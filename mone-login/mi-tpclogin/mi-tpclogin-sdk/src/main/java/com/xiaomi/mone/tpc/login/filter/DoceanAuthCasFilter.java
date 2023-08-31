package com.xiaomi.mone.tpc.login.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.CommonUtil;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.util.SignUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.UserInfoVO;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 10:08
 */
public class DoceanAuthCasFilter extends DoceanFilter {

    private static final Logger log = LoggerFactory.getLogger(DoceanAuthCasFilter.class);
    private String[] publicKeys = null;
    private String[] ignoreUrls;

    @Override
    public void init(Map<String, String> filterConfig) {
        String publicKeyStr = filterConfig.get(ConstUtil.PUBLIC_KEY_FILTER_INIT_PARAM_KEY);
        if (StringUtils.isBlank(publicKeyStr)) {
            throw new IllegalArgumentException("CAS_PUBLIC_KEY must config");
        }
        publicKeys = publicKeyStr.split("[,|，]");

        String ignoreUrl = filterConfig.get(ConstUtil.ignoreUrl);
        if (StringUtils.isNotBlank(ignoreUrl)) {
            ignoreUrls = ignoreUrl.split("[,|，]");
            log.info("已设置忽略路径，ignoreUrls:{}", ignoreUrl);
        }
    }

    @Override
    public boolean doFilter(MvcContext mvcContext) {
        try {
            String url = mvcContext.getPath();
            log.info("AuthCasFilter begin filter url:{}", url);
            if (CommonUtil.isIgnoreUrl(ignoreUrls, url)) {
                log.info("配置为忽略的路径,url:{}", url);
                return true;
            }
            String verifyIdentitySignData = mvcContext.getHeaders().get(ConstUtil.HEADER_KEY_SIGN_VERIFY_IDENTITY);
            if (StringUtils.isEmpty(verifyIdentitySignData)) {
                log.error("没有标识身份的签名数据,url:{}", url);
                noAuthResponse(mvcContext);
                return false;
            }
            String currentUsePublicKey = null;
            String verifyIdentityData = null;
            for (String key : publicKeys) {
                verifyIdentityData = SignUtil.verifySignGetInfo(verifyIdentitySignData, key);
                if (StringUtils.isNotEmpty(verifyIdentityData)) {
                    currentUsePublicKey = key;
                    break;
                }
            }
            if (StringUtils.isEmpty(verifyIdentityData)) {
                log.error("检测身份,验签失败,url:{},signData:{}", url, verifyIdentitySignData);
                noAuthResponse(mvcContext);
                return false;
            }
            log.info("账号登录,url:{}", url);
            String signAndUserSignData = mvcContext.getHeaders().get(ConstUtil.HEADER_KEY_SIGN_AND_USER_DATA);
            //没有签名信息不做验签
            if (StringUtils.isEmpty(signAndUserSignData)) {
                log.info("确认请求，没有签名用户数据(bypass|静态资源)，url:{}", url);
                return true;
            }
            //验签，获取用户数据
            String userJson = SignUtil.verifySignGetInfo(signAndUserSignData, currentUsePublicKey);
            //验签失败
            if (StringUtils.isEmpty(userJson)) {
                log.error("获取用户数据，验签失败,url:{},signData:{}", url, signAndUserSignData);
                noAuthResponse(mvcContext);
                return false;
            }
            Gson gson = new GsonBuilder().serializeNulls().create();
            UserInfoVO userInfo = gson.fromJson(userJson, UserInfoVO.class);
            AuthUserVo authUserVo = new AuthUserVo();
            authUserVo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            authUserVo.setAccount(userInfo.getUser());
            authUserVo.setName(userInfo.getDisplayName());
            authUserVo.setEmail(userInfo.getEmail());
            authUserVo.setAvatarUrl(userInfo.getAvatar());
            authUserVo.setCasUid(userInfo.getuID());
            authUserVo.setDepartmentName(userInfo.getDepartmentName());
            mvcContext.session().setAttribute(ConstUtil.TPC_USER, authUserVo);
            return true;
        } catch (Throwable e) {
            log.error("AuthCasFilter check exception", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 无权限
     * @param mvcContext
     */
    private void noAuthResponse(MvcContext mvcContext) {
        mvcContext.getResHeaders().put("x-status", "401");
    }

    @Override
    public void destroy() {
        log.info("AuthCasFilter destroy finish..");
    }

}
