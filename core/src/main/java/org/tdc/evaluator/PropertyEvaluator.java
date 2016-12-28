package org.tdc.evaluator;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;
import org.tdc.util.Util;

/**
 * An {@link Evaluator} implementation for property values.
 * 
 * <p>Supports providing a property value as an Evaluator result.
 * 
 * <p>Properties, essentially, correspond to methods on a node.
 * The full method name can be specified, or just the property name itself
 * (if the corresponding method begins with "get"). Either is acceptable.
 * 
 * <p>The method/property being called must not expect any parameters/arguments and 
 * must return a result (i.e. not void).
 * 
 * <pre>
 * {@code
 * <Evaluator type="property" source="current">MyProperty</Evaluator>
 * 
 * <Evaluator type="property" source="current">getMyProperty</Evaluator>
 * 
 * <Evaluator type="property" source="current">myProperty</Evaluator>
 * 
 * <Evaluator type="property" source="previous">MyProperty</Evaluator>
 * }
 * </pre>
 */
public class PropertyEvaluator implements Evaluator {
	private final Source source;
	private final String propertyName;
	
	public PropertyEvaluator(Source source, String propertyName) {
		this.source = source;
		this.propertyName = propertyName;
	}
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		TDCNode node = source == Source.CURRENT ? currentNode : prevNode;
		String value = node == null ? "" : Util.getStringValueFromProperty(node, propertyName, "");
		return new ValueResult(value);
	}
}
