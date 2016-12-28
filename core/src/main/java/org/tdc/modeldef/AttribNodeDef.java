package org.tdc.modeldef;

import org.tdc.model.AttribNode;
import org.tdc.model.ModelVisitor;

/**
 * A {@link NodeDef} implementation specific to attributes.
 */
public class AttribNodeDef extends NodeDef implements AttribNode {
	
	private String name;
	private boolean isRequired;
	
	public AttribNodeDef(ElementNodeDef parent, ModelDefSharedState sharedState) {
		super(parent, sharedState);
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		getSharedState().throwExceptionIfImmutable("setName");
		this.name = name;
	}
	
	@Override
	public boolean isRequired() {
		return isRequired;
	}
	
	public void setRequired(boolean isRequired) {
		getSharedState().throwExceptionIfImmutable("setRequired");
		this.isRequired = isRequired;
	}

	@Override
	public ElementNodeDef getParent() {
		return (ElementNodeDef)super.getParent();
	}

	@Override
	public String getDispName() {
		return "@" + getName();
	}
	
	@Override 
	public String getDispOccurs() {
		return isRequired ? "1..1" : "0..1";
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
