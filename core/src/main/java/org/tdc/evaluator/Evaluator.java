package org.tdc.evaluator;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * Defines core functionality for Evaluator objects.
 * 
 * <p>The use of different types of Evaluators (often nested within each other)
 * enables both the configuration and runtime extraction of some fairly
 * complex values from various sources.
 * 
 * <p>Used primarily with modelCustomizers to support the initial creation
 * and subsequent updating of customizer spreadsheets. 
 * 
 * <p>On the subject of updating customizer spreadsheets when schemas are updated: 
 * This has typically been a very manual, time-consuming process. 
 * Evaluator classes will enable this to be done in a much more efficient manner.
 */
public interface Evaluator {
	/**
	 * Calculates and returns a result based on the Evaluator 'type'.
	 *
	 * @param currentNode Node from current model (for variable or property information).
	 * @param prevNode Node from previous model (for variable or property information);
	 *        only provided if an upgrade from a previous model is being done.
	 * @return Result of evaluation; includes a String value 
	 *         (plus optional additional information such as cell style)
	 */
	ValueResult evaluate(TDCNode currentNode, TDCNode prevNode);
}
