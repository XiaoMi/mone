package run.mone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import run.mone.bo.User;
import run.mone.vo.UserVo;

/**
 * @author goodjava@qq.com
 * @date 2024/4/23 16:59
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserVo userToUserVo(User user);

}
