package org.tdc.modelinst;

import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.ModelDef;

/**
 * Defines a Model "instance" (ModelInst) -- which is based on a 
 * Model "definition" ({@link org.tdc.modeldef.ModelDef ModelDef}).
 * 
 * @see ModelDef
 */
public interface ModelInst {
	ModelConfig getModelConfig();
	ModelDef getModelDef();
	ElementNodeInst getRootElement();
	MPathIndex<NodeInst> getMPathIndex();
}
