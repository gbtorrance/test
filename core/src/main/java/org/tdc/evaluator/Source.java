package org.tdc.evaluator;

/**
 * Defines the possibly "source" values for Variable and Property Evaluators.
 * 
 * <p>Refers to either a "current" model node or a "previous" model node 
 * (for when doing comparisons). 
 */
public enum Source {
	CURRENT,
	PREVIOUS
}
