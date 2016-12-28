package org.tdc.schemaparse.filter;

import org.apache.xerces.xs.XSElementDeclaration;
import org.tdc.modeldef.ModelDef;

/**
 * Interface definition for objects that can be queried during the 
 * course of schema parsing / {@link ModelDef} building. The associated method
 * will be called for every Element during a pre-order traversal of
 * the schema Element tree. 
 * 
 * <p>This functionality might not always be necessary, 
 * particular in the case of small, easy-to-manage schemas, 
 * as any elements can easily be excluded from the resulting {@link ModelDef} 
 * using a Customizer spreadsheet.
 * However, some schemas may be particularly  large, resulting in huge
 * Customizer spreadsheets, and a lot of unnecessary schema processing
 * if only a tiny subset of the schema is necessary in the resulting
 * {@link ModelDef}. This functionality allows for schemas to be "pre-filtered"
 * to quickly eliminate unnecessary sections. The resulting {@link ModelDef} and
 * Customizer spreadsheet (if used) will only contain the Elements that
 * passed through the filter, significantly simplifying management and
 * speeding up processing. 
 */
public interface SchemaFilter {
	/**
	 * Called once for every Element encountered while building a model
	 * from a schema. Should return {@code true} if the Element
	 * should be included in the model, and {@code false} if it should
	 * be excluded.
	 * 
	 * @param parentPath Parent path of Element to be tested.
	 * @param xsCurrentElement Element to be tested for inclusion/exclusion.
	 * @return {@code true} to include Element; {@code false} to exclude Element.
	 */
	boolean includeElementInModel(String parentPath, XSElementDeclaration xsCurrentElement);
}
