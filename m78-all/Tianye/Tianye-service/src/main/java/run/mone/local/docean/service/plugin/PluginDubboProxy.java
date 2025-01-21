package run.mone.local.docean.service.plugin;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.SyncFlowStatus;
import run.mone.local.docean.fsm.flow.PluginFlow;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.TemplateUtils;
import run.mone.m78.api.bo.plugins.BotPluginDTO;

import java.util.HashMap;
import java.util.Map;


/**
 * @author wmin
 * @date 2024/8/7
 */
@Slf4j
public class PluginDubboProxy {

    private PluginFlow pluginFlow;

    public PluginDubboProxy(PluginFlow pluginFlow) {
        this.pluginFlow = pluginFlow;
    }

    public String proxy(BotPluginDTO pluginInfo, FlowReq req, FlowContext context) {
        BotPluginDTO.BotPluginMeta meta = pluginInfo.getBotPluginMeta();
        String key = ReferenceConfigCache.getKey(meta.getDubboServiceName(), meta.getDubboServiceGroup(), meta.getDubboServiceVersion());
        GenericService genericService = ReferenceConfigCache.getCache().get(key);
        if (null == genericService) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            ApplicationConfig applicationConfig = Ioc.ins().getBean(ApplicationConfig.class);
            RegistryConfig registryConfig = Ioc.ins().getBean(RegistryConfig.class);
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(meta.getDubboServiceName());
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setGroup(meta.getDubboServiceGroup());
            reference.setVersion(meta.getDubboServiceVersion());
            reference.setTimeout(meta.getTimeout());
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
        }
        String timeout = pluginFlow.getValueFromInputMapWithDefault(CommonConstants.TY_PLUGIN_DUBBO_TIMEOUT_MARK, "3000", String.class);
        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, timeout);
        if (meta.getRpcContext() != null && meta.getRpcContext().size() > 0) {
            meta.getRpcContext().entrySet().stream().forEach(entry -> RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue()));
        }
        Object res = genericService.$invoke(meta.getDubboMethodName(),
                meta.getDubboMethodParamtypes().toArray(new String[0]),
                parseAndRenderPluginParameters(req, context));

        return GsonUtils.gson.toJson(res);
    }

    private Object[] parseAndRenderPluginParameters(FlowReq req, FlowContext context) {
        String paramsStr = pluginFlow.getValueFromInputMapWithDefault(CommonConstants.TY_PLUGIN_DUBBO_PARAMS_MARK, "[]", String.class);
        Map<String, String> m = new HashMap<>();
        pluginFlow.getInputMap().entrySet().stream()
                .filter(e -> e.getValue().isOriginalInput() && !CommonConstants.TY_PLUGIN_DUBBO_PARAMS_MARK.equals(e.getKey()))
                .forEach(it -> {
                    m.put(it.getKey(), it.getValue().getValue().toString());
                });
        String finalParamsStr = TemplateUtils.renderTemplate(paramsStr, m);
        log.info("parseAndRenderPluginParameters : {}", finalParamsStr);

        //pluginFlow.getInputMap().get(CommonConstants.TY_PLUGIN_DUBBO_PARAMS_MARK).setValue(new JsonPrimitive(finalParamsStr));
        addSyncFlowStatus(req, context, finalParamsStr);
        return GsonUtils.gson.fromJson(finalParamsStr, new TypeToken<Object[]>() {
        }.getType());
    }


    private void addSyncFlowStatus(FlowReq req, FlowContext context, String finalParamsStr) {
        if (req.isSyncFlowStatusToM78()) {
            SyncFlowStatus.SyncNodeInput syncNodeInput = (SyncFlowStatus.SyncNodeInput) context.getFlowRes().getInputs().get(pluginFlow.getId());
            if (syncNodeInput != null) {
                syncNodeInput.getInputDetails().forEach(detail -> {
                    if (CommonConstants.TY_PLUGIN_DUBBO_PARAMS_MARK.equals(detail.getName())) {
                        detail.setValue(finalParamsStr);
                    }
                });
            }
            pluginFlow.getSyncFlowStatusServices().addSyncFlowStatusMap(pluginFlow.getFlowRecordId(), syncNodeInput, null, false);
        }
    }


}
