package org.tdc.filter;

import java.util.ArrayList;
import java.util.List;

import org.tdc.book.Book;
import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;
import org.tdc.config.book.FilterConfig;

/**
 * An {@link Filter} implementation that can be used to combine the 
 * results of executing the "ignore" methods on any number of {@link Filter} objects.
 * If any of the objects returns a true result for one of the method calls, 
 * the composite filter will also return true. 
 */
public class CompositeFilter implements Filter {
	private final List<Filter> filters;
	
	public CompositeFilter(List<Filter> filters) {
		this.filters = filters;
	}
	
	private CompositeFilter(Builder builder) {
		this.filters = builder.filters;
	}

	@Override
	public boolean ignoreTestSet(TestSet testSet) {
		return filters.stream().anyMatch(f -> f.ignoreTestSet(testSet));
	}

	@Override
	public boolean ignoreTestCase(TestSet testSet, TestCase testCase) {
		return filters.stream().anyMatch(f -> f.ignoreTestCase(testSet, testCase));
	}

	@Override
	public boolean ignoreTestDoc(TestSet testSet, TestCase testCase, TestDoc testDoc) {
		return filters.stream().anyMatch(f -> f.ignoreTestDoc(testSet, testCase, testDoc));
	}

	public static class Builder {
		private final FilterFactory filterFactory;
		private final Book book;
		
		private List<Filter> filters;
		
		public Builder(FilterFactory filterFactory, Book book) {
			this.filterFactory = filterFactory;
			this.book = book;
		}
		
		public Filter build() {
			this.filters = createFilters();
			return new CompositeFilter(this);
		}
		
		private List<Filter> createFilters() {
			List<Filter> list = new ArrayList<>();
			List<FilterConfig> filterConfigs = book.getConfig().getFilterConfigs();
			for (FilterConfig filterConfig : filterConfigs) {
				Filter filter = filterFactory.createFilter(filterConfig);
				list.add(filter);
			}
			return list;
		}
	}
}
