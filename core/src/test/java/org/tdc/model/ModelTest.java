package org.tdc.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.ModelDefFactoryImpl;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.ModelInstFactoryImpl;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;
import org.tdc.util.MPathModelWriterForTesting;
import org.tdc.util.SummaryModelWriterForTesting;

/**
 * Unit tests for {@link ModelDef} and {@link ModelInst}.
 * 
 * <p>These tests output various Models to an intermediary String format, 
 * which is then compared with expected results to determine pass or fail.
 * 
 * @see org.tdc.util.AbstractModelWriterForTesting AbstractModelWriterForTesting
 */
public class ModelTest {
	
	// TODO these tests need quite a bit of work still
	
	private static final int INDENT_SIZE = 3;
	
	private static SpreadsheetFileFactory spreadsheetFileFactory;
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;

	private static SchemaFactory schemaFactory;
	private static ModelDefFactory modelDefFactory;
	private static ModelInstFactory modelInstFactory;
	
/*
	 NOTES
	 
	 test modelDef = modelDef
	 test modelInst = modelInst
	 test mpath modelDef = mpath modelDef
	 test mpath modelInst = mpath modelInst
	 test size modelDef = size modelDef
	 test size modelInst = size modelInst
	 test mpath modelDef = mpath modelInst (for those records that exist) (how??)
	
	 === SCHEMA SCENARIOS ===
	 
	 - broken schema
	 - repeating elements
	 - root vs. regular elements
	 - sequence
	 - choice
	 - all
	 - group
	 - complex
	 - simple
	 ------------
	 - root element is sequence repeating
	 - root element is choice
	 - contains group
	 - extension
	 - all
	 - enumeration
	 
	 - flattening situations
	 - ensure default repeating does not exceed maximum for type in question
	 ------------
	 Schema scenarios list
	 ------------
	 - root element, complex, 1..1, sequence, 3 simple children
	 - root element, complex, 1..1, choice, 3 simple children
	 - root element, complex, 1..1, all, 3 simple children
	 - root element, complex, 0..2, sequence, 3 simple children
	 - root element, complex, 0..2, choice, 3 simple children
	 - root element, complex, 0..2, all, 3 simple children
	

		// flatten compositor if:
		// - it is a sequence with one and only one occurrence (and)
		//   - its parent is an element (or)
		//   - its parent is a compositor which is NOT a choice
		//     (because if it's a choice, it needs to be clearly marked as such in the output, so it can't be flattened)
 */
	
	
	@BeforeClass
	public static void setup() {
		Path schemaRoot = Paths.get("testfiles/TDCFiles/Schemas");
		
		spreadsheetFileFactory = new ExcelSpreadsheetFileFactory();

		schemaConfigFactory = new SchemaConfigFactoryImpl(schemaRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);

		schemaFactory = new SchemaFactoryImpl();
		modelDefFactory = new ModelDefFactoryImpl(schemaFactory, spreadsheetFileFactory);
		modelInstFactory = new ModelInstFactoryImpl(modelDefFactory);
	}
	
//	@Test
//	public void test1() {
//		String modelAddr = "Test/TestSchemaV1.0/Model_OldTest";
//		System.out.println("--------------------------------------------------");
//		outputModelDefSummary(modelAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelDefMPath(modelAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelInstSummary(modelAddr);
//		System.out.println("--------------------------------------------------");
//		outputModelInstMPath(modelAddr);
//		compareModelDefSummary(modelAddr, "ModelDefSummary.txt");
//		compareModelDefMPath(modelAddr, "ModelDefMPath.txt");
//		compareModelInstSummary(modelAddr, "ModelInstSummary.txt");
//		compareModelInstMPath(modelAddr, "ModelInstMPath.txt");
//	}
	
	@Test
	public void testVariousModelDefMpathCreatedCorrectly() {
		compareModelDefMPath("Test/TestSchemaV1.0/Model_TestVarious", "ModelDefMPath_Various.txt");
	}
	
	@Test
	public void testVariousModelDefStructureCreatedCorrectly() {
		compareModelDefSummary("Test/TestSchemaV1.0/Model_TestVarious", "ModelDefSummary_Various.txt");
	}
	
	@Test
	public void oldTestModelDefStructureCreatedCorrectly() {
		compareModelDefSummary("Test/TestSchemaV1.0/Model_OldTest", "ModelDefSummary.txt");
	}
	
	@Test
	public void oldTestModelDefMpathCreatedCorrectly() {
		compareModelDefMPath("Test/TestSchemaV1.0/Model_OldTest", "ModelDefMPath.txt");
	}
	
	@Test
	public void oldTestModelInstStructureCreatedCorrectly() {
		compareModelInstSummary("Test/TestSchemaV1.0/Model_OldTest", "ModelInstSummary.txt");
	}
	
	@Test
	public void oldTestModelInstMpathCreatedCorrectly() {
		compareModelInstMPath("Test/TestSchemaV1.0/Model_OldTest", "ModelInstMPath.txt");
	}

	@Test
	public void oldTestModelDefCustomizedStructureCreatedCorrectly() {
		compareModelDefSummary("Test/TestSchemaV1.0/Model_OldTest_Customized", "ModelDefSummary.txt");
	}
	
	@Test
	public void oldTestModelDefCustomizedMpathCreatedCorrectly() {
		compareModelDefMPath("Test/TestSchemaV1.0/Model_OldTest_Customized", "ModelDefMPath.txt");
	}
	
	@Test
	public void oldTestModelInstCustomizedStructureCreatedCorrectly() {
		compareModelInstSummary("Test/TestSchemaV1.0/Model_OldTest_Customized", "ModelInstSummary_Customized.txt");
	}
	
	@Test
	public void oldTestModelInstCustomizedMpathCreatedCorrectly() {
		compareModelInstMPath("Test/TestSchemaV1.0/Model_OldTest_Customized", "ModelInstMPath_Customized.txt");
	}

	@Test
	public void oldTestModelDefFilteredStructureCreatedCorrectly() {
		compareModelDefSummary("Test/TestSchemaV1.0/Model_OldTest_Filtered", "ModelDefSummary_Filtered.txt");
	}
	
	@Test
	public void oldTestModelDefFilteredMpathCreatedCorrectly() {
		compareModelDefMPath("Test/TestSchemaV1.0/Model_OldTest_Filtered", "ModelDefMPath_Filtered.txt");
	}
	
	// ------------------------------
	
	private void compareModelDefSummary(String addrStr, String resourceName) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		compareSummary(rootNode, resourceName);
	}
	
	private void compareModelDefMPath(String addrStr, String resourceName) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		compareMPath(rootNode, resourceName);
	}
	
	private void compareModelInstSummary(String addrStr, String resourceName) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		compareSummary(rootNode, resourceName);
	}
	
	private void compareModelInstMPath(String addrStr, String resourceName) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		compareMPath(rootNode, resourceName);
	}
	
	// ------------------------------
	
	// for when it's necessary to output a ModelDef summary
	private void outputModelDefSummary(String addrStr) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		outputSummary(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelDef mpath list
	private void outputModelDefMPath(String addrStr) {
		ModelDef modelDef = getModelDef(addrStr);
		TDCNode rootNode = modelDef.getRootElement();
		outputMPath(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelInst summary
	private void outputModelInstSummary(String addrStr) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		outputSummary(rootNode, addrStr);
	}

	// for when it's necessary to output a ModelInst mpath list
	private void outputModelInstMPath(String addrStr) {
		ModelInst modelInst = getModelInst(addrStr);
		TDCNode rootNode = modelInst.getRootElement();
		outputMPath(rootNode, addrStr);
	}
	
	// ------------------------------
	
	private ModelDef getModelDef(String addrStr) {
		Addr modelAddr = new Addr(addrStr);
		ModelConfig modelConfig = modelConfigFactory.getModelConfig(modelAddr);
		return modelDefFactory.getModelDef(modelConfig);
	}
	
	private ModelInst getModelInst(String addrStr) {
		Addr modelAddr = new Addr(addrStr);
		ModelConfig modelConfig = modelConfigFactory.getModelConfig(modelAddr);
		return modelInstFactory.getModelInst(modelConfig);
	}
	
	private void compareSummary(TDCNode rootNode, String resourceName) {
		SummaryModelWriterForTesting writer = new SummaryModelWriterForTesting(rootNode, INDENT_SIZE);
		List<String> actualLines = writer.writeToStringList();
		List<String> expectedLines = readResourceToStringList(resourceName);
		assertThat(actualLines).isEqualTo(expectedLines);
	}
	
	private void compareMPath(TDCNode rootNode, String resourceName) {
		MPathModelWriterForTesting writer = new MPathModelWriterForTesting(rootNode, 0);
		List<String> actualLines = writer.writeToStringList();
		List<String> expectedLines = readResourceToStringList(resourceName);
		assertThat(actualLines).isEqualTo(expectedLines);
	}
	
	private void outputSummary(TDCNode rootNode, String addrStr) {
		SummaryModelWriterForTesting writer = new SummaryModelWriterForTesting(rootNode, INDENT_SIZE);
		String actualLines = writer.writeToString();
		System.out.println("Actual lines for Addr: " + addrStr);
		System.out.println(actualLines);
	}

	private void outputMPath(TDCNode rootNode, String addrStr) {
		MPathModelWriterForTesting writer = new MPathModelWriterForTesting(rootNode, 0);
		String actualLines = writer.writeToString();
		System.out.println("Actual lines for Addr: " + addrStr);
		System.out.println(actualLines);
	}

	private List<String> readResourceToStringList(String resourceName) {
		List<String> lines = new ArrayList<>();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
		} 
		catch (FileNotFoundException e) {
			// wrapping as RuntimeException; impossible to code for 'fixing' this issue at runtime
			throw new RuntimeException("Unable to read resource file: " + file.toString(), e);
		}
		return lines;
	}
}
