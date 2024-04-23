package run.mone.controller;

import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import run.mone.auth.Auth;
import run.mone.bo.MongoBo;
import run.mone.bo.Page;
import run.mone.bo.User;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/22 17:10
 */
public class MongodbController<T extends MongoBo> {

    @Resource
    protected Datastore datastore;

    private Class<T> clazz;

    public MongodbController(Class<T> clazz) {
        this.clazz = clazz;
    }

    //查询一条记录
    //{"name":"$eq","field":"name","value":"bbb"}
    @Auth
    @RequestMapping(path = "/one", method = "get")
    public T one(Filter filter) {
        return this.datastore.find(this.clazz).filter(filter).first();
    }

    @Auth
    @RequestMapping(path = "/getById", method = "get")
    public T getById(@RequestParam("id") String id) {
        return datastore.find(this.clazz).filter(Filters.eq("id", id)).first();
    }

    //按id删除(class)
    @Auth
    @RequestMapping(path = "/deleteById", method = "get")
    public boolean deleteById(@RequestParam("id") String id) {
        this.datastore.find(this.clazz).filter(Filters.eq("id", id)).delete();
        return true;
    }

    @Auth(role = "user")
    @RequestMapping(path = "/deleteByIdAndUid", method = "get")
    public boolean deleteByIdAndUid(@RequestParam("id") String id) {
        User user = getCurrentUser();
        this.datastore.find(this.clazz).filter(Filters.and(Filters.eq("id", id), Filters.eq("uid", user.getId()))).delete();
        return true;
    }

    public User getCurrentUser() {
        MvcContext context = ContextHolder.getContext().get();
        User user = (User) context.session().getAttribute("user");
        if (null == user) {
            throw new RuntimeException("user is null");
        }
        return user;
    }

    //查询所有记录
    @Auth
    @RequestMapping(path = "/all", method = "get")
    public List<T> all() {
        return this.datastore.find(this.clazz).filter(Filters.eq("state", 0)).iterator().toList();
    }

    //按filter条件搜索
    @Auth
    @RequestMapping(path = "/search")
    public List<T> search(Filter filter) {
        return this.datastore.find(this.clazz).filter(filter).iterator().toList();
    }

    //按filter和uid搜索
    @Auth(role = "user")
    @RequestMapping(path = "/searchByFilterAndUid")
    public List<T> searchWithUid(Filter filter) {
        User user = getCurrentUser();
        return this.datastore.find(this.clazz)
                .filter(Filters.and(filter, Filters.eq("uid", user.getId())))
                .iterator()
                .toList();
    }

    //带分页的search(class)
    @Auth
    @RequestMapping(path = "/searchWithPaging")
    public Page<T> searchWithPaging(Filter filter, int page, int size) {
        Query<T> query = this.datastore.find(this.clazz).filter(filter);
        List<T> list = query.iterator(new FindOptions().skip(page * size).limit(size)).toList();
        long total = query.count();
        return new Page<>(list, page, size, total);
    }


    //删除
    @Auth
    @RequestMapping(path = "/delete")
    public boolean delete(T t) {
        this.datastore.delete(t);
        return true;
    }

    //删除
    @Auth(role = "user")
    @RequestMapping(path = "/delete")
    public boolean deleteWithUid(T t) {
        User user = getCurrentUser();
        t.setUid(user.getUid());
        this.datastore.delete(t);
        return true;
    }

    //更新
    @Auth
    @RequestMapping(path = "/update")
    public boolean update(T t) {
        t.setUtime(System.currentTimeMillis());
        this.datastore.merge(t);
        return true;
    }

    //更新
    @Auth(role = "user")
    @RequestMapping(path = "/update")
    public boolean updateWithUid(T t) {
        User user = getCurrentUser();
        t.setUtime(System.currentTimeMillis());
        t.setUid(user.getUid());
        this.datastore.merge(t);
        return true;
    }

    //添加
    @Auth
    @RequestMapping(path = "/add")
    public boolean add(T t) {
        long now = System.currentTimeMillis();
        t.setState(0);
        t.setCtime(now);
        t.setUtime(now);
        this.datastore.insert(t);
        return true;
    }

    @Auth(role = "user")
    @RequestMapping(path = "/add")
    public boolean addWithUid(T t) {
        User user = getCurrentUser();
        long now = System.currentTimeMillis();
        t.setState(0);
        t.setUid(user.getUid());
        t.setCtime(now);
        t.setUtime(now);
        this.datastore.insert(t);
        return true;
    }


    //添加并返回
    @Auth
    @RequestMapping(path = "/addAndReturnDetail")
    public T addAndReturnDetail(T t) {
        long now = System.currentTimeMillis();
        t.setState(0);
        t.setCtime(now);
        t.setUtime(now);
        this.datastore.insert(t);
        return t;
    }


}
