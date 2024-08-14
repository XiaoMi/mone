package run.mone.ultraman.event;

import com.google.common.eventbus.Subscribe;
import com.xiaomi.youpin.tesla.ip.bo.ClassInfo;
import com.xiaomi.youpin.tesla.ip.bo.ValueInfo;
import lombok.Getter;
import run.mone.ultraman.bo.PackageInfo;
import run.mone.ultraman.bo.ParamsInfo;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/6/24 10:49
 */
public class EventListener {

    @Getter
    private Consumer<PackageInfo> packageConsumer;

    @Getter
    private Consumer<ClassInfo> classConsumer;

    @Getter
    private Consumer<ValueInfo> valueInfoConsumer;

    @Getter
    private Consumer<ParamsInfo> paramsInfoConsumer;

    @Subscribe
    public void listenSelectPackage(PackageInfo packageInfo) {
        if (null != packageConsumer) {
            packageConsumer.accept(packageInfo);
            this.packageConsumer = null;
        }
    }

    @Subscribe
    public void listenParamsInfo(ParamsInfo paramsInfo) {
        if (null != paramsInfoConsumer) {
            paramsInfoConsumer.accept(paramsInfo);
            this.paramsInfoConsumer = null;
        }
    }

    @Subscribe
    public void listenSelectClass(ClassInfo ci) {
        if (null != classConsumer) {
            classConsumer.accept(ci);
            this.classConsumer = null;
        }
    }

    @Subscribe
    public void listenValueInfo(ValueInfo ci) {
        if (null != valueInfoConsumer) {
            valueInfoConsumer.accept(ci);
            this.valueInfoConsumer = null;
        }
    }

    @Subscribe
    public void listenConsumer(ConsumerBo bo) {
        if ("params".equals(bo.getType())) {
            this.paramsInfoConsumer = bo.getConsumer();
        } else if ("class".equals(bo.getType())) {
            this.classConsumer = bo.getConsumer();
        } else if ("value".equals(bo.getType())) {
            this.valueInfoConsumer = bo.getConsumer();
        } else {
            this.packageConsumer = bo.getConsumer();
        }
    }


}
