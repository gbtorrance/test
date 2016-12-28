package org.tdc.model;

/**
 * Interface for compositor nodes. 
 */
public interface CompositorNode extends NonAttribNode {
	CompositorType getCompositorType();
}
