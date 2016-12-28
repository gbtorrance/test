package org.tdc.model;

/**
 * Highest-level node in either Model hierarchy ({@link org.tdc.modeldef.ModelDef ModelDef} or {@link org.tdc.modelinst.ModelInst ModelInst}).
 * 
 * <p>Named with "TDC" prefix to avoid confusion with w3c {@link org.w3c.dom.Node Node} object.
 */
public interface TDCNode extends CanHaveParent, CanHaveVariables, Addressable, Displayable, Visitable {

}
