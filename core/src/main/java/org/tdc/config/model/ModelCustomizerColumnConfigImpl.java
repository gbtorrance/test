package org.tdc.config.model;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.util.Util;

/**
 * A {@link ModelCustomizerColumnConfig} implementation.
 */
public class ModelCustomizerColumnConfigImpl implements ModelCustomizerColumnConfig {
	
	private final String[] headerLabels;
	private final int width;
	private final CellStyle style;
	private final Evaluator initAsNewEvaluator;
	private final Evaluator initFromPrevEvaluator;
	private final String storeValueWithVariableName;
	private final int index;
	private final int colNum;
	
	private ModelCustomizerColumnConfigImpl(Builder builder) {
		this.headerLabels = builder.headerLabels;
		this.width = builder.width;
		this.style = builder.style;
		this.initAsNewEvaluator = builder.initAsNewEvaluator;
		this.initFromPrevEvaluator = builder.initFromPrevEvaluator;
		this.storeValueWithVariableName = builder.storeValueWithVariableName;
		this.index = builder.index;
		this.colNum = builder.nodeDetailColStart + index;
	}

	@Override
	public String getHeaderLabel(int headerRowNum) {
		return headerLabels[headerRowNum-1];
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public CellStyle getStyle() {
		return style;
	}

	@Override
	public Evaluator getInitAsNewEvaluator() {
		return initAsNewEvaluator;
	}

	@Override
	public Evaluator getInitFromPrevEvaluator() {
		return initFromPrevEvaluator;
	}

	@Override
	public String getStoreValueWithVariableName() {
		return storeValueWithVariableName;
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public int getColNum() {
		return colNum;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "Customizer.NodeDetailColumns.Column";

		private final Config config;
		private final GeneralEvaluatorFactory evaluatorFactory;
		private final int headerRowCount;
		private final CellStyle defaultNodeDetailStyle;
		private final int nodeDetailColStart;
		
		private String[] headerLabels;
		private int width;
		private CellStyle style;
		private Evaluator initAsNewEvaluator;
		private Evaluator initFromPrevEvaluator;
		private String storeValueWithVariableName;
		private int index;
		
		public Builder(Config config, 
				GeneralEvaluatorFactory evaluatorFactory, int headerRowCount, 
				CellStyle defaultNodeDetailStyle, int nodeDetailColStart) {
			
			this.config = config;
			this.evaluatorFactory = evaluatorFactory;
			this.headerRowCount = headerRowCount;
			this.defaultNodeDetailStyle = defaultNodeDetailStyle;
			this.nodeDetailColStart = nodeDetailColStart;
		}
		
		public List<ModelCustomizerColumnConfig> buildAll() {
			List<ModelCustomizerColumnConfig> columns = new ArrayList<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (index = 0; index < count; index++) {
				ModelCustomizerColumnConfig column = build();
				columns.add(column);
			}
			return columns;
		}
	
		private ModelCustomizerColumnConfig build() {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ").";
			headerLabels = Util.getHeaderLabels(
					config, indexPrefix + "HeaderLabels", headerRowCount);
			width = config.getInt(indexPrefix + "Width", 0, true);
			style = new CellStyleImpl.Builder().setFromConfig(
					config, indexPrefix + "Style", defaultNodeDetailStyle, false).build();
			initAsNewEvaluator = evaluatorFactory.createEvaluator(
					config, indexPrefix + "InitializeAsNew.Evaluator", style);
			initFromPrevEvaluator = evaluatorFactory.createEvaluator(
					config, indexPrefix + "InitializeFromPrevious.Evaluator", style);
			storeValueWithVariableName = config.getString(
					indexPrefix + "Read.StoreValueWithVariableName", null, false);
			return new ModelCustomizerColumnConfigImpl(this);
		}
	}
}
