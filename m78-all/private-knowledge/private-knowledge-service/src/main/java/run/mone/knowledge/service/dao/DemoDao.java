package run.mone.knowledge.service.dao;

import run.mone.knowledge.service.dao.entity.DemoEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DemoDao {

    public DemoEntity getById(Long id) {
        return DemoEntity.build();
    }

}
