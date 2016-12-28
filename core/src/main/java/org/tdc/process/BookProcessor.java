package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.util.Addr;

/**
 * Interface defining core functionality for processing Books.
 */
public interface BookProcessor {
	String getTargetBookFileExtension(Addr bookAddr);
	void createBook(
			Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting);
	Book loadAndProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks, 
			List<String> taskIDsToProcess, Map<String, String> taskParams, 
			Path targetPath, boolean overwriteExisting);
}
