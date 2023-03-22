package run.mone.docean.plugin.sidecar.bo;

import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.Address;
import run.mone.api.IClient;
import run.mone.docean.plugin.sidecar.SidecarPlugin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/2/27 17:28
 */
@Slf4j
@Data
public class ProviderMap implements Serializable {

    private int state;

    private ConcurrentHashMap<Address, IClient> clientMap = new ConcurrentHashMap<>();

    private Function function;

    private String name;

    private SidecarPlugin plugin;

    private Task task;

    public void refresh(Function function, String name, SidecarPlugin plugin) {
        this.function = function;
        this.name = name;
        this.plugin = plugin;
        List<Pair> list = (List<Pair>) function.apply(name);
        list.stream().forEach(it -> {
            IClient c = plugin.createTcpClient(it);
            clientMap.put(c.address(), c);
        });
        clientMap.entrySet().stream().forEach(it -> {
            it.getValue().start("");
        });

        this.task = new Task(true);

        Executors.newSingleThreadScheduledExecutor().schedule(this.task, 5, TimeUnit.SECONDS);
    }


    private boolean hasAddress(Pair pair, Map<Address, IClient> map) {
        Address address = new Address();
        address.setIp(pair.getKey().toString());
        address.setPort((int) pair.getValue());
        return map.containsKey(address);
    }

    private IClient getClient(Pair pair, Map<Address, IClient> map) {
        Address address = new Address();
        address.setIp(pair.getKey().toString());
        address.setPort((int) pair.getValue());
        return map.get(address);
    }

    class Task implements Runnable {

        private boolean schedule = true;

        public Task(boolean schedule) {
            this.schedule = schedule;
        }

        public synchronized void runOnce() {
            Safe.runAndLog(() -> {
                Set<Pair> set = new HashSet<>((List<Pair>) function.apply(name));
                log.info("{} sidecar num:{} old num:{}", name, set.size(), clientMap.size());

                Set<Pair> add = new HashSet<>();
                Set<Pair> remove = new HashSet<>();

                //查询的列表里有,但我没有
                add.addAll(set.stream().filter(it -> !hasAddress(it, clientMap)).collect(Collectors.toSet()));
                //我的列表里有,但查询的里没有
                remove.addAll(clientMap.entrySet().stream().map(it -> it.getKey()).map(it -> Pair.of(it.getIp(), it.getPort())).filter(it -> !set.contains(it)).collect(Collectors.toSet()));

                if (add.size() > 0) {
                    add.stream().forEach(it -> {
                        log.info("add client:{}", it);
                        IClient client = plugin.createTcpClient(it);
                        clientMap.put(client.address(), client);
                    });
                }

                if (remove.size() > 0) {
                    remove.stream().forEach(it -> {
                        log.info("remove client:{}", it);
                        IClient client = getClient(it, clientMap);
                        client.shutdown();
                        clientMap.remove(client.address());
                    });
                }

            });
        }

        @Override
        public void run() {
            runOnce();
            if (schedule) {
                Executors.newSingleThreadScheduledExecutor().schedule(this, 5, TimeUnit.SECONDS);
            }
        }
    }

}
