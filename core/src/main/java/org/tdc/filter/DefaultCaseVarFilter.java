package org.tdc.filter;

import org.tdc.book.TestCase;
import org.tdc.book.TestSet;
import org.tdc.config.book.FilterConfig;

/**
 * A {@link Filter} that checks for the existence of a non-blank
 * value in a particular {@link TestCase} variable. 
 * If found, true will be returned and the {@link TestCase} should 
 * be ignored during processing. 
 * 
 * <p>This {@link Filter} enables a user to mark a particular {@link TestCase}
 * to be ignored/skipped by adding a check mark (e.g. a "x") in a particular
 * DocIDRow. (The DocIDRow must be configured to set a "case" variable.)
 */
public class DefaultCaseVarFilter extends AbstractFilter {
	private final DefaultCaseVarFilterConfig config;
	
	public DefaultCaseVarFilter(DefaultCaseVarFilterConfig config) {
		this.config = config;
	}
	
	@Override
	public boolean ignoreTestCase(TestSet testSet, TestCase testCase) {
		String filterStr = testCase.getCaseVariables().get(config.getCaseVarName());
		return filterStr != null && filterStr.trim().length() > 0;
	}

	public static Filter build(FilterConfig filterConfig) {
		if (!(filterConfig instanceof DefaultCaseVarFilterConfig)) {
			throw new IllegalStateException(
					"FilterConfig '" + filterConfig.getFilterClassName() + 
					"' must be an instance of " + 
					DefaultCaseVarFilterConfig.class.getName());
		}
		return new DefaultCaseVarFilter((DefaultCaseVarFilterConfig)filterConfig);
	}
}
