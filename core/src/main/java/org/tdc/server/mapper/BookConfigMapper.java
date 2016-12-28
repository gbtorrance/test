package org.tdc.server.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.book.BookConfig;
import org.tdc.server.dto.BookConfigDTO;

/**
 * MapStruct Mapper to map {@link BookConfig} to {@link BookConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class BookConfigMapper {
	public static BookConfigMapper INSTANCE = Mappers.getMapper(BookConfigMapper.class);
	
	public abstract List<BookConfigDTO> bookConfigsToBookConfigDTOs(List<BookConfig> bookConfigs);
	
	@Mapping(source = "addr", target = "bookAddress")
	public abstract BookConfigDTO bookConfigToBookConfigDTO(BookConfig bookConfig);
}
