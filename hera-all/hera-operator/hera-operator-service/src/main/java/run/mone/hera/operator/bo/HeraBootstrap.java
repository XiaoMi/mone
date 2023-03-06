package run.mone.hera.operator.bo;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1")
@Group("k8s.mone.run")
@Plural("heras")
//@Kind("HeraBootstrap")
public class HeraBootstrap extends CustomResource<HeraSpec, HeraStatus> implements Namespaced {
}
