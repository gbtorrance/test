package org.tdc.modelinst;

import org.tdc.model.AttribNode;
import org.tdc.model.ModelVisitor;
import org.tdc.modeldef.AttribNodeDef;

/**
 * A {@link NodeInst} implementation specific to attribute nodes.
 */
public class AttribNodeInst extends NodeInst implements AttribNode {
	
	public AttribNodeInst(ElementNodeInst parent, ModelInstSharedState sharedState, AttribNodeDef attribNodeDef) {
		super(parent, sharedState, attribNodeDef);
	}
	
	@Override
	public AttribNodeDef getNodeDef() {
		return (AttribNodeDef)super.getNodeDef();
	}

	@Override
	public String getName() {
		return getNodeDef().getName();
	}
	
	@Override
	public boolean isRequired() {
		return getNodeDef().isRequired();
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean isChildOfChoice() {
		// attributes will always be children of elements, nothing else
		return false;
	}
}
