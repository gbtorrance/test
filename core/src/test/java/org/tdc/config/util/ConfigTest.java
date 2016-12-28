package org.tdc.config.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.spreadsheet.CellAlignment;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.util.Util;

/**
 * Unit tests for {@link Config} and its related classes.
 */
public class ConfigTest {
	
	private static Config config;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		ClassLoader classLoader = ConfigTest.class.getClassLoader();
		Path path = Paths.get(classLoader.getResource(
				"ConfigTest.xml").getPath().toString());
		config = new ConfigImpl.Builder(path).build();
	}

	@Test
	public void testConfigXmlDoesNotExistThrowsException() {
		Path path = Paths.get("/PathDoesNotExist");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to locate config file:");
		new ConfigImpl.Builder(path).build();
	}

	@Test
	public void testInvalidXmlThrowsException() {
		ClassLoader classLoader = ConfigTest.class.getClassLoader();
		Path path = Paths.get(classLoader.getResource(
				"ConfigTest_InvalidXML.xml").getPath().toString());
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to load config file:");
		new ConfigImpl.Builder(path).build();
	}
	
	@Test
	public void testStringElementFound() {
		String found = config.getString("StringElement", null, false);
		assertThat(found).isEqualTo("TestString");
	}
	
	@Test
	public void testStringElementNotFoundUseDefault() {
		String notFound = config.getString("StringElement_NotFound", "Default", false);
		assertThat(notFound).isEqualTo("Default");
	}

	@Test
	public void testStringElementNotFoundButRequiredThrowsException() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getString("DoesNotExist", null, true);
	}

	@Test
	public void testIntElementFound() {
		int found = config.getInt("IntElement", 0, false);
		assertThat(found).isEqualTo(1);
	}
	
	@Test
	public void testIntElementNotFoundUseDefault() {
		int notFound = config.getInt("IntElement_NotFound", 5, false);
		assertThat(notFound).isEqualTo(5);
	}

	@Test
	public void testIntElementRequiredButNotFoundThrowsException() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getInt("DoesNotExist", 0, true);
	}

	@Test
	public void testDoubleElementFound() {
		double found = config.getDouble("DoubleElement", 0, false);
		assertThat(found).isEqualTo(1.5);
	}
	
	@Test
	public void testDoubleElementNotFoundUseDefault() {
		double notFound = config.getDouble("DoubleElement_NotFound", 5.5, false);
		assertThat(notFound).isEqualTo(5.5);
	}

	@Test
	public void testDoubleElementRequiredButNotFoundThrowsException() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getDouble("DoesNotExist", 0, true);
	}

	@Test
	public void testBooleanElementFound() {
		boolean foundTrue = config.getBoolean("BooleanElementTrue", false, false);
		boolean foundYes = config.getBoolean("BooleanElementYes", false, false);
		boolean foundFalse = config.getBoolean("BooleanElementFalse", true, false);
		boolean foundNo = config.getBoolean("BooleanElementNo", true, false);
		assertThat(foundTrue).isEqualTo(true);
		assertThat(foundYes).isEqualTo(true);
		assertThat(foundFalse).isEqualTo(false);
		assertThat(foundNo).isEqualTo(false);
	}
	
	@Test
	public void testBooleanElementNotFoundUseDefault() {
		boolean notFoundTrue = config.getBoolean("BooleanElement_NotFound", true, false);
		boolean notFoundFalse = config.getBoolean("BooleanElement_NotFound", false, false);
		assertThat(notFoundTrue).isEqualTo(true);
		assertThat(notFoundFalse).isEqualTo(false);
	}

	@Test
	public void testBooleanElementRequiredButNotFoundThrowsException() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getInt("DoesNotExist", 0, true);
	}
	
	@Test
	public void testCountOfRepeatingElements() {
		assertThat(config.getCount("RepeatingElement")).isEqualTo(3);
	}

	@Test
	public void testCountOfElementThatDoesNotExist() {
		assertThat(config.getCount("RepeatingElement_DoesNotExist")).isEqualTo(0);
	}

	@Test
	public void testMaxIndexOfRepeatingElements() {
		assertThat(config.getMaxIndex("RepeatingElement")).isEqualTo(2);
	}

	@Test
	public void testMaxIndexOfElementThatDoesNotExist() {
		assertThat(config.getMaxIndex("RepeatingElement_DoesNotExist")).isEqualTo(Util.UNDEFINED);
	}
	
	@Test
	public void testHasNodeFoundEmptyNode() {
		assertThat(config.hasNode("EmptyElement")).isEqualTo(true);
	}

	@Test
	public void testHasNodeFoundParentNode() {
		assertThat(config.hasNode("ParentElement")).isEqualTo(true);
	}

	@Test
	public void testHasNodeNotFound() {
		assertThat(config.hasNode("DoesNotExist")).isEqualTo(false);
	}
	
	@Test
	public void testCellStyleFound() {
		CellStyle style = new CellStyleImpl.Builder().setFromConfig(
				config, "CellStyle", null, false).build();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getColorRed()).isEqualTo(50);
		assertThat(style.getColorGreen()).isEqualTo(100);
		assertThat(style.getColorBlue()).isEqualTo(150);
		assertThat(style.getItalic()).isEqualTo(true);
	}

	@Test
	public void testCellStyleNotFoundUseDefault() {
		CellStyle defaultStyle = new CellStyleImpl.Builder()
				.setFontName("Arial")
				.setFontHeight(12.0)
				.setColor(new Color(10, 20, 30))
				.setFillColor(new Color(75, 70, 65))
				.setItalic(true)
				.setBold(true)
				.setShrinkToFit(true)
				.setAlignment(CellAlignment.CENTER)
				.setFormat("text")
				.build();
		CellStyle style = new CellStyleImpl.Builder().setFromConfig(
				config, "CellStyle_DoesNotExist", defaultStyle, false).build();
		assertThat(style.getFontName()).isEqualTo("Arial");
		assertThat(style.getFontHeight()).isEqualTo(12);
		assertThat(style.getColorRed()).isEqualTo(10);
		assertThat(style.getColorGreen()).isEqualTo(20);
		assertThat(style.getColorBlue()).isEqualTo(30);
		assertThat(style.getFillColorRed()).isEqualTo(75);
		assertThat(style.getFillColorGreen()).isEqualTo(70);
		assertThat(style.getFillColorBlue()).isEqualTo(65);
		assertThat(style.getItalic()).isEqualTo(true);
		assertThat(style.getBold()).isEqualTo(true);
		assertThat(style.getShrinkToFit()).isEqualTo(true);
		assertThat(style.getAlignment()).isEqualTo(CellAlignment.CENTER);
		assertThat(style.getFormat()).isEqualTo("text");
	}
}
