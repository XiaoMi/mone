package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.dto.SearchSaveDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogSearchSaveDO;
import com.xiaomi.mone.log.manager.model.vo.SearchSaveInsertCmd;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SearchSaveConvert {
    SearchSaveConvert INSTANCE = Mappers.getMapper(SearchSaveConvert.class);

    @Mapping(target = "id", resultType = String.class)
    SearchSaveDTO fromDO(MilogLogSearchSaveDO obj);

    List<SearchSaveDTO> fromDOList(List<MilogLogSearchSaveDO> objList);

    MilogLogSearchSaveDO toDO(SearchSaveInsertCmd obj);

}
