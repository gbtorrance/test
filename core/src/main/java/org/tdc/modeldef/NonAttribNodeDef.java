package org.tdc.modeldef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.CompositorType;
import org.tdc.model.NonAttribNode;

/**
 * A {@link NodeDef} implementation specific to nodes that are NOT attributes (such as compositors and elements).
 * 
 * @see CompositorNodeDef
 * @see ElementNodeDef
 */
public abstract class NonAttribNodeDef extends NodeDef implements NonAttribNode {
	
	public static final int MIN_MAX_UNDEFINED = -2;
	public static final int MAX_UNBOUNDED = -1;

	private int minOccurs = MIN_MAX_UNDEFINED;
	private int maxOccurs = MIN_MAX_UNDEFINED;
	
	private List<NonAttribNodeDef> children = new ArrayList<>();
	
	protected NonAttribNodeDef(NonAttribNodeDef parent, ModelDefSharedState sharedState) {
		super(parent, sharedState);
	}
	
	@Override
	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		getSharedState().throwExceptionIfImmutable("setMinOccurs");
		this.minOccurs = minOccurs;
	}

	@Override
	public int getMaxOccurs() {
		return maxOccurs;
	}
	
	public void setMaxOccurs(int maxOccurs) {
		getSharedState().throwExceptionIfImmutable("setMaxOccurs");
		this.maxOccurs = maxOccurs;
	}

	@Override
	public boolean isUnbounded() {
		return maxOccurs == MAX_UNBOUNDED;
	}

	@Override
	public List<NonAttribNodeDef> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public boolean hasChild() {
		return children.size() > 0;
	}
	
	public void addChild(NonAttribNodeDef child) {
		getSharedState().throwExceptionIfImmutable("addChild");
		children.add(child);
	}
	
	public void removeChild(NonAttribNodeDef child) {
		getSharedState().throwExceptionIfImmutable("removeChild");
		children.remove(child);
	}
	
	@Override
	public boolean isChildOfChoice() {
		boolean result = false;
		NonAttribNodeDef parentNode = getParent();
		if (parentNode != null && 
				parentNode instanceof CompositorNodeDef &&
				((CompositorNodeDef)parentNode).getCompositorType() == CompositorType.CHOICE) {
			result = true;
		}
		return result;
	}

	@Override 
	public String getDispOccurs() {
		return minOccurs + ".." + (maxOccurs == MAX_UNBOUNDED ? "n" : maxOccurs);
	}
}
