package run.mone.docean.plugin.k8s;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;

public abstract class ReconcileResourceEventHandler<T extends HasMetadata> implements ResourceEventHandler<T> {
    @Override
    public void onAdd(T t) {
        if (!addPredicated(t) || !predicated(t)) {
            return;
        }
        this.reconcile(t.getMetadata());
    }

    @Override
    public void onDelete(T t, boolean b) {
        if (!deletePredicated(t) || !predicated(t)) {
            return;
        }
        this.reconcile(t.getMetadata());
    }

    @Override
    public void onUpdate(T t, T t1) {
        if (!updatePredicated(t, t1) || !predicated(t1)) {
            return;
        }
        this.reconcile(t1.getMetadata());
    }

    abstract public void reconcile(ObjectMeta meta);

    public boolean predicated(T t) {
        return true;
    }

    public boolean updatePredicated(T oldObj, T newObj) {
        return true;
    }

    public boolean addPredicated(T t) {
        return true;
    }

    public boolean deletePredicated(T t) {
        return true;
    }
}
