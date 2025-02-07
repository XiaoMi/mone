package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.m78.service.dao.entity.M78FileManagement;
import run.mone.m78.service.service.fileserver.store.M78FileManagementService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author zhangzhiyong
 * @since 2024-08-09
 */
@RestController
@RequestMapping("/m78FileManagement")
public class M78FileManagementController {

    @Autowired
    private M78FileManagementService m78FileManagementService;

    /**
     * 添加。
     *
     * @param m78FileManagement 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody M78FileManagement m78FileManagement) {
        return m78FileManagementService.save(m78FileManagement);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return m78FileManagementService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param m78FileManagement 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody M78FileManagement m78FileManagement) {
        return m78FileManagementService.updateById(m78FileManagement);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<M78FileManagement> list() {
        return m78FileManagementService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public M78FileManagement getInfo(@PathVariable Serializable id) {
        return m78FileManagementService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<M78FileManagement> page(Page<M78FileManagement> page) {
        return m78FileManagementService.page(page);
    }

}
