package org.tdc.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.book.BookFactory;
import org.tdc.book.BookFileWriter;
import org.tdc.book.BookSchemaValidator;
import org.tdc.book.BookSpreadsheetLogWriter;
import org.tdc.book.BookTestDataLoader;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.filter.CompositeFilter;
import org.tdc.filter.Filter;
import org.tdc.filter.FilterFactory;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.schemavalidate.SchemaValidatorFactory;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.task.TaskFactory;
import org.tdc.task.TaskProcessor;
import org.tdc.util.Addr;

/**
 * A {@link BookProcessor} implementation.
 */
public class BookProcessorImpl implements BookProcessor {
	private final BookConfigFactory bookConfigFactory;
	private final ModelInstFactory modelInstFactory;
	private final BookFactory bookFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	private final FilterFactory filterFactory;
	private final TaskFactory taskFactory;
	private final SchemaValidatorFactory schemaValidatorFactory;
	
	public BookProcessorImpl(
			BookConfigFactory bookConfigFactory, 
			ModelInstFactory modelInstFactory, 
			BookFactory bookFactory,
			SpreadsheetFileFactory spreadsheetFileFactory, 
			FilterFactory filterFactory, 
			TaskFactory taskFactory, 
			SchemaValidatorFactory schemaValidatorFactory) {
		
		this.bookConfigFactory = bookConfigFactory;
		this.modelInstFactory = modelInstFactory;
		this.bookFactory = bookFactory;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
		this.filterFactory = filterFactory;
		this.taskFactory = taskFactory;
		this.schemaValidatorFactory = schemaValidatorFactory;
	}
	
	@Override
	public String getTargetBookFileExtension(Addr bookAddr) {
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		return getTargetBookFileExtension(bookConfig);
	}
	
	@Override
	public void createBook(Addr bookAddr, Path targetPath, 
			Path basedOnBookPath, boolean overwriteExisting) {
		
		SpreadsheetFile basedOnBookSF = getBasedOnSpreadsheetFile(basedOnBookPath);
		Book basedOnBook = getBasedOnBook(basedOnBookSF);
		BookConfig bookConfig = getBookConfig(bookAddr);
		verifyTargetPathExtension(bookConfig, targetPath);
		SpreadsheetFile newBookSF = createNewSpreadsheetFile(bookConfig);
		writeToSpreadsheetFile(bookConfig, newBookSF, basedOnBook, basedOnBookSF);
		saveNewBook(newBookSF, targetPath, overwriteExisting);
	}

	@Override
	public Book loadAndProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks,
			List<String> taskIDsToProcess, Map<String, String> taskParams,
			Path targetPath, boolean overwriteExisting) {
		
		// load phase
		SpreadsheetFile spreadsheetFile = 
				targetPath == null ? 
				spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(bookPath) : 
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(bookPath); 
		Book book = bookFactory.getBook(spreadsheetFile);
		Filter filter = new CompositeFilter
				.Builder(filterFactory, book)
				.build();
		BookTestDataLoader loader = new BookTestDataLoader(book, spreadsheetFile, filter);
		loader.loadTestData();
		TaskProcessor taskProcessor = null;
		if (processTasks) {
			taskProcessor = new TaskProcessor
					.Builder(taskFactory, book)
					.setTaskIDList(taskIDsToProcess) 
					.setTaskParams(taskParams)
					.setFilter(filter)
					.build();
			taskProcessor.preProcessRead(spreadsheetFile);
		}
		
		// validate / task process phase
		if (schemaValidate) {
			BookSchemaValidator schemaValidator = new BookSchemaValidator(
					book, schemaValidatorFactory, filter);
			schemaValidator.validate();
		}
		if (processTasks) {
			taskProcessor.processTasks();
		}
		
		// write phase (optional)
		if (targetPath != null) {
			BookSpreadsheetLogWriter logWriter = 
					new BookSpreadsheetLogWriter(book, spreadsheetFile, filter);
			logWriter.writeLog();
			if (processTasks) {
				taskProcessor.postProcessWrite(spreadsheetFile);
			}
			if (overwriteExisting) {
				spreadsheetFile.save(targetPath);
			}
			else {
				spreadsheetFile.saveAsNew(targetPath);
			}
		}
		return book;
	}
	
	private String getTargetBookFileExtension(BookConfig bookConfig) {
		String extension = bookConfig.getBookTemplateFileExtension();
		return extension == null ? "xlsx" : extension;
	}
	
	private SpreadsheetFile getBasedOnSpreadsheetFile(Path basedOnBookPath) {
		SpreadsheetFile spreadsheetFile = null;
		if (basedOnBookPath != null) {
			spreadsheetFile = spreadsheetFileFactory
					.createReadOnlySpreadsheetFileFromPath(basedOnBookPath); 
		}
		return spreadsheetFile;
	}

	private Book getBasedOnBook(SpreadsheetFile basedOnBookSF) {
		Book book = null;
		if (basedOnBookSF != null) {
			book = bookFactory.getBook(basedOnBookSF); 
		}
		return book;
	}

	private BookConfig getBookConfig(Addr bookAddr) {
		return bookConfigFactory.getBookConfig(bookAddr);
	}

	private void verifyTargetPathExtension(BookConfig bookConfig, Path targetPath) {
		String expectedExtension = getTargetBookFileExtension(bookConfig);
		String targetFilename = targetPath.toString();
		String targetExtension = targetFilename.substring(targetFilename.lastIndexOf(".") + 1);
		if (!expectedExtension.equals(targetExtension)) {
			throw new IllegalStateException("Extension of target Book file " + 
					targetFilename + " does not match expected extension '." + 
					expectedExtension + "'");
		}
	}

	private SpreadsheetFile createNewSpreadsheetFile(BookConfig bookConfig) {
		Path templateFile = bookConfig.getBookTemplateFile();
		SpreadsheetFile newSpreadsheetFile = 
				templateFile == null ?
				spreadsheetFileFactory.createNewSpreadsheetFile() :
				spreadsheetFileFactory.createEditableSpreadsheetFileFromPath(templateFile);
		return newSpreadsheetFile;
	}

	private void writeToSpreadsheetFile(
			BookConfig bookConfig, SpreadsheetFile newBookSF, 
			Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		
		BookFileWriter bookFileWriter =  new BookFileWriter(
				bookConfig, newBookSF, modelInstFactory);
		if (basedOnBook == null) {
			bookFileWriter.write();
		} 
		else {
			bookFileWriter.writeWithTestDataFromExistingBook(basedOnBook, basedOnBookSF);
		}
		bookFileWriter.deleteDefaultSheetIfExists();
	}

	private void saveNewBook(SpreadsheetFile newBookSF, 
			Path targetPath, boolean overwriteExisting) {
		
		if (overwriteExisting) {
			newBookSF.save(targetPath);
		}
		else {
			newBookSF.saveAsNew(targetPath);
		}
	}
}
