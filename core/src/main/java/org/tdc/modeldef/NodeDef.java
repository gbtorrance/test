package org.tdc.modeldef;

import org.tdc.model.AbstractTDCNode;
import org.tdc.util.Util;

/**
 * {@link AbstractTDCNode} implementation specific to 'definition' nodes. 
 * 
 * @see AttribNodeDef
 * @see NonAttribNodeDef
 */
public abstract class NodeDef extends AbstractTDCNode {
	
	private int occursCountOverride = Util.UNDEFINED;
	
	public NodeDef(NonAttribNodeDef parent, ModelDefSharedState sharedState) {
		super(parent, sharedState);
	}
	
	@Override
	public NonAttribNodeDef getParent() {
		return (NonAttribNodeDef)super.getParent();
	}

	@Override
	public ModelDefSharedState getSharedState() {
		return (ModelDefSharedState)super.getSharedState();
	}

	public int getOccursCountOverride() {
		return occursCountOverride;
	}
	
	public void setOccursCountOverride(int occursCountOverride) {
		getSharedState().throwExceptionIfImmutable("setOccursCountOverride");
		this.occursCountOverride = occursCountOverride;
	}
	
	@Override
	public String getVariable(String name) {
		return getSharedState().getVariable(name, this);
	}
	
	public void setVariable(String name, String value) {
		getSharedState().setVariable(name, this, value);
	}
}
