package run.mone.m78.service.dao.mapper;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.BaseMapper;
import run.mone.m78.service.dao.entity.M78CodeGenerationInfo;

/**
 *  映射层。
 *
 * @author zhangzhiyong
 * @since 2024-06-12
 */
@UseDataSource("codeStatistics")
public interface M78CodeGenerationInfoMapper extends BaseMapper<M78CodeGenerationInfo> {

}
