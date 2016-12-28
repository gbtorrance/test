package org.tdc.model;

/**
 * Defines functionality of nodes that can be repeated (i.e. have multiple occurrences).
 */
public interface Repeatable {
	int getMinOccurs();
	int getMaxOccurs();
	boolean isUnbounded();
}
