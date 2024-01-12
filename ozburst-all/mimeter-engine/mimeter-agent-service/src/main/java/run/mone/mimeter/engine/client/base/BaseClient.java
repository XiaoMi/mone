package run.mone.mimeter.engine.client.base;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.listener.event.EventType;
import lombok.extern.slf4j.Slf4j;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.filter.common.BasePostFilter;
import run.mone.mimeter.engine.filter.common.BasePreFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.common.MimeterFilterInfo;
import run.mone.mimeter.engine.filter.postFilter.IPostFilterChain;
import run.mone.mimeter.engine.filter.postFilter.PostFilterAnno;
import run.mone.mimeter.engine.filter.postFilter.filters.MimeterPostFilter;
import run.mone.mimeter.engine.filter.preFilter.filters.MimeterPreFilter;
import run.mone.mimeter.engine.filter.postFilter.PostFilter;
import run.mone.mimeter.engine.filter.preFilter.IPreFilterChain;
import run.mone.mimeter.engine.filter.preFilter.PreFilter;
import run.mone.mimeter.engine.filter.preFilter.PreFilterAnno;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author dongzhenxing
 */
@Slf4j
@Component
public class BaseClient implements IClient, IPreFilterChain, IPostFilterChain {

    private final CopyOnWriteArrayList<PreFilter> preFilterList = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<PostFilter> postFilterList = new CopyOnWriteArrayList<>();


    private volatile BasePreFilter lastFilter;

    private volatile BasePostFilter firstFilter;

    /**
     * 前置过滤器链是否初始化完成
     */
    private final AtomicBoolean preFilterInit = new AtomicBoolean(false);

    /**
     * 后置过滤器链是否初始化完成
     */
    private final AtomicBoolean postFilterInit = new AtomicBoolean(false);


    @Override
    public Result call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext) {
        return Result.success("ok");
    }

    @Override
    public CommonReqInfo doPreFilter(Task task,TaskContext context, CommonReqInfo reqInfo) {
        if (!preFilterInit.get()) {
            //filter 没加载完成，使用原参数
            return reqInfo;
        }
        try {
            return this.lastFilter.doFilter(task, context,reqInfo);
        } catch (Exception e) {
            log.error("do filter failed:{}", e.getMessage());
            //filter 执行错误，使用原参数
            return reqInfo;
        }
    }

    @Override
    public Object doPostFilter(Task task, Object resInfo) {
        if (!postFilterInit.get()) {
            //filter 没加载完成，返回结果不做处理
            return resInfo;
        }
        try {
            return this.firstFilter.doFilter(task, resInfo);
        } catch (Exception e) {
            log.error("do filter failed:{}", e.getMessage());
            //filter 执行错误，使用原参数
            return resInfo;
        }
    }

    public void init() {
        //加载前置过滤器
        this.loadPreFilter();

        //加载后置过滤器
        this.loadPostFilter();
    }

    private void loadPreFilter() {
        Ioc bizIoc = Ioc.ins().getBean("bizIoc");
        bizIoc.regListener(event -> {
            if (event.getEventType().equals(EventType.initFinish)){
                Map<String, Object> preFilterMaps = bizIoc.getBeansWithAnnotation(PreFilterAnno.class);
                List<Object> preList = new ArrayList<>(preFilterMaps.values());
                log.info("pre filter size:{}", preList.size());
                List<PreFilter> preFilters = preList.stream().map(filter -> (PreFilter) filter).toList();
                preFilters = sortPreFilterList(preFilters);
                preFilterList.addAll(preFilters);

                try {
                    BasePreFilter last = new MimeterPreFilter();
                    int size = preFilters.size();
                    for (int i = 0; i < size; i++) {
                        log.info("init pre filter index:{}", i);
                        PreFilter filter = preFilters.get(i);
                        //最后一个为默认filter
                        BasePreFilter next = last;
                        last = (task,reqInfo,context) -> filter.doFilter(task, reqInfo,context, next);
                    }
                    log.info("init pre filter finish");
                    this.lastFilter = last;
                } finally {
                    log.info("load pre filter end");
                }
                preFilterInit.set(true);
            }
        });
    }

    private void loadPostFilter() {
        Ioc bizIoc = Ioc.ins().getBean("bizIoc");
        bizIoc.regListener(event -> {
            if (event.getEventType().equals(EventType.initFinish)){
                Map<String, Object> postFilterMaps = bizIoc.getBeansWithAnnotation(PostFilterAnno.class);
                List<Object> postList = new ArrayList<>(postFilterMaps.values());
                log.info("post filter size:{}", postList.size());
                List<PostFilter> postFilters = postList.stream().map(filter -> (PostFilter) filter).toList();
                postFilters = sortPostFilterList(postFilters);
                postFilterList.addAll(postFilters);

                try {
                    BasePostFilter last = new MimeterPostFilter();
                    int size = postFilters.size();
                    for (int i = 0; i < size; i++) {
                        log.info("init post filter index:{}", i);
                        PostFilter filter = postFilters.get(i);
                        //最后一个为默认filter
                        BasePostFilter next = last;
                        last = (task, resInfo) -> filter.doFilter(task, resInfo, next);
                    }
                    log.info("init post filter finish");
                    this.firstFilter = last;
                } finally {
                    log.info("load post filter end");
                }
                postFilterInit.set(true);
            }
        });
    }

    /**
     * 前置filter 按order顺序倒序 如：5,4,3,2,1
     */
    private List<PreFilter> sortPreFilterList(List<PreFilter> list) {
        return list.stream().sorted((a, b) -> {
            Integer x = a.getClass().getAnnotation(FilterOrder.class).value();
            Integer y = b.getClass().getAnnotation(FilterOrder.class).value();
            return y.compareTo(x);
        }).collect(Collectors.toList());
    }

    /**
     * 后置filter 按order顺序正序 如：1,2,3,4,5
     */
    private List<PostFilter> sortPostFilterList(List<PostFilter> list) {
        return list.stream().sorted((a, b) -> {
            Integer x = a.getClass().getAnnotation(FilterOrder.class).value();
            Integer y = b.getClass().getAnnotation(FilterOrder.class).value();
            return x.compareTo(y);
        }).collect(Collectors.toList());
    }

    /**
     * 获取前置过滤器列表
     */
    public List<MimeterFilterInfo> getPreFilterInfoList() {
        if (this.preFilterList.size() > 0) {
            return this.preFilterList.stream().map(it -> {
                MimeterFilterInfo info = new MimeterFilterInfo();
                info.setFilterName(it.getClass().getCanonicalName());
                return info;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 获取后置过滤器列表
     */
    public List<MimeterFilterInfo> getPostFilterInfoList() {
        if (this.postFilterList.size() > 0) {
            return this.postFilterList.stream().map(it -> {
                MimeterFilterInfo info = new MimeterFilterInfo();
                info.setFilterName(it.getClass().getCanonicalName());
                return info;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
