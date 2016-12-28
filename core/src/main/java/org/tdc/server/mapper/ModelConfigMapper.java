package org.tdc.server.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.config.model.ModelConfig;
import org.tdc.server.dto.ModelConfigDTO;

/**
 * MapStruct Mapper to map {@link ModelConfig} to {@link ModelConfigDTO}.
 */
@Mapper(uses = UtilMapper.class)
public abstract class ModelConfigMapper {
	public static ModelConfigMapper INSTANCE = Mappers.getMapper(ModelConfigMapper.class);
	
	public abstract List<ModelConfigDTO> modelConfigsToModelConfigDTOs(List<ModelConfig> modelConfigs);
	
	@Mapping(source = "addr", target = "modelAddress")
	public abstract ModelConfigDTO modelConfigToModelConfigDTO(ModelConfig modelConfig);
}
