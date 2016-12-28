package org.tdc.model;

/**
 * Interface for non-attribute nodes (i.e. nodes other than attribute nodes). 
 * 
 * <p>Non-attribute nodes include elements and compositors (which are used for "composing" other elements and compositors).
 */
public interface NonAttribNode extends TDCNode, CanHaveChildren, Repeatable {

}
