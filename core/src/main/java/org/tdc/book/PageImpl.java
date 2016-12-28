package org.tdc.book;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tdc.config.book.PageConfig;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * A {@link Page} implementation.
 */
public class PageImpl implements Page {
	
	private final PageConfig config;
	private final ModelInst modelInst;
	private final List<TestDoc> testDocs;
	
	private PageImpl(Builder builder) {
		this.config = builder.config;
		this.modelInst = builder.modelInst;
		this.testDocs = Collections.unmodifiableList(builder.testDocs); // unmodifiable
	}

	@Override
	public PageConfig getConfig() {
		return config;
	}
	
	@Override
	public String getName() {
		return config.getPageName();
	}
	
	@Override
	public ModelInst getModelInst() {
		return modelInst;
	}
	
	@Override 
	public List<TestDoc> getTestDocs() {
		return testDocs;
	}
	
	public static class Builder {
		private final Map<String, PageConfig> configs;
		private final ModelInstFactory modelInstFactory;
		private final SpreadsheetFile spreadsheetFile;
		
		private PageConfig config;
		private ModelInst modelInst;
		private List<TestDoc> testDocs;
		
		public Builder(
				Map<String, PageConfig> configs, 
				ModelInstFactory modelInstFactory, 
				SpreadsheetFile spreadsheetFile) {
			
			this.configs = configs;
			this.modelInstFactory = modelInstFactory;
			this.spreadsheetFile = spreadsheetFile;
		}
		
		public Map<String, Page> buildAll() {
			Map<String, Page> pages = new LinkedHashMap<>();
			Collection<PageConfig> pageConfigs = configs.values(); 
			for (PageConfig pageConfig : pageConfigs) {
				if (spreadsheetFile.getSpreadsheet(pageConfig.getPageName()) != null) {
					pages.put(pageConfig.getPageName(), build(pageConfig));
				}
			}
			return pages;
		}

		private Page build(PageConfig config) {
			this.config = config;
			modelInst = modelInstFactory.getModelInst(config.getModelConfig());
			testDocs = new TestDocImpl.Builder(config, spreadsheetFile).buildAll();
			return new PageImpl(this);
		}
	}
}
