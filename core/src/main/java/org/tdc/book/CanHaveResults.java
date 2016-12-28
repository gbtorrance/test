package org.tdc.book;

import org.tdc.result.Results;

/**
 * Defines functionality for test-related classes that can have {@link Results}.
 * 
 * <p>{@link Results} are used for reporting back the "results" of performing various
 * operations on the test classes (such as loading, validating, generating, etc.)
 * 
 * @see TestSet
 * @see TestCase
 * @see TestDoc
 */
public interface CanHaveResults {
	Results getResults();
}
