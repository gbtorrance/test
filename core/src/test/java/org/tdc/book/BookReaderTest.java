package org.tdc.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.process.BookProcessor;
import org.tdc.process.BookProcessorImpl;
import org.tdc.process.SystemInitializer;
import org.tdc.process.SystemInitializerImpl;
import org.tdc.util.Util;

/**
 * Unit tests for reading a {@link Book} from spreadsheet file, 
 * loading its contained {@link TestDoc}s/{@link TestCase}s/{@link TestSet}s, 
 * and then performing validation.
 */
public class BookReaderTest {

	private static BookProcessor bookProcessor;

	@BeforeClass
	public static void setup() {
		Path systemConfigRoot = Paths.get("testfiles/TDCFiles");
		SystemInitializer init = new SystemInitializerImpl
				.Builder()
				.defaultFactories(systemConfigRoot)
				.build();
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
	public void testReadBookAndValidate() throws IOException {
		Path bookPath = Paths.get("testfiles/SampleFiles/TestBook.xlsx");
		Path targetPath = Paths.get("testfiles/Temp/TestBookWithLog.xlsx");
		Util.purgeDirectory(Paths.get("testfiles/Temp/ExportRoot"));
		Files.deleteIfExists(targetPath);
		
		bookProcessor.loadAndProcessBook(
				bookPath, true, true, null, null, targetPath, true);

		assertThat(Files.exists(targetPath)).isEqualTo(true);
	}
}
