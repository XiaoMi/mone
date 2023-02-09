package run.mone.docean.plugin.k8s.test.bo;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("run.mone.k8s")
public class Mone extends CustomResource<MoneSpec, MoneStatus> implements Namespaced {
}
