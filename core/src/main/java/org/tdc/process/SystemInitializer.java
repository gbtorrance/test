package org.tdc.process;

import org.tdc.book.BookFactory;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.system.SystemConfig;
import org.tdc.filter.FilterFactory;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.schema.SchemaFactory;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.task.TaskFactory;

/**
 * Interface for retrieving initialized system components.
 */
public interface SystemInitializer {
	SystemConfig getSystemConfig();
	SchemaConfigFactory getSchemaConfigFactory();
	ModelConfigFactory getModelConfigFactory();
	BookConfigFactory getBookConfigFactory();
	SpreadsheetFileFactory getSpreadsheetFileFactory();
	SchemaFactory getSchemaFactory();
	ModelDefFactory getModelDefFactory();
	ModelInstFactory getModelInstFactory();
	BookFactory getBookFactory();
	FilterFactory getFilterFactory();
	SchemaValidatorFactory getSchemaValidatorFactory();
	TaskFactory getTaskFactory();
}
