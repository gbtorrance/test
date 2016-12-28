package org.tdc.model;

/**
 * Interface for model visitors (according to the visitor pattern).
 */
public interface ModelVisitor {
	void visit(AttribNode attribNode);
	void visit(CompositorNode compositorNode);
	void visit(ElementNode elementNode);
}
