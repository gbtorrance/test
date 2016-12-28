package org.tdc.config.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link ModelConfig} and its related classes.
 */
public class ModelConfigTest {
	private static Path schemasConfigRoot;
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
	private static Addr modelAddr;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		modelAddr = new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest");
	}
	
	@Test
	public void testCachingReturnsSameConfig() {
		ModelConfig schemaConfig1 = modelConfigFactory.getModelConfig(modelAddr);
		ModelConfig schemaConfig2 = modelConfigFactory.getModelConfig(modelAddr);
		assertThat(schemaConfig1).isEqualTo(schemaConfig2);
	}
	
	@Test
	public void testValidConfig() {
		ModelConfig modelConfig = modelConfigFactory.getModelConfig(modelAddr);
		SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(new Addr("/ConfigTest/SchemaConfigTest"));
		assertThat(modelConfig.getSchemaConfig()).isEqualTo(schemaConfig);
		assertThat(modelConfig.getAddr()).isEqualTo(modelAddr);
		assertThat(modelConfig.getModelConfigRoot()).isEqualTo(schemasConfigRoot.resolve(modelAddr.toString()));
		assertThat(modelConfig.getSchemaRootFile()).isEqualTo("SchemaRootFile.xsd");
		assertThat(modelConfig.getSchemaRootFileFullPath()).isEqualTo(schemaConfig.getSchemaFilesRoot().resolve("SchemaRootFile.xsd"));
		assertThat(modelConfig.getSchemaRootElementName()).isEqualTo("TestRoot");
		assertThat(modelConfig.getSchemaRootElementNamespace()).isEqualTo("http://org.tdc/test");
		assertThat(modelConfig.getFailOnParserWarning()).isEqualTo(true);
		assertThat(modelConfig.getFailOnParserNonFatalError()).isEqualTo(true);
		assertThat(modelConfig.getDefaultOccursCount()).isEqualTo(5);
		assertThat(modelConfig.hasModelCustomizerConfig()).isEqualTo(true);
		ModelCustomizerConfig cust = modelConfig.getModelCustomizerConfig();
		assertThat(cust.getFilePath()).isEqualTo(modelConfig.getModelConfigRoot().resolve("Customizer_ConfigTest.xlsx"));
		assertThat(cust.getDefaultNodeStyle().getFontName()).isEqualTo("Calibri");
		assertThat(cust.getDefaultNodeStyle().getFontHeight()).isEqualTo(11);
		assertThat(cust.getParentNodeStyle().getFontName()).isEqualTo("Arial");
		assertThat(cust.getParentNodeStyle().getFontHeight()).isEqualTo(12);
		assertThat(cust.getParentNodeStyle().getColor()).isEqualTo(new Color(0, 0, 255));
		assertThat(cust.getParentNodeStyle().getItalic()).isEqualTo(false);
		assertThat(cust.getAttribNodeStyle().getFontName()).isEqualTo("Calibri");
		assertThat(cust.getAttribNodeStyle().getFontHeight()).isEqualTo(13);
		assertThat(cust.getAttribNodeStyle().getColor()).isEqualTo(new Color(34, 97, 13));
		assertThat(cust.getCompositorNodeStyle().getFontName()).isEqualTo("Times New Roman");
		assertThat(cust.getCompositorNodeStyle().getFontHeight()).isEqualTo(14);
		assertThat(cust.getCompositorNodeStyle().getColor()).isEqualTo(new Color(64, 64, 64));
		assertThat(cust.getCompositorNodeStyle().getItalic()).isEqualTo(true);
		assertThat(cust.getChoiceMarkerNodeStyle().getFontName()).isEqualTo("Calibri");
		assertThat(cust.getChoiceMarkerNodeStyle().getFontHeight()).isEqualTo(15);
		assertThat(cust.getChoiceMarkerNodeStyle().getColor()).isEqualTo(new Color(255, 0, 0));
		assertThat(cust.getNodeColumnCount()).isEqualTo(12);
		assertThat(cust.getNodeColumnWidth()).isEqualTo(500);
		assertThat(cust.getAllowMinMaxInvalidOccursCountOverride()).isEqualTo(true);
	}
	
	@Test
	public void testConfigRootDoesNotExist() {
		Addr modelAddrConfigRootDoesNotExist = new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest_DoesNotExit");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("ModelConfig root dir does not exist:");
		modelConfigFactory.getModelConfig(modelAddrConfigRootDoesNotExist);
	}

	@Test
	public void testConfigXmlDoesNotExist() {
		Addr modelAddrConfigXMLMissing = new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest_ConfigXMLMissing");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to locate config file:");
		modelConfigFactory.getModelConfig(modelAddrConfigXMLMissing);
	}
}
