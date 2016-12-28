package org.tdc.model;

/**
 * Defines functionality for nodes that can have parent nodes.
 */
public interface CanHaveParent {
	TDCNode getParent();
	boolean isChildOfChoice();
}
