package org.tdc.process;

import java.nio.file.Path;

import org.tdc.book.BookFactory;
import org.tdc.book.BookFactoryImpl;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.book.BookConfigFactoryImpl;
import org.tdc.config.book.FilterConfigFactory;
import org.tdc.config.book.FilterConfigFactoryImpl;
import org.tdc.config.book.TaskConfigFactory;
import org.tdc.config.book.TaskConfigFactoryImpl;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.config.system.InitializerConfigFactory;
import org.tdc.config.system.InitializerConfigFactoryImpl;
import org.tdc.config.system.SystemConfig;
import org.tdc.config.system.SystemConfigImpl;
import org.tdc.filter.FilterFactory;
import org.tdc.filter.FilterFactoryImpl;
import org.tdc.initializer.InitializerFactory;
import org.tdc.initializer.InitializerFactoryImpl;
import org.tdc.initializer.InitializerProcessor;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.schemavalidate.SchemaValidatorFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.task.TaskFactoryImpl;

/**
 * A {@link SystemInitializer} implementation. 
 */
public class SystemInitializerImpl implements SystemInitializer {
	private final SystemConfig systemConfig;
	private final SchemaConfigFactory schemaConfigFactory;
	private final ModelConfigFactory modelConfigFactory;
	private final BookConfigFactory bookConfigFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	private final SchemaFactory schemaFactory;
	private final ModelDefFactory modelDefFactory;
	private final ModelInstFactory modelInstFactory;
	private final BookFactory bookFactory;
	private final FilterFactory filterFactory;
	private final SchemaValidatorFactory schemaValidatorFactory;
	private final TaskFactory taskFactory;
	
	private SystemInitializerImpl(Builder builder) {
		this.systemConfig = builder.systemConfig;
		this.schemaConfigFactory = builder.schemaConfigFactory;
		this.modelConfigFactory = builder.modelConfigFactory;
		this.bookConfigFactory = builder.bookConfigFactory;
		this.spreadsheetFileFactory = builder.spreadsheetFileFactory;
		this.schemaFactory = builder.schemaFactory;
		this.modelDefFactory = builder.modelDefFactory;
		this.modelInstFactory = builder.modelInstFactory;
		this.bookFactory = builder.bookFactory;
		this.filterFactory = builder.filterFactory;
		this.schemaValidatorFactory = builder.schemaValidatorFactory;
		this.taskFactory = builder.taskFactory;
	}
	
	@Override
	public SystemConfig getSystemConfig() {
		return systemConfig;
	}

	@Override
	public SchemaConfigFactory getSchemaConfigFactory() {
		return schemaConfigFactory;
	}

	@Override
	public ModelConfigFactory getModelConfigFactory() {
		return modelConfigFactory;
	}

	@Override
	public BookConfigFactory getBookConfigFactory() {
		return bookConfigFactory;
	}

	@Override
	public SpreadsheetFileFactory getSpreadsheetFileFactory() {
		return spreadsheetFileFactory;
	}

	@Override
	public SchemaFactory getSchemaFactory() {
		return schemaFactory;
	}

	@Override
	public ModelDefFactory getModelDefFactory() {
		return modelDefFactory;
	}

	@Override
	public ModelInstFactory getModelInstFactory() {
		return modelInstFactory;
	}

	@Override
	public BookFactory getBookFactory() {
		return bookFactory;
	}

	@Override
	public FilterFactory getFilterFactory() {
		return filterFactory;
	}

	@Override
	public SchemaValidatorFactory getSchemaValidatorFactory() {
		return schemaValidatorFactory;
	}

	@Override
	public TaskFactory getTaskFactory() {
		return taskFactory;
	}
	
	public static class Builder {
		private InitializerConfigFactory initializerConfigFactory;
		private SystemConfig systemConfig;
		private SchemaConfigFactory schemaConfigFactory;
		private ModelConfigFactory modelConfigFactory;
		private FilterConfigFactory filterConfigFactory;
		private TaskConfigFactory taskConfigFactory;
		private BookConfigFactory bookConfigFactory;
		private SpreadsheetFileFactory spreadsheetFileFactory;
		private InitializerFactory initializerFactory;
		private SchemaFactory schemaFactory;
		private ModelDefFactory modelDefFactory;
		private ModelInstFactory modelInstFactory;
		private BookFactory bookFactory;
		private FilterFactory filterFactory;
		private SchemaValidatorFactory schemaValidatorFactory; 
		private TaskFactory taskFactory;
		
		public Builder defaultFactories(Path systemConfigRoot) {
			initializerConfigFactory = new InitializerConfigFactoryImpl();
			systemConfig = new SystemConfigImpl.Builder(
					systemConfigRoot, initializerConfigFactory).build();
			schemaConfigFactory = new SchemaConfigFactoryImpl(systemConfig.getSchemasConfigRoot());
			modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
			filterConfigFactory = new FilterConfigFactoryImpl();
			taskConfigFactory = new TaskConfigFactoryImpl();
			bookConfigFactory = new BookConfigFactoryImpl(
					systemConfig.getBooksConfigRoot(), modelConfigFactory, 
					filterConfigFactory, taskConfigFactory);
			spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();
			initializerFactory = new InitializerFactoryImpl();
			schemaFactory = new SchemaFactoryImpl();
			modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
			modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
			bookFactory = new BookFactoryImpl(bookConfigFactory, modelInstFactory);
			filterFactory = new FilterFactoryImpl();
			schemaValidatorFactory = new SchemaValidatorFactoryImpl();
			taskFactory = new TaskFactoryImpl();
			return this;
		}
		
		public Builder setInitializerConfigFactory(InitializerConfigFactory initializerConfigFactory) {
			this.initializerConfigFactory = initializerConfigFactory;
			return this;
		}
		
		public Builder setSystemConfig(SystemConfig systemConfig) {
			this.systemConfig = systemConfig;
			return this;
		}
		
		public Builder setSchemaConfigFactory(SchemaConfigFactory schemaConfigFactory) {
			this.schemaConfigFactory = schemaConfigFactory;
			return this;
		}

		public Builder setModelConfigFactory(ModelConfigFactory modelConfigFactory) {
			this.modelConfigFactory = modelConfigFactory;
			return this;
		}

		public Builder setFilterConfigFactory(FilterConfigFactory filterConfigFactory) {
			this.filterConfigFactory = filterConfigFactory;
			return this;
		}

		public Builder setTaskConfigFactory(TaskConfigFactory taskConfigFactory) {
			this.taskConfigFactory = taskConfigFactory;
			return this;
		}

		public Builder setBookConfigFactory(BookConfigFactory bookConfigFactory) {
			this.bookConfigFactory = bookConfigFactory;
			return this;
		}

		public Builder setSpreadsheetFileFactory(SpreadsheetFileFactory spreadsheetFileFactory) {
			this.spreadsheetFileFactory = spreadsheetFileFactory;
			return this;
		}

		public Builder setInitializerFactory(InitializerFactory initializerFactory) {
			this.initializerFactory = initializerFactory;
			return this;
		}

		public Builder setSchemaFactory(SchemaFactory schemaFactory) {
			this.schemaFactory = schemaFactory;
			return this;
		}

		public Builder setModelDefFactory(ModelDefFactory modelDefFactory) {
			this.modelDefFactory = modelDefFactory;
			return this;
		}

		public Builder setModelInstFactory(ModelInstFactory modelInstFactory) {
			this.modelInstFactory = modelInstFactory;
			return this;
		}

		public Builder setBookFactory(BookFactory bookFactory) {
			this.bookFactory = bookFactory;
			return this;
		}

		public Builder setFilterFactory(FilterFactory filterFactory) {
			this.filterFactory = filterFactory;
			return this;
		}

		public Builder setSchemaValidatorFactory(SchemaValidatorFactory schemaValidatorFactory) {
			this.schemaValidatorFactory = schemaValidatorFactory;
			return this;
		}

		public Builder setTaskFactory(TaskFactory taskFactory) {
			this.taskFactory = taskFactory;
			return this;
		}

		public SystemInitializer build() {
			processInitializers();
			return new SystemInitializerImpl(this);
		}

		private void processInitializers() {
			InitializerProcessor processor = new InitializerProcessor
					.Builder(initializerFactory, systemConfig)
					.build();
			processor.processInitializers();;
		}
	}
}
