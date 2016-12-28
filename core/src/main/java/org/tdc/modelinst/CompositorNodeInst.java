package org.tdc.modelinst;

import org.tdc.model.CompositorNode;
import org.tdc.model.CompositorType;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;
import org.tdc.modeldef.CompositorNodeDef;

/**
 * A {@link NonAttribNodeInst} implementation specific to compositors.
 * 
 * <p>Compositors (such as "sequence" and "choice") are nodes that can contain other nodes.
 */
public class CompositorNodeInst extends NonAttribNodeInst implements CompositorNode {
	
	public CompositorNodeInst(NonAttribNodeInst parent, ModelInstSharedState sharedState, CompositorNodeDef compositorNodeDef) {
		super(parent, sharedState, compositorNodeDef);
	}

	@Override
	public CompositorNodeDef getNodeDef() {
		return (CompositorNodeDef)super.getNodeDef();
	}

	@Override
	public CompositorType getCompositorType() {
		return getNodeDef().getCompositorType();
	}

	@Override
	public String getDispName() {
		return getNodeDef().getDispName();
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		for (NonAttribNode nonAttrib : getChildren()) {
			nonAttrib.accept(visitor);
		}
	}
}
