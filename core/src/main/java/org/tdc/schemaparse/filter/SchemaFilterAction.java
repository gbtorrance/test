package org.tdc.schemaparse.filter;

/**
 * Defines the type of action that will be associated with an Element
 * that is to be included or excluded during the parsing of a schema.
 */
public enum SchemaFilterAction {
	// include Element
	INCLUDE,
	// include Element as it is a parent of an include Element (i.e. it is on the "include path")
	INCLUDE_PATH, 
	// exclude Element
	EXCLUDE,
	// exclude children of Element, where possible, but this Element
	// cannot be excluded because it is on the "include path" and is must be
	// included to support the inclusion of a particular child Element
	EXCLUDE_ON_INCLUDE_PATH;
}
