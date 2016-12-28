package org.tdc.util;

import java.util.ArrayList;
import java.util.List;

import org.tdc.model.AttribNode;
import org.tdc.model.CanHaveAttributes;
import org.tdc.model.CanHaveChildren;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;

/**
 * Abstract class that supports writing a structured representation of a {@link TDCNode} tree
 * for testing or debugging purposes.
 * 
 * <p>Using this approach, Models can be more easily compared by simply doing a string comparison of the
 * expected and actual Model representations. 
 * 
 * <p>Uses the template method pattern to have subclasses provided specific string content for each node of the tree. 
 */
public abstract class AbstractModelWriterForTesting {
	
	private final TDCNode rootNode;
	private final int indentSize;
	
	public AbstractModelWriterForTesting(TDCNode rootNode, int indentSize) {
		this.rootNode = rootNode;
		this.indentSize = indentSize;
	}
	
	public String writeToString() {
		List<String> lines = writeToStringList();
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	public List<String> writeToStringList() {
		List<String> lines = new ArrayList<>();
		processNode(lines, rootNode, 0);
		return lines;
	}
	
	protected int getIndentSize() {
		return indentSize;
	}
	
	private void processNode(List<String> lines, TDCNode node, int level) {
		String line = tempBuildNodeString(node, level); 
		lines.add(line);
		if (node instanceof CanHaveAttributes) {
			CanHaveAttributes canHaveAttributes = (CanHaveAttributes)node;
			List<? extends AttribNode> attribs = canHaveAttributes.getAttributes();
			for (AttribNode attrib : attribs) {
				processNode(lines, attrib, level+1);
			}
		}
		if (node instanceof CanHaveChildren) {
			CanHaveChildren canHaveChildren = (CanHaveChildren)node;
			List<? extends NonAttribNode> nonAttribs = canHaveChildren.getChildren();
			for (NonAttribNode nonAttrib : nonAttribs) {
				processNode(lines, nonAttrib, level+1);
			}
		}
	}
	
	protected abstract String tempBuildNodeString(TDCNode node, int level); 
}
