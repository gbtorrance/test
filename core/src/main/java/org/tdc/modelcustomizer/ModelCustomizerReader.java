package org.tdc.modelcustomizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerColumnConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.util.Util;

/**
 * Implementation of class with functionality for reading an existing customizer spreadsheet
 * and updating a ModelDef with customizations extracted from the spreadsheet.
 */
public class ModelCustomizerReader extends AbstractModelCustomizer {

	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerReader.class);

	public ModelCustomizerReader(ElementNodeDef rootElement, ModelCustomizerConfig config, 
			SpreadsheetFile spreadsheetFile) {
		super(rootElement, config, spreadsheetFile);
	}
	
	public void readCustomizer() {
		if (getSpreadsheetFile().getSpreadsheet(CUSTOMIZER_SHEET_NAME) == null) {
			throw new IllegalStateException("Customizer is missing required '" + CUSTOMIZER_SHEET_NAME + "' worksheet");
		}
		processTree();
	}
	
	@Override
	protected void processAttribNode(AttribNode node) {
		validateNode(node);
		readCustomColumns(node);
		readOccursCountOverride(node, true);
	}

	@Override
	protected void processCompositorNode(CompositorNode node) {
		validateNode(node);
		readCustomColumns(node);
		readOccursCountOverride(node, false);
	}
	
	@Override
	protected void processElementNode(ElementNode node) {
		validateNode(node);
		readCustomColumns(node);
		readOccursCountOverride(node, false);
	}
	
	private void validateNode(TDCNode node) {
		validateNodeName(node);
	}
	
	private void validateNodeName(TDCNode node) {
		int row = getNodeRow(node);
		int col = getNodeCol(node);
		String expectedValue = node.getDispName();
		String actualValue = getCustomizerSheet().getCellValue(row, col);
		if (!actualValue.equals(expectedValue)) {
			exception(row, col, "Invalid node name", actualValue, "expected '" + expectedValue + "'");
		}
	}

	private void readCustomColumns(TDCNode node) {
		NodeDef nodeDef = (NodeDef)node; // cast so we can set variables;
		List<ModelCustomizerColumnConfig> columns = getConfig().getNodeDetailColumns(); 
		for (int i = 0; i < columns.size(); i++) {
			ModelCustomizerColumnConfig column = columns.get(i);
			String variable = column.getStoreValueWithVariableName();
			if (variable != null && !variable.equals("")) {
				String value = getCustomizerSheet().getCellValue(getNodeRow(node), column.getColNum()).trim();
				nodeDef.setVariable(variable, value);
			}
		}
	}
	
	private void readOccursCountOverride(TDCNode node, boolean isAttrib) {
		int row = getNodeRow(node);
		int parentOverride = node.getParent() == null ? 
				Util.UNDEFINED : getOccursOverride(node.getParent());
		int override = getOccursOverride(node);
		if (override != Util.UNDEFINED) {
			boolean allowInvalid = getConfig().getAllowMinMaxInvalidOccursCountOverride();
			AttribNode attrib = isAttrib ? (AttribNode)node : null;
			NonAttribNode nonAttrib = !isAttrib ? (NonAttribNode)node : null;
			if (isAttrib && override > 1) {
				exception(row, "Invalid Occurs Override value", ""+override, 
						"attribute can have at most one occurrence");
			}
			if (node == getRootElement() && override != 1) {
				exception(row, "Invalid Occurs Override value", ""+override, 
						"root element must have exactly one occurrence");
			}
			if (!allowInvalid && parentOverride != 0) {
				if (isAttrib) {
					if (attrib.isRequired() && override != 1) {
						exception(row, "Invalid Occurs Override value", ""+override, 
								"required attributes must have exactly one occurrence");
					}
				}
				else {
					if (!nonAttrib.isUnbounded() && override > nonAttrib.getMaxOccurs()) {
						exception(row, "Invalid Occurs Override value", ""+override, 
								"cannot be greater than max " + nonAttrib.getMaxOccurs());
					}
					if (override < nonAttrib.getMinOccurs()) {
						exception(row, "Invalid Occurs Override value", ""+override, 
								"cannot be less than min " + nonAttrib.getMinOccurs());
					}
				}
			}
		}
		NodeDef nodeDef = (NodeDef)node;
		nodeDef.setOccursCountOverride(override);
	}
	
	private int getOccursOverride(TDCNode node) {
		int row = getNodeRow(node);
		String overrideVariable = getConfig().getReadOccursCountOverrideFromVariable();
		String overrideStr = node.getVariable(overrideVariable).trim();
		int override = Util.UNDEFINED;
		try {
			if (overrideStr.length() > 0) {
				override = Integer.parseUnsignedInt(overrideStr);
			}
		}
		catch (NumberFormatException ex) {
			exception(row, "Invalid Occurs Override value", overrideStr, 
					"expected non-negative number");
		}
		return override;
	}
	
	private void exception(int row, String invalidMsg, String actualValue, String expectedMsg) {
		exception(row, Util.UNDEFINED, invalidMsg, actualValue, expectedMsg);
	}

	private void exception(int row, int col, String invalidMsg, String actualValue, String expectedMsg) {
		throw new RuntimeException(invalidMsg + " '" + actualValue + 
				"' at row " + row + (col == Util.UNDEFINED ? "" : " col " + col) + "; " + expectedMsg);
	}
}
