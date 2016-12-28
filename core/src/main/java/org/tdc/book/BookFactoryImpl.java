package org.tdc.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link BookFactory} implementation.
 * 
 * <p>Creates {@link ModelInst} instances, as necessary, using provided {@link ModelInstFactory}.
 */
public class BookFactoryImpl implements BookFactory {
	
	private static final Logger log = LoggerFactory.getLogger(BookFactoryImpl.class);

	private final BookConfigFactory bookConfigFactory;
	private final ModelInstFactory modelInstFactory;
	
	public BookFactoryImpl(BookConfigFactory bookConfigFactory, ModelInstFactory modelInstFactory) {
		this.bookConfigFactory = bookConfigFactory;
		this.modelInstFactory = modelInstFactory;
	}

	@Override
	public Book getBook(SpreadsheetFile spreadsheetFile) {
		return new BookImpl.Builder(
				spreadsheetFile, 
				bookConfigFactory, 
				modelInstFactory).build();
	}
}
