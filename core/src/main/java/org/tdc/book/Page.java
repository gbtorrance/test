package org.tdc.book;

import java.util.List;

import org.tdc.config.book.PageConfig;
import org.tdc.modelinst.ModelInst;

/**
 * Defines core functionality for a Page.
 * 
 * <p> Pages are contained within {@link Book}s and each refers to 
 * a particular {@link ModelInst} which defines the structure of the Page.
 * 
 * <p>Pages also each contain zero or more {@link TestDoc}s.
 */
public interface Page {
	PageConfig getConfig();
	String getName();
	ModelInst getModelInst();
	List<TestDoc> getTestDocs();
}
