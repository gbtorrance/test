package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * Defines functionality for factories for {@link Evaluator} objects.
 */
public interface EvaluatorFactory {
	/**
	 * Creates an {@link Evaluator}.
	 *
	 * @param config Contains configuration information for the Evaluator to be created.
	 * @param configKey The key (in Apache Commons Configuration format} 
	 *        to the root element of the new Evaluator.
	 * @param defaultStyle Default CellStyle, if needed, for ValuePlusStyleEvaluatorFactory.
	 * @return An {@link Evaluator}
	 */
	Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle);
}
