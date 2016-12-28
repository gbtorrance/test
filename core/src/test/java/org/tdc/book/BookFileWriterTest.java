package org.tdc.book;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.process.BookProcessor;
import org.tdc.process.BookProcessorImpl;
import org.tdc.process.ModelProcessor;
import org.tdc.process.ModelProcessorImpl;
import org.tdc.process.SystemInitializer;
import org.tdc.process.SystemInitializerImpl;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookFileWriter} an its related classes.
 */
public class BookFileWriterTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does exercise the creation of a book spreadsheet file from a Book object
	
	private static ModelProcessor modelProcessor;
	private static BookProcessor bookProcessor;

	@BeforeClass
	public static void setup() {
		Path systemConfigRoot = Paths.get("testfiles/TDCFiles");
		SystemInitializer init = new SystemInitializerImpl
				.Builder()
				.defaultFactories(systemConfigRoot)
				.build();
		modelProcessor = new ModelProcessorImpl(
				init.getModelConfigFactory(), 
				init.getModelDefFactory(), 
				init.getSpreadsheetFileFactory());
		bookProcessor = new BookProcessorImpl(
				init.getBookConfigFactory(), 
				init.getModelInstFactory(), 
				init.getBookFactory(), 
				init.getSpreadsheetFileFactory(), 
				init.getFilterFactory(), 
				init.getTaskFactory(), 
				init.getSchemaValidatorFactory());
	}
	
	@Test
	public void testWriteBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		Path targetPath = Paths.get("testfiles/Temp/TestBook.xlsm");
		bookProcessor.createBook(bookAddr, targetPath, null, true);
	}
	
	@Test
	public void testWriteBookBasedOnExistingBook() {
		Addr bookAddr = new Addr("/Tax/IndividualIncome2012v1");
		Path targetPath = Paths.get("testfiles/Temp/TestBookWithExistingData.xlsm");
		Path basedOnBookPath = Paths.get("testfiles/SampleFiles/TestBook.xlsx");
		bookProcessor.createBook(bookAddr, targetPath, basedOnBookPath, true);
	}
	
	@Test
	public void initCustomizer() {
		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040A");
		Path targetPath = Paths.get("testfiles/Temp/Customizer2.xlsx");
		modelProcessor.createCustomizer(modelAddr, targetPath, null, true);
	}

	@Test
	public void initCustomizerFromPrevious() {
		Addr modelAddr = new Addr("Tax/efile1040x_2012v3.0/Model_1040A");
		Path targetPath = Paths.get("testfiles/Temp/Customizer3.xlsx");
		Addr basedOnModelAddr = new Addr("Tax/efile1040x_2011v3.0/Model_1040A");
		modelProcessor.createCustomizer(modelAddr, targetPath, basedOnModelAddr, true);
	}
}
