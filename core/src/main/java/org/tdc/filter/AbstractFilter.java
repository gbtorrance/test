package org.tdc.filter;

import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;

/**
 * An abstract {@Filter} implementation.
 */
public class AbstractFilter implements Filter {

	@Override
	public boolean ignoreTestSet(TestSet testSet) {
		return false;
	}

	@Override
	public boolean ignoreTestCase(TestSet testSet, TestCase testCase) {
		return false;
	}

	@Override
	public boolean ignoreTestDoc(TestSet testSet, TestCase testCase, TestDoc testDoc) {
		return false;
	}
}
