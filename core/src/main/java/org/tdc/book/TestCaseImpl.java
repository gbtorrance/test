package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.tdc.config.book.DocTypeConfig;
import org.tdc.result.Results;

/**
 * A {@link TestCase} implementation.
 */
public class TestCaseImpl implements TestCase {
	
	private final int caseNum;
	private final String setName;
	private final List<TestDoc> testDocs;
	private final Map<String, String> caseVariables;
	private final Results results;
	
	private TestCaseImpl(Builder builder) {
		this.caseNum = builder.caseNum;
		this.setName = builder.setName;
		this.testDocs = Collections.unmodifiableList(builder.testDocs); // unmodifiable
		this.caseVariables = Collections.unmodifiableMap(builder.caseVariables); // unmodifiable
		this.results = builder.results;
	}

	@Override
	public int getCaseNum() {
		return caseNum;
	}

	@Override
	public String getSetName() {
		return setName;
	}
	
	@Override
	public List<TestDoc> getTestDocs() {
		return testDocs;
	}

	@Override
	public TestDoc getTestDocOfType(String docType) {
		Optional<TestDoc> testDoc = testDocs
				.stream()
				.filter(td -> td
						.getPageConfig()
						.getDocTypeConfig()
						.getDocTypeName()
						.equals(docType))
				.findFirst();
		return testDoc.isPresent() ? testDoc.get() : null;
	}
	
	@Override
	public Map<String, String> getCaseVariables() {
		return caseVariables;
	}
	
	@Override
	public Results getResults() {
		return results;
	}
	
	@Override
	public String toString() {
		return caseNum + (setName == null || setName.length() == 0 ? "" : " [" + setName + "]");
	}

	public static class Builder {
		private final String setName;
		private final List<TestDoc> allTestDocsInSet;
		private final Map<String, DocTypeConfig> docTypeConfigs;
		
		private int caseNum;
		private List<TestDoc> testDocs;
		private Map<String, String> caseVariables;
		private Results results;
		
		public Builder(
				String setName, 
				List<TestDoc> allTestDocsInSet, 
				Map<String, DocTypeConfig> docTypeConfigs) {
			
			this.setName = setName;
			this.allTestDocsInSet = allTestDocsInSet;
			this.docTypeConfigs = docTypeConfigs;
		}
		
		public List<TestCase> buildAll() {
			List<TestCase> testCases = new ArrayList<>();
			Map<Integer, List<TestDoc>> testCaseNumToTestDocMap = allTestDocsInSet
					.stream()
					.collect(Collectors.groupingBy(TestDoc::getCaseNum));
			for (Entry<Integer, List<TestDoc>> entry : testCaseNumToTestDocMap.entrySet()) {
				TestCase testCase = build(entry.getKey(), entry.getValue());
				testCases.add(testCase);
			}
			return testCases;
		}
		
		private TestCase build(int caseNum, List<TestDoc> allTestDocsInCase) {
			this.caseNum = caseNum;
			this.testDocs = allTestDocsInCase;
			results = new Results();
			buildVariables();
			verifyDocTypeMinMax();
			return new TestCaseImpl(this);
		}
		
		private void buildVariables() {
			// populate caseVariables with all case variables defined 
			// for TestDocs belonging to this TestCase
			caseVariables = new HashMap<>();
			testDocs.stream().forEach(td -> caseVariables.putAll(td.getCaseVariables()));
		}

		private void verifyDocTypeMinMax() {
			// count the number of each DocType among the TestDocs for this TestCase
			Map<DocTypeConfig, Long> docTypeConfigToDocTypeCountMap = testDocs
					.stream()
					.collect(Collectors.groupingBy(
							td -> td.getPageConfig().getDocTypeConfig(),
							Collectors.counting()));
			// compare to min/max in complete list of DocTypes specified in configuration
			// [don't want to compare to just list in Book; if we do, some might be missed]
			for (DocTypeConfig dtConfig : docTypeConfigs.values()) {
				Long count = docTypeConfigToDocTypeCountMap.getOrDefault(dtConfig, Long.valueOf(0));
				if (dtConfig.getMinPerTestCase() > count || dtConfig.getMaxPerTestCase() < count) {
					String msg = "Number of Test Docs for Test Set '" + 
							(setName.equals("") ? "[default]" : setName) + "', Test Case " +
							caseNum + " must be at least " + dtConfig.getMinPerTestCase() + 
							" and at most " + dtConfig.getMaxPerTestCase() + "; current count: " + count;
					throw new RuntimeException(msg);
				}
			}
		}
	}
}
