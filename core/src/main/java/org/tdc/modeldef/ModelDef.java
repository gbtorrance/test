package org.tdc.modeldef;

import org.tdc.config.model.ModelConfig;
import org.tdc.model.MPathIndex;
import org.tdc.schema.Schema;

/**
 * Defines a Model "definition" (ModelDef) -- which may become the foundation for one or more  
 * Model "instances" ({@link org.tdc.modelinst.ModelInst ModelInst}).
 * 
 * <p>A ModelDef is an in-memory representation of a Schema (or certain parts of a Schema) built as a hierarchy of node objects.
 * 
 * <p>Unlike a traditional DOM tree, which only contains nodes that correspond to XML elements or attributes,
 * both types of Models (ModelDef and ModelInst) also contain various structural "compositor" nodes that help define how
 * Models should be represented visually to a user. These include 
 * "sequence" (defining an ordered sequence of nodes), 
 * "choice" (defining a list of possible nodes from which at most one can be provided), and
 * "any" (defining an unordered list of nodes; not currently supported).
 * 
 * <p>The primary difference between a ModelDef and a ModelInst is that
 * a ModelDef contains only one of any node that may possibly repeat, whereas a
 * ModelInst contains as many instance of a repeating node as are allowed by the ModelInst configuration.
 * 
 * <p>A ModelDef is a purely-internal representation, whereas a ModelInst is a representation
 * that can ultimately be made visible to a user in Excel.
 * 
 * <p>Every node in a ModelInst contain a reference to its "defining" node in a ModelDef. 
 * For example, if an element node can be repeated up to 5 times in a ModelInst (per configuration), there will be 5 such
 * nodes in the ModelInst; but each of them will refer to the same "defining" node in the ModelDef.
 */
public interface ModelDef {
	ModelConfig getModelConfig();
	Schema getSchema();
	ElementNodeDef getRootElement();
	MPathIndex<NodeDef> getMPathIndex();
}
