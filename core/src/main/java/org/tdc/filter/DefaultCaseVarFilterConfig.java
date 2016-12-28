package org.tdc.filter;

import java.nio.file.Path;

import org.tdc.config.book.FilterConfig;
import org.tdc.config.util.Config;
import org.tdc.util.Addr;

/**
 * Configuration for {@link DefaultCaseVarFilter}.
 */
public class DefaultCaseVarFilterConfig implements FilterConfig {
	private final String caseVarName;

	public DefaultCaseVarFilterConfig(String caseVarName) {
		this.caseVarName = caseVarName;
	}

	@Override
	public String getFilterClassName() {
		return DefaultCaseVarFilter.class.getName();
	}
	
	public String getCaseVarName() {
		return caseVarName;
	}
	
	public static FilterConfig build(
			Config config, String key, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
	
		String caseVarName = config.getString(key + ".CaseVariableName", null, true);
		return new DefaultCaseVarFilterConfig(caseVarName);
	}
}
