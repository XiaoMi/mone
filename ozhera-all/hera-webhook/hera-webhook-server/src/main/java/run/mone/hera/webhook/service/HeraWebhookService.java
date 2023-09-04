package run.mone.hera.webhook.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarSource;
import io.fabric8.kubernetes.api.model.ObjectFieldSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hera.webhook.domain.JsonPatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/10 11:27 AM
 */
@Service
@Slf4j
public class HeraWebhookService {

    private static final String HOST_IP = "host.ip";
    private static final String NODE_IP = "node.ip";
    private static final String MIONE_PROJECT_ENV_NAME = "MIONE_PROJECT_ENV_NAME";
    private static final String MIONE_PROJECT_NAME = "MIONE_PROJECT_NAME";
    // cad need env
    // "-" replace of "_"
    private static final String APPLICATION = "APPLICATION";
    private static final String SERVER_ENV = "serverEnv";

    public List<JsonPatch> setPodEnv(JSONObject admissionRequest) {
        String operation = admissionRequest.getString("operation");
        switch (operation) {
            case "CREATE":
            case "UPDATE":
                List<JsonPatch> result = new ArrayList<>();
                JSONArray containersJson = admissionRequest.getJSONObject("object").getJSONObject("spec").getJSONArray("containers");
                for (int i = 0; i < containersJson.size(); i++) {
                    JSONObject container = containersJson.getJSONObject(i);
                    if (container != null) {
                        JSONArray env = container.getJSONArray("env");
                        // don't have env element
                        if (env == null) {
                            String path = "/spec/containers/" + i + "/env";
                            List<EnvVar> envs = new ArrayList<>();
                            envs.add(createPodIdEnv());
                            envs.add(createNodeIdEnv());
                            result.add(new JsonPatch("add", path, envs));
                        } else {
                            Set<String> envKeys = envKeys(env);
                            String path = "/spec/containers/" + i + "/env/-";
                            if (!envKeys.contains(HOST_IP)) {
                                result.add(new JsonPatch("add", path, createPodIdEnv()));
                            }
                            if (!envKeys.contains(NODE_IP)) {
                                result.add(new JsonPatch("add", path, createNodeIdEnv()));
                            }
                            //set cadvisor need env
                            if (env != null && env.size() > 0) {
                                for (int j = 0; j < env.size(); j++) {
                                    JSONObject envJson = env.getJSONObject(j);
                                    String envKey = envJson.getString("name");
                                    String envValue = envJson.getString("value");
                                    if (!envKeys.contains(APPLICATION) && MIONE_PROJECT_NAME.equals(envKey)) {
                                        result.add(new JsonPatch("add", path, buildEnv(APPLICATION, envValue.replaceAll("-", "_"))));
                                    }
                                    if (!envKeys.contains(SERVER_ENV) && MIONE_PROJECT_ENV_NAME.equals(envKey)) {
                                        result.add(new JsonPatch("add", path, buildEnv(SERVER_ENV, envValue)));
                                    }
                                }
                            }
                        }
                    }
                }
                return result;
            default:
                return null;
        }
    }

    private EnvVar createPodIdEnv(){
        return buildEnvRef(HOST_IP, "v1", "status.podIP");
    }
    private EnvVar createNodeIdEnv(){
        return buildEnvRef(NODE_IP, "v1", "status.hostIP");
    }

    private EnvVar buildEnv(String key, String value) {
        EnvVar env = new EnvVar();
        env.setName(key);
        env.setValue(value);
        return env;
    }

    private EnvVar buildEnvRef(String key, String apiVersion, String fieldPath) {
        EnvVar env = new EnvVar();
        env.setName(key);
        EnvVarSource envVarSource = new EnvVarSource();
        ObjectFieldSelector objectFieldSelector = new ObjectFieldSelector();
        objectFieldSelector.setApiVersion(apiVersion);
        objectFieldSelector.setFieldPath(fieldPath);
        envVarSource.setFieldRef(objectFieldSelector);
        env.setValueFrom(envVarSource);
        return env;
    }

    private Set<String> envKeys(JSONArray envs) {
        Set<String> keySet = new HashSet<>();
        if (envs != null && envs.size() > 0) {
            for (int i = 0; i < envs.size(); i++) {
                keySet.add(envs.getJSONObject(i).getString("name"));
            }
        }
        return keySet;
    }

}
