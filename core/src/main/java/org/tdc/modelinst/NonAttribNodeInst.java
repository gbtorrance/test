package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tdc.model.NonAttribNode;
import org.tdc.modeldef.NonAttribNodeDef;

/**
 * A {@link NodeInst} implementation specific to nodes that are NOT attributes (such as compositors and elements).
 * 
 * @see CompositorNodeInst
 * @see ElementNodeInst
 */
public abstract class NonAttribNodeInst extends NodeInst implements NonAttribNode {
	
	private final List<NonAttribNodeInst> children = new ArrayList<>();
	
	private int occurNum;
	private int occurCount;
	
	protected NonAttribNodeInst(NonAttribNodeInst parent, ModelInstSharedState sharedState, NonAttribNodeDef nonAttribNodeDef) {
		super(parent, sharedState, nonAttribNodeDef);
	}
	
	@Override
	public List<NonAttribNodeInst> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public boolean hasChild() {
		return children.size() > 0;
	}
	
	public void addChild(NonAttribNodeInst child) {
		getSharedState().throwExceptionIfImmutable("addChild");
		children.add(child);
	}

	public void addChildAll(List<? extends NonAttribNodeInst> childList) {
		getSharedState().throwExceptionIfImmutable("addChildAll");
		children.addAll(childList);
	}

	@Override
	public int getMinOccurs() {
		return getNodeDef().getMinOccurs();
	}

	@Override
	public int getMaxOccurs() {
		return getNodeDef().getMaxOccurs();
	}

	@Override
	public boolean isUnbounded() {
		return getNodeDef().isUnbounded();
	}

	public int getOccurNum() {
		return occurNum;
	}
	
	public void setOccurNum(int occurNum) {
		getSharedState().throwExceptionIfImmutable("setOccurNum");
		this.occurNum = occurNum;
	}

	public int getOccurCount() {
		return occurCount;
	}
	
	public void setOccurCount(int occurCount) {
		getSharedState().throwExceptionIfImmutable("setOccurCount");
		this.occurCount = occurCount;
	}
	
	@Override
	public boolean isChildOfChoice() {
		// since compositor 'inst' nodes are sometimes removed in the 'inst' model for visual clarity,
		// we need to check if the 'def' node is the child of a choice
		return getNodeDef().isChildOfChoice();
	}
	
	public boolean isFirstChildOfChoice() {
		return occurNum == 1 && isChildOfChoice();
	}

	@Override
	public NonAttribNodeDef getNodeDef() {
		return (NonAttribNodeDef)super.getNodeDef();
	}

	@Override
	public String toString() {
		return super.toString() + ", occurNum: " + occurNum + ", occurCount: " + occurCount;		
	}
}
