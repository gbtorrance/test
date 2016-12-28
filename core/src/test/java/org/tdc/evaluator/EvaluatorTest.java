package org.tdc.evaluator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.util.Config;
import org.tdc.config.util.ConfigImpl;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.evaluator.factory.GeneralEvaluatorFactoryImpl;
import org.tdc.evaluator.result.ValuePlusStyleResult;
import org.tdc.evaluator.result.ValueResult;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDefSharedState;

/**
 * Unit tests for {@link Evaluator} and its related classes.
 */
public class EvaluatorTest {
	
	private static Config config; 
	
	@BeforeClass
	public static void beforeClass() {
		ClassLoader classLoader = EvaluatorTest.class.getClassLoader();
		Path path = Paths.get(classLoader.getResource("EvaluatorConfigTest.xml").getPath());
		config = new ConfigImpl.Builder(path).build();
	}
	
	@Test
	public void testEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestEmpty.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testSpaceDefaultSizeOne() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceDefaultSizeOne.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo(" ");
	}

	@Test
	public void testSpaceSizeZero() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeZero.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testSpaceSizeOne() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeOne.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo(" ");
	}

	@Test
	public void testSpaceSizeFive() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestSpaceSizeFive.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("     ");
	}

	@Test
	public void testLiteral() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestLiteral.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Abc123");
	}

	@Test
	public void testLiteralEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestLiteralEmpty.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCompound() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompound.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("AbCd - Ef");
	}

	@Test
	public void testCompoundNoMatch() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompoundNoMatch.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCompoundEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCompoundEmpty.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCoalesce() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesce.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Ab");
	}

	@Test
	public void testCoalesceNoMatch() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesceNoMatch.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testCoalesceEmpty() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestCoalesceEmpty.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testValuePlusStyle() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyle.Evaluator", null);
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Abc");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testValuePlusStyleInsideIf() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyleInsideIf.Evaluator", null);
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testValuePlusStyleInsideCoalesce() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestValuePlusStyleInsideCoalesce.Evaluator", null);
		ValuePlusStyleResult result = (ValuePlusStyleResult)evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Inside coalesce");
		assertThat(result.getCellStyle().getFontName()).isEqualTo("Calibri");
		assertThat(result.getCellStyle().getFontHeight()).isEqualTo(11);
	}

	@Test
	public void testIfEqualsOperatorEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorEquals.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
	}

	@Test
	public void testIfEqualsOperatorNotEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorNotEquals.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Else block");
	}

	@Test
	public void testIfNotEqualsOperatorEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfNotEqualsOperatorEquals.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Else block");
	}

	@Test
	public void testIfNotEqualsOperatorNotEquals() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfNotEqualsOperatorNotEquals.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Then block");
	}
	
	@Test
	public void testIfEqualsOperatorNotEqualsAndNoElseBlock() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfEqualsOperatorNotEqualsAndNoElseBlock.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("");
	}

	@Test
	public void testIfInsideIf() {
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestIfInsideIf.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, null);
		assertThat(result.getValue()).isEqualTo("Inner Else block");
	}
	
	@Test
	public void testVariableCurrent() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setVariable("TEST_VARIABLE", "my value");
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestVariableCurrent.Evaluator", null);
		ValueResult result = evaluator.evaluate(element, null);
		assertThat(result.getValue()).isEqualTo("my value");
	}

	@Test
	public void testVariablePrevious() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setVariable("TEST_VARIABLE", "my value");
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestVariablePrevious.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, element);
		assertThat(result.getValue()).isEqualTo("my value");
	}

	@Test
	public void testPropertyCurrent() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setMaxOccurs(125);
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestPropertyCurrent.Evaluator", null);
		ValueResult result = evaluator.evaluate(element, null);
		assertThat(result.getValue()).isEqualTo("125");
	}

	@Test
	public void testPropertyPrevious() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setMaxOccurs(125);
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestPropertyPrevious.Evaluator", null);
		ValueResult result = evaluator.evaluate(null, element);
		assertThat(result.getValue()).isEqualTo("125");
	}

	@Test
	public void testPropertyFullMethodName() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setMaxOccurs(125);
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestPropertyFullMethodName.Evaluator", null);
		ValueResult result = evaluator.evaluate(element, null);
		assertThat(result.getValue()).isEqualTo("125");
	}

	@Test
	public void testPropertyWithPropertyNameStartingWithLowerCaseLetter() {
		ModelDefSharedState sharedState = new ModelDefSharedState();
		ElementNodeDef element = new ElementNodeDef(null, sharedState);
		element.setMaxOccurs(125);
		GeneralEvaluatorFactory factory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
		Evaluator evaluator = factory.createEvaluator(config, "TestPropertyLowerCaseFirstLetter.Evaluator", null);
		ValueResult result = evaluator.evaluate(element, null);
		assertThat(result.getValue()).isEqualTo("125");
	}
}
