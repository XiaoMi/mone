package run.mone.m78.service.service.api;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78ApiKey;
import run.mone.m78.service.dao.mapper.M78ApiKeyMapper;
import run.mone.m78.service.dto.ApiKeyDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

/**
 * ApiKeyService类提供了对API密钥的创建、删除和查询操作。
 * 该类使用了M78ApiKeyMapper进行数据库操作，支持通过API密钥、类型ID和类型进行查询。
 * 主要功能包括：
 * - 创建新的API密钥
 * - 通过主键ID删除API密钥
 * - 根据API密钥查询对应的API密钥列表
 * - 根据类型ID和类型查询API密钥列表
 *
 * 该类使用了Spring的@Service注解标识为服务类，并通过@Autowired注解注入M78ApiKeyMapper。
 */

public class ApiKeyService {

    @Autowired
    private M78ApiKeyMapper m78ApiKeyMapper;


    /**
     * 创建一个新的M78ApiKey
     *
     * @param apiKeyDTO 包含创建API密钥所需的属性
     * @return 如果成功创建API密钥则返回true，否则返回false
     */
    //通过m78ApiKeyMapper的insert方法创建M78ApiKey
    public boolean createApiKey(ApiKeyDTO apiKeyDTO) {
        // 把apiKeyDTO属性赋值给M78ApiKey
        M78ApiKey apiKey = M78ApiKey.builder()
                .typeId(apiKeyDTO.getTypeId())
                .type(apiKeyDTO.getType())
                .apiKey(UUID.randomUUID().toString())
                .creator(apiKeyDTO.getCreator())
                .createTime(LocalDateTime.now())
                .build();
        return m78ApiKeyMapper.insert(apiKey) > 0;
    }


    /**
     * 通过主键删除M78ApiKey
     *
     * @param id 主键ID
     * @return 删除操作是否成功，成功返回true，失败返回false
     */
    //通过主键删除M78ApiKey
    public boolean deleteApiKeyById(Long id) {
        int result = m78ApiKeyMapper.deleteById(id);
        return result > 0;
    }


    /**
     * 根据提供的apiKey查询对应的ApiKeyDTO列表
     *
     * @param apiKey 用于查询的apiKey
     * @return 匹配的ApiKeyDTO列表
     */
    //通过apiKey查询List<M78ApiKey>
    public List<ApiKeyDTO> getApiKeysByApiKey(String apiKey) {
        List<M78ApiKey> m78ApiKeyList = m78ApiKeyMapper.selectListByQuery(new QueryWrapper().eq("api_key", apiKey));
        //把m78ApiKeyList转成List<ApiKeyDTO>
        List<ApiKeyDTO> apiKeyDTOList = m78ApiKeyList.stream()
                .map(item -> ApiKeyDTO.builder()
                        .id(item.getId())
                        .typeId(item.getTypeId())
                        .type(item.getType())
                        .apiKey(item.getApiKey())
                        .creator(item.getCreator())
                        .createTime(item.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        return apiKeyDTOList;
    }


    /**
     * 根据给定的typeId和type查询API密钥列表
     *
     * @param typeId API密钥的类型ID
     * @param type   API密钥的类型
     * @return 匹配条件的API密钥DTO列表
     */
    //通过typeId和type查询List<M78ApiKey>
    public List<ApiKeyDTO> getApiKeysByTypeIdAndType(Long typeId, Integer type) {
        List<M78ApiKey> m78ApiKeyList = m78ApiKeyMapper.selectListByQuery(new QueryWrapper().eq("type_id", typeId).eq("type", type));
        List<ApiKeyDTO> apiKeyDTOList = m78ApiKeyList.stream()
                .map(item -> ApiKeyDTO.builder()
                        .id(item.getId())
                        .typeId(item.getTypeId())
                        .type(item.getType())
                        .apiKey(item.getApiKey())
                        .creator(item.getCreator())
                        .createTime(item.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        return apiKeyDTOList;
    }

}
