package org.tdc.server.process;

import java.io.File;
import java.util.List;

import org.tdc.server.dto.BookConfigDTO;
import org.tdc.server.dto.BookDTO;
import org.tdc.server.dto.ModelConfigDTO;
import org.tdc.server.dto.SchemaConfigDTO;

public interface ServerProcessor {
	List<SchemaConfigDTO> getSchemaConfigDTOs();
	List<ModelConfigDTO> getModelConfigDTOs();
	List<BookConfigDTO> getBookConfigDTOs();
	String uploadBookFile(File bookFile);
	BookDTO getBookInfo(String bookID);
}
