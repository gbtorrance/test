package org.tdc.config.schema;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link SchemaConfig} and its related classes.
 */
public class SchemaConfigTest {
	private static Path schemasConfigRoot;
	private static SchemaConfigFactory schemaConfigFactory;
	private static Addr schemaAddr;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
		schemaAddr = new Addr("/ConfigTest/SchemaConfigTest");
	}
	
	@Test
	public void testCachingReturnsSameConfig() {
		SchemaConfig schemaConfig1 = schemaConfigFactory.getSchemaConfig(schemaAddr);
		SchemaConfig schemaConfig2 = schemaConfigFactory.getSchemaConfig(schemaAddr);
		assertThat(schemaConfig1).isEqualTo(schemaConfig2);
	}
	
	@Test
	public void testValidConfig() {
		SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(schemaAddr);
		assertThat(schemaConfig.getSchemasConfigRoot()).isEqualTo(schemasConfigRoot);
		assertThat(schemaConfig.getAddr()).isEqualTo(schemaAddr);
		assertThat(schemaConfig.getSchemaConfigRoot()).isEqualTo(schemasConfigRoot.resolve(schemaAddr.toString()));
		assertThat(schemaConfig.getSchemaFilesRoot()).isEqualTo(schemasConfigRoot.resolve(schemaAddr.toString()).resolve("SchemaFiles"));
	}
	
	@Test
	public void testConfigRootDoesNotExist() {
		Addr schemaAddrConfigRootDoesNotExist = new Addr("/ConfigTest/SchemaConfigTest_DoesNotExit");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("SchemaConfig root dir does not exist:");
		schemaConfigFactory.getSchemaConfig(schemaAddrConfigRootDoesNotExist);
	}

	@Test
	public void testConfigXmlDoesNotExist() {
		Addr schemaAddrConfigXMLMissing = new Addr("/ConfigTest/SchemaConfigTest_ConfigXMLMissing");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to locate config file:");
		schemaConfigFactory.getSchemaConfig(schemaAddrConfigXMLMissing);
	}

	@Test
	public void testSchemaFilesDirDoesNotExist() {
		Addr schemaAddrSchemaFilesDirMissing = new Addr("/ConfigTest/SchemaConfigTest_SchemaFilesDirMissing");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Schema files root dir does not exist:");
		schemaConfigFactory.getSchemaConfig(schemaAddrSchemaFilesDirMissing);
	}
}
