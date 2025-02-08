package run.mone.m78.service.service.categoty;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import run.mone.m78.service.bo.category.CategoryVo;
import run.mone.m78.service.dao.entity.M78Category;
import run.mone.m78.service.dao.mapper.M78CategoryMapper;

import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-02 09:47
 */
@Service
public class CategoryService extends ServiceImpl<M78CategoryMapper, M78Category> {

    /**
     * 删除指定ID的分类
     *
     * @param categoryId 分类的ID
     * @return 如果删除成功返回true，否则返回false
     */
	public boolean deleteCategory(Long categoryId) {
        M78Category category = M78Category.builder()
                .id(categoryId).deleted(1).build();
        return super.updateById(category);
    }

    /**
     * 获取分类列表
     *
     * @param type 分类类型，如果为null则返回所有未删除的分类
     * @return 分类列表，包含未删除的分类信息
     */
	public List<CategoryVo> listCategory(Integer type) {
        if (type == null) {
            return super.list(QueryWrapper.create().eq("deleted", 0)).stream().map(c -> new CategoryVo(c.getId(), c.getName(), c.getType())).toList();
        }
        return super.list(QueryWrapper.create().eq("deleted", 0).eq("type", type)).stream().map(c -> new CategoryVo(c.getId(), c.getName(), c.getType())).toList();
    }



}
