package org.tdc.filter;

import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;

/**
 * Interface used during the various phases of processing for 
 * {@link TestSet}s, {@link TestCase}s, and {@link TestDoc}s 
 * (including data loading, schema validation, task processing, etc.)
 * to indicate whether or not a particular set, case, or doc should
 * be processed or ignored.
 */
public interface Filter {
	boolean ignoreTestSet(TestSet testSet);
	boolean ignoreTestCase(TestSet testSet, TestCase testCase);
	boolean ignoreTestDoc(TestSet testSet, TestCase testCase, TestDoc testDoc);
}
