package org.tdc.server.mapper;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.tdc.util.Addr;

/**
 * MapStruct Mapper containing various helper methods.
 */
@Mapper
public class UtilMapper {
	public String addressToString(Addr addr) {
		return addr.toString();
	}
	
	public <T> T optionalResultToResult(Optional<T> optional) {
		return optional.isPresent() ? optional.get() : null;
	}
}
