package org.tdc.model;

/**
 * Interface for nodes that can be visited (according to the visitor pattern).
 */
public interface Visitable {
	void accept(ModelVisitor visitor);
}
