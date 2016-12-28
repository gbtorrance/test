package org.tdc.util;

import org.tdc.model.TDCNode;
import org.tdc.util.Util;

/**
 * A {@link AbstractModelWriterForTesting} implementation for creating MPath node representations.
 */
public class MPathModelWriterForTesting extends AbstractModelWriterForTesting {

	public MPathModelWriterForTesting(TDCNode rootNode, int indentSize) {
		super(rootNode, indentSize);
	}

	@Override
	protected String tempBuildNodeString(TDCNode node, int level) {
		String mpath = node.getMPath();
		return Util.spaces(level * getIndentSize()) + mpath;
	}
}
