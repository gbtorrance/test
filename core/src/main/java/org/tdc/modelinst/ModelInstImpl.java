package org.tdc.modelinst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.ModelDef;

/**
 * A {@link ModelInst} implementation.
 * 
 * <p>This light-weight object's primary purpose is as a holder for the ModelInst's root {@link ElementNodeInst}.
 *
 */
public class ModelInstImpl implements ModelInst {

	private static final Logger log = LoggerFactory.getLogger(ModelInstImpl.class);

	private final ModelDef modelDef;
	private final ElementNodeInst rootElement;
	private final MPathIndex<NodeInst> mpathIndex;
	private final ModelInstSharedState sharedState;
	
	public ModelInstImpl(
			ModelDef modelDef, ElementNodeInst rootElement, 
			MPathIndex<NodeInst> mpathIndex, ModelInstSharedState sharedState) {
		
		log.info("Creating ModelInstImpl: {}", modelDef.getModelConfig().getAddr());
		this.modelDef = modelDef;
		this.rootElement = rootElement;
		this.mpathIndex = mpathIndex;
		this.sharedState = sharedState;
		sharedState.setImmutable();
	}
	
	@Override
	public ModelConfig getModelConfig() {
		return modelDef.getModelConfig();
	}
	
	@Override
	public ModelDef getModelDef() {
		return modelDef;
	}

	@Override
	public ElementNodeInst getRootElement() {
		return rootElement;
	}

	@Override
	public MPathIndex<NodeInst> getMPathIndex() {
		return mpathIndex;
	}
}
