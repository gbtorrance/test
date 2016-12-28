package org.tdc.book;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.filter.Filter;
import org.tdc.result.Result;
import org.tdc.schemavalidate.SchemaValidator;
import org.tdc.schemavalidate.SchemaValidatorFactory;

/**
 * Schema validates all {@link TestDoc}s in a particular {@link Book}.
 */
public class BookSchemaValidator {
	
	private static final Logger log = LoggerFactory.getLogger(BookSchemaValidator.class);
	
	private final Book book;
	private final SchemaValidatorFactory schemaValidatorFactory;
	private final Filter filter;
	
	public BookSchemaValidator(Book book, 
			SchemaValidatorFactory schemaValidatorFactory, Filter filter) {
		
		this.book = book;
		this.schemaValidatorFactory = schemaValidatorFactory;
		this.filter = filter;
	}
	
	public void validate() {
		List<TestSet> testSets = book.getTestSets();
		for (TestSet testSet : testSets) {
			if (filter == null || !filter.ignoreTestSet(testSet)) {
				validateTestSet(testSet);
			}
		}
	}

	private void validateTestSet(TestSet testSet) {
		log.debug("Schema validating TestSet: {}", testSet.getSetName());
		List<TestCase> testCases = testSet.getTestCases();
		for (TestCase testCase : testCases) {
			if (filter == null || !filter.ignoreTestCase(testSet, testCase)) {
				validateTestCase(testSet, testCase);
			}
		}
	}

	private void validateTestCase(TestSet testSet, TestCase testCase) {
		log.debug("Schema validating TestCase: {}", testCase.getCaseNum());
		List<TestDoc> testDocs = testCase.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			if (filter == null || !filter.ignoreTestDoc(testSet, testCase, testDoc)) {
				validateTestDoc(testDoc);
			}
		}
	}

	private void validateTestDoc(TestDoc testDoc) {
		log.debug("Schema validating TestCase num {}, TestSet name '{}', column {}", 
				testDoc.getCaseNum(), testDoc.getSetName(), testDoc.getColNum());
		Optional<Result> schemaValidateResult = testDoc.getResults().getSchemaValidateResult();
		ModelConfig modelConfig = testDoc.getPageConfig().getModelConfig(); 
		if (schemaValidateResult.isPresent()) {
			log.debug("Schema validation already complete for this TestDoc; skipping!");
		}
		else if (!modelConfig.getSchemaValidateEnable()) {
			log.debug("Schema validation is not enabled for '" + 
					modelConfig.getModelName() + "'; skipping!");
		}
		else {
			SchemaValidator schemaValidator = schemaValidatorFactory.getSchemaValidator(modelConfig);
			testDoc.getResults().setSchemaValidateResult(new Result());
			schemaValidator.validate(testDoc);
		}
	}
}
