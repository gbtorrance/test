package org.tdc.modeldef;

/**
 * Builder responsible for doing the complex work of constructing and assembling a {@link ModelDef} instance.
 */
public interface ModelDefBuilder {
	ModelDef build();
	ModelDef buildSkipCustomization();
}
