package org.tdc.modelinst;

import org.tdc.model.AbstractTDCNode;
import org.tdc.modeldef.NodeDef;

/**
 * {@link AbstractTDCNode} implementation specific to 'instance' nodes.
 *  
 * <p>Adds the ability to associate a "definition" node ({@link NodeDef}) to an "instance" node ({@link NodeInst}).
 * 
 * @see AttribNodeInst
 * @see NonAttribNodeInst
 */
public abstract class NodeInst extends AbstractTDCNode {
	
	private final NodeDef nodeDef;
	
	public NodeInst(NonAttribNodeInst parent, ModelInstSharedState sharedState) {
		this(parent, sharedState, null);
	}

	public NodeInst(NonAttribNodeInst parent, ModelInstSharedState sharedState, NodeDef nodeDef) {
		super(parent, sharedState);
		this.nodeDef = nodeDef;
	}
	
	public NodeDef getNodeDef() {
		return nodeDef;
	}

	@Override
	public NonAttribNodeInst getParent() {
		return (NonAttribNodeInst)super.getParent();
	}
	
	@Override
	public ModelInstSharedState getSharedState() {
		return (ModelInstSharedState)super.getSharedState();
	}
	
	@Override
	public String getDispName() {
		return nodeDef.getDispName();
	}

	@Override
	public String getDispOccurs() {
		return nodeDef.getDispOccurs();
	}
	
	@Override
	public String getVariable(String name) {
		// instance models don't have their own variables; return from def model
		return nodeDef.getSharedState().getVariable(name, nodeDef);
	}
}

