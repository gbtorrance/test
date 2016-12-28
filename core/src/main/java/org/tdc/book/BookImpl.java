package org.tdc.book;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.util.Addr;

/**
 * A {@link Book} implementation.
 */
public class BookImpl implements Book {

	private static final Logger log = LoggerFactory.getLogger(BookImpl.class);
	
	private final BookConfig config;
	private final Map<String, Page> pages;
	private final List<TestSet> testSets;
	
	private BookImpl(Builder builder) {
		this.config = builder.config;
		this.pages = Collections.unmodifiableMap(builder.pages); // unmodifiable
		this.testSets = Collections.unmodifiableList(builder.testSets); // unmodifiable
	}
	
	@Override
	public BookConfig getConfig() {
		return config;
	}
	
	@Override
	public Map<String, Page> getPages() {
		return pages;
	}
	
	@Override
	public List<TestSet> getTestSets() {
		return testSets;
	}
	
	@Override
	public TestSet getTestSet(String setName) {
		Optional<TestSet> testSet = 
				testSets.stream()
				.filter(x -> x.getSetName().equals(setName))
				.findFirst();
		if (!testSet.isPresent()) {
			throw new IllegalStateException("Unable to find matching TestSet: '" + setName + "'");
		}
		return testSet.get();
	}
	
	public static class Builder {
		private final SpreadsheetFile spreadsheetFile;
		private final BookConfigFactory bookConfigFactory; 
		private final ModelInstFactory modelInstFactory;
		
		private BookConfig config;
		private Map<String, Page> pages;
		public List<TestSet> testSets;
		
		public Builder(
				SpreadsheetFile spreadsheetFile,
				BookConfigFactory bookConfigFactory, 
				ModelInstFactory modelInstFactory) {
			
			log.info("Creating Book from SpreadsheetFile object");
			this.spreadsheetFile = spreadsheetFile;
			this.bookConfigFactory = bookConfigFactory;
			this.modelInstFactory = modelInstFactory;
		}
		
		public Book build() {
			Addr addr = getBookAddrFromConfigSheet(spreadsheetFile);
			config = bookConfigFactory.getBookConfig(addr);
			pages = new PageImpl.Builder(config.getPageConfigs(), modelInstFactory, spreadsheetFile).buildAll();
			testSets = new TestSetImpl.Builder(pages, config.getDocTypeConfigs()).buildAll();
			return new BookImpl(this);
		}
		
		private Addr getBookAddrFromConfigSheet(SpreadsheetFile spreadsheetFile) {
			Spreadsheet sheet = spreadsheetFile.getSpreadsheet(BookUtil.CONFIG_SHEET_NAME);
			if (sheet == null) {
				throw new RuntimeException("Configuration worksheet '" + BookUtil.CONFIG_SHEET_NAME + "' does not exist");
			}
			String addrStr = sheet.getCellValue(BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW, BookUtil.CONFIG_SHEET_BOOK_ADDR_COL).trim();
			if (addrStr.equals("")) {
				throw new RuntimeException("Configuration worksheet '" + 
						BookUtil.CONFIG_SHEET_NAME + "' must contain a Book address in row " + 
						BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW + ", column " + BookUtil.CONFIG_SHEET_BOOK_ADDR_COL);
			}
			return new Addr(addrStr);
		}
	}
}
