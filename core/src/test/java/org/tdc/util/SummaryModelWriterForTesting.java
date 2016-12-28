package org.tdc.util;

import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.TDCNode;
import org.tdc.util.Util;

/**
 * A {@link AbstractModelWriterForTesting} implementation for creating 
 * node representations to use for general summary purposes.
 * 
 * <p>The output produced by this class is useful for getting an 
 * overall understanding of a Model tree structure.
 */
public class SummaryModelWriterForTesting extends AbstractModelWriterForTesting {
	
	private final StringBuilder sb = new StringBuilder();

	public SummaryModelWriterForTesting(TDCNode rootNode, int indentSize) {
		super(rootNode, indentSize);
	}

	@Override
	protected String tempBuildNodeString(TDCNode node, int level) {
		String summary = getTestSummaryString(node);
		return Util.spaces(level * getIndentSize()) + summary;
	}
	
	private String getTestSummaryString(TDCNode node) {
		sb.setLength(0);
		sb.append(node.getDispName());
		if (node instanceof AttribNode) {
			AttribNode attribNode = (AttribNode)node;
			sb.append(", req:");
			sb.append(attribNode.isRequired());
		}
		else if (node instanceof CompositorNode) {
			CompositorNode compNode = (CompositorNode)node;
			sb.append(", minOccurs:");
			sb.append(compNode.getMinOccurs());
			sb.append(", maxOccurs:");
			sb.append(compNode.getMaxOccurs());
			
		}
		else if (node instanceof ElementNode) {
			ElementNode elementNode = (ElementNode)node;
			sb.append(", minOccurs:");
			sb.append(elementNode.getMinOccurs());
			sb.append(", maxOccurs:");
			sb.append(elementNode.getMaxOccurs());
		}
		else {
			throw new IllegalStateException("Unknown node type: " + node.getClass().getName());
		}
		return sb.toString();
	}
}
