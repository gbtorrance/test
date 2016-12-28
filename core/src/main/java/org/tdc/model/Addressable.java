package org.tdc.model;

/**
 * Defines functionality for nodes that can have an MPath address and a specific location/position in a model tree. 
 */
public interface Addressable {
	String getMPath();
	int getColOffset();
	int getRowOffset();
}
