package org.tdc.modeldef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.AttribNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;

/**
 * A {@link NonAttribNodeDef} implementation specific to XML elements.
 */
public class ElementNodeDef extends NonAttribNodeDef implements ElementNode {
	
	private String name;
	private List<AttribNodeDef> attributes = new ArrayList<>();
	
	public ElementNodeDef(NonAttribNodeDef parent, ModelDefSharedState sharedState) {
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
	public List<AttribNodeDef> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}
	
	@Override
	public boolean hasAttribute() {
		return attributes.size() > 0;
	}
	
	public void addAttribute(AttribNodeDef attribute) {
		getSharedState().throwExceptionIfImmutable("addAttribute");
		attributes.add(attribute);
	}
	
	@Override
	public String getDispName() {
		return name;
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
		for (AttribNode attrib : getAttributes()) {
			attrib.accept(visitor);
		}
		for (NonAttribNode nonAttrib : getChildren()) {
			nonAttrib.accept(visitor);
		}
	}
}
