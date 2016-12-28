package org.tdc.config.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link BookConfig} an its related classes.
 */
public class BookConfigTest {
	private static Path schemasConfigRoot;
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
	private static FilterConfigFactory filterConfigFactory;
	private static TaskConfigFactory taskConfigFactory;
	private static Path booksConfigRoot;
	private static BookConfigFactory bookConfigFactory;
	private static Addr bookAddr;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		schemasConfigRoot = Paths.get("testfiles/TDCFiles/Schemas");
		schemaConfigFactory = new SchemaConfigFactoryImpl(schemasConfigRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		filterConfigFactory = new FilterConfigFactoryImpl();
		taskConfigFactory = new TaskConfigFactoryImpl();
		booksConfigRoot = Paths.get("testfiles/TDCFiles/Books");
		bookConfigFactory = new BookConfigFactoryImpl(
				booksConfigRoot, modelConfigFactory, 
				filterConfigFactory, taskConfigFactory);
		bookAddr = new Addr("/ConfigTest/BookConfigTest");
	}
	
	@Test
	public void testCachingReturnsSameConfig() {
		BookConfig bookConfig1 = bookConfigFactory.getBookConfig(bookAddr);
		BookConfig bookConfig2 = bookConfigFactory.getBookConfig(bookAddr);
		assertThat(bookConfig1).isEqualTo(bookConfig2);
	}
	
	@Test
	public void testValidConfig() {
		BookConfig bookConfig = bookConfigFactory.getBookConfig(bookAddr);
		assertThat(bookConfig.getBooksConfigRoot()).isEqualTo(booksConfigRoot);
		assertThat(bookConfig.getAddr()).isEqualTo(bookAddr);
		assertThat(bookConfig.getBookConfigRoot()).isEqualTo(booksConfigRoot.resolve(bookAddr.toString()));

		CellStyle style;
		style = bookConfig.getDefaultStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(null);
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();

		style = bookConfig.getNodeHeaderStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getBold()).isEqualTo(true);
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(null);
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();
		
		style = bookConfig.getDefaultNodeDetailStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(null);
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isEqualTo(true);
		
		style = bookConfig.getDefaultNodeStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(10);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(null);
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();

		style = bookConfig.getParentNodeStyle();
		assertThat(style.getFontName()).isEqualTo("Arial");
		assertThat(style.getFontHeight()).isEqualTo(12);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isEqualTo(false);
		assertThat(style.getColor()).isEqualTo(new Color(0, 0, 255));
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();

		style = bookConfig.getAttribNodeStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(13);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(null);
		assertThat(style.getFillColor()).isEqualTo(new Color(34, 97, 13));
		assertThat(style.getShrinkToFit()).isNull();

		style = bookConfig.getCompositorNodeStyle();
		assertThat(style.getFontName()).isEqualTo("Times New Roman");
		assertThat(style.getFontHeight()).isEqualTo(10);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isEqualTo(true);
		assertThat(style.getColor()).isEqualTo(new Color(64, 64, 64));
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();

		style = bookConfig.getChoiceMarkerNodeStyle();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(10);
		assertThat(style.getBold()).isNull();
		assertThat(style.getItalic()).isNull();
		assertThat(style.getColor()).isEqualTo(new Color(255, 0, 0));
		assertThat(style.getFillColor()).isEqualTo(null);
		assertThat(style.getShrinkToFit()).isNull();

		Map<String, DocTypeConfig> docTypeConfigs = bookConfig.getDocTypeConfigs();
		DocTypeConfig dtConfig1 = docTypeConfigs.get("DocType1");
		assertThat(dtConfig1.getDocTypeName()).isEqualTo("DocType1");
		assertThat(dtConfig1.getMinPerTestCase()).isEqualTo(0);
		assertThat(dtConfig1.getMaxPerTestCase()).isEqualTo(2);
		DocTypeConfig dtConfig2 = docTypeConfigs.get("DocType2");
		assertThat(dtConfig2.getDocTypeName()).isEqualTo("DocType2");
		assertThat(dtConfig2.getMinPerTestCase()).isEqualTo(0);
		assertThat(dtConfig2.getMaxPerTestCase()).isEqualTo(1);
		DocTypeConfig dtConfig3 = docTypeConfigs.get("DocType3");
		assertThat(dtConfig3.getDocTypeName()).isEqualTo("DocType3");
		assertThat(dtConfig3.getMinPerTestCase()).isEqualTo(1); // defaults
		assertThat(dtConfig3.getMaxPerTestCase()).isEqualTo(1); // defaults

		Map<String, PageConfig> pageConfigs = bookConfig.getPageConfigs();
		PageConfig pConfig1 = pageConfigs.get("Page1");
		assertThat(pConfig1.getPageName()).isEqualTo("Page1");
		assertThat(pConfig1.getModelConfig().getAddr()).isEqualTo(new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest"));
		assertThat(pConfig1.getDocTypeConfig()).isEqualTo(dtConfig1);
		PageConfig pConfig2 = pageConfigs.get("Page2");
		assertThat(pConfig2.getPageName()).isEqualTo("Page2");
		assertThat(pConfig2.getModelConfig().getAddr()).isEqualTo(new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest"));
		assertThat(pConfig2.getDocTypeConfig()).isEqualTo(dtConfig2);
		PageConfig pConfig3 = pageConfigs.get("Page3");
		assertThat(pConfig3.getPageName()).isEqualTo("Page3");
		assertThat(pConfig3.getModelConfig().getAddr()).isEqualTo(new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest"));
		assertThat(pConfig3.getDocTypeConfig()).isEqualTo(dtConfig3);
		PageConfig pConfig4 = pageConfigs.get("Page4");
		assertThat(pConfig4.getPageName()).isEqualTo("Page4");
		assertThat(pConfig4.getModelConfig().getAddr()).isEqualTo(new Addr("/ConfigTest/SchemaConfigTest/ModelConfigTest"));
		assertThat(pConfig4.getDocTypeConfig()).isEqualTo(dtConfig1); // intentionally use one that was used before
	}
	
	@Test
	public void testDocTypeNotFound() {
		Addr bookAddrDocTypeMissing = new Addr("/ConfigTest/BookConfigTest_DocTypeMissing");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to locate DocType 'DocTypeNotInConfig' for Page 'Page1'");
		bookConfigFactory.getBookConfig(bookAddrDocTypeMissing);
	}

	@Test
	public void testConfigRootDoesNotExist() {
		Addr bookAddrConfigRootDoesNotExist = new Addr("/ConfigTest/BookConfigTest_DoesNotExit");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("BookConfig root dir does not exist:");
		bookConfigFactory.getBookConfig(bookAddrConfigRootDoesNotExist);
	}

	@Test
	public void testConfigXmlDoesNotExist() {
		Addr bookAddrConfigXMLMissing = new Addr("/ConfigTest/BookConfigTest_ConfigXMLMissing");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to locate config file:");
		bookConfigFactory.getBookConfig(bookAddrConfigXMLMissing);
	}
}
