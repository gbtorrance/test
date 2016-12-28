package org.tdc.modelcustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.ModelVisitor;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Abstract class containing common functionality for classes responsible for customizer reading and writing.  
 */
public abstract class AbstractModelCustomizer {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractModelCustomizer.class);
	
	protected static final String CUSTOMIZER_SHEET_NAME = "Customizer";
	
	private final ElementNodeDef rootElement;
	private final ModelCustomizerConfig config; 
	private final SpreadsheetFile spreadsheetFile;
	
	private Spreadsheet customizerSheet;
	
	/**
	 * Constructor. 
	 * 
	 * @param rootElement Root {@link ElementNodeDef} of the {@link ModelDef} to customize. 
	 * @param format{@link ModelCustomizerConfig} containing customizer config information (format, style, params).
	 * @param spreadsheetFile {@link SpreadsheetFile} to write to or read from.
	 */
	public AbstractModelCustomizer(ElementNodeDef rootElement, ModelCustomizerConfig config, 
			SpreadsheetFile spreadsheetFile) {
		this.rootElement = rootElement;
		this.config = config;
		this.spreadsheetFile = spreadsheetFile;
	}
	
	protected final ElementNodeDef getRootElement() {
		return rootElement;
	}
	
	protected final SpreadsheetFile getSpreadsheetFile() {
		return spreadsheetFile;
	}
	
	protected final Spreadsheet getCustomizerSheet() {
		if (customizerSheet == null) {
			customizerSheet = spreadsheetFile.getSpreadsheet(CUSTOMIZER_SHEET_NAME);
		}
		return customizerSheet;
	}
	
	protected final ModelCustomizerConfig getConfig() {
		return config;
	}
	
	protected final int getNodeRow(TDCNode node) {
		return config.getNodeRowStart() + node.getRowOffset();
	}
	
	protected final int getNodeCol(TDCNode node) {
		return config.getNodeColStart() + node.getColOffset();
	}
	
	protected void processTree() {
		rootElement.accept(new CustomizerVisitor());
	}
	
	protected abstract void processAttribNode(AttribNode attribNode);
	
	protected abstract void processCompositorNode(CompositorNode compositorNode);
	
	protected abstract void processElementNode(ElementNode elementNode);
	
	class CustomizerVisitor implements ModelVisitor {
		@Override
		public void visit(AttribNode attribNode) {
			processAttribNode(attribNode);
		}

		@Override
		public void visit(CompositorNode compositorNode) {
			processCompositorNode(compositorNode);
		}

		@Override
		public void visit(ElementNode elementNode) {
			processElementNode(elementNode);
		}
	}
}
