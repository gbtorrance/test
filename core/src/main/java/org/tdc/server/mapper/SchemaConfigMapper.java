package org.tdc.server.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.server.dto.SchemaConfigDTO;

/**
 * MapStruct Mapper to map {@link SchemaConfig} to {@link SchemaConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class SchemaConfigMapper {
	public static SchemaConfigMapper INSTANCE = Mappers.getMapper(SchemaConfigMapper.class);
	
	public abstract List<SchemaConfigDTO> schemaConfigsToSchemaConfigDTOs(List<SchemaConfig> schemaConfigs);
	
	@Mapping(source = "addr", target = "schemaAddress")
	public abstract SchemaConfigDTO schemaConfigToSchemaConfigDTO(SchemaConfig schemaConfig);
}
