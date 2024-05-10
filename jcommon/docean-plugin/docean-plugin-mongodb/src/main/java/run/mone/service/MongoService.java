package run.mone.service;

import com.xiaomi.youpin.docean.anno.Service;
import dev.morphia.Datastore;
import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import run.mone.bo.MongoBo;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 */
@Service
@Data
@Slf4j
public class MongoService<T extends MongoBo> {

    @Resource
    private Datastore datastore;

    private Class<T> clazz;

    public MongoService() {
    }

    public MongoService(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T findFirst() {
        return datastore.find(clazz).first();
    }

    public T findFirst(Filter filter) {
        return datastore.find(this.clazz).filter(filter).first();
    }

    public long count() {
        return datastore.find(this.clazz).count();
    }

    public long count(Filter filter) {
        return datastore.find(this.clazz).filter(filter).count();
    }

    //实现findById,返回Document
    public T findById(String id) {
        return datastore.find(this.clazz).filter(Filters.eq("_id", id)).first();
    }

    public T find(Document nativeQuery) {
        return datastore.find(this.clazz, nativeQuery).first();
    }

    public List<T> findAll(Filter filter) {
        return datastore.find(this.clazz).filter(filter).iterator().toList();
    }

    public boolean delete(T t) {
        datastore.delete(t);
        return true;
    }

    public boolean deleteById(String id) {
        return datastore.find(this.clazz).filter(Filters.eq("_id", id)).delete().getDeletedCount() == 1;
    }

    public boolean delete(Filter filter) {
        datastore.find(this.clazz).filter(filter).delete();
        return true;
    }

    public boolean update(T t) {
        t.setUtime(System.currentTimeMillis());
        datastore.merge(t);
        return true;
    }

    public boolean update(String id, UpdateOperator... updateOperators) {
        return datastore.find(this.clazz).filter(Filters.eq("_id", id)).update(new UpdateOptions(), updateOperators).getModifiedCount() > 0;
    }

    public boolean updateWithVersion(String id, Consumer<T> consumer) {
        for (; ; ) {
            T data = this.findById(id);
            if (null == data) {
                return false;
            }
            int version = data.getVersion();
            consumer.accept(data);
            data.setVersion(version + 1);
            UpdateOperator setUpdateOperator = UpdateOperators.set(data);
            boolean b = datastore.find(this.clazz).filter(Filters.and(Filters.eq("_id", id), Filters.eq("version", version)))
                    .update(new UpdateOptions(), setUpdateOperator).getModifiedCount() > 0;
            if (b) {
                break;
            } else {
                log.info("retry:{}", id);
            }
        }
        return false;
    }

    public boolean save(T t) {
        datastore.insert(t);
        return true;
    }

}
