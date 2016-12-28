package org.tdc.config.book;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.util.Util;

/**
 * A {@link NodeDetailColumnConfigImpl} implementation.
 */
public class NodeDetailColumnConfigImpl implements NodeDetailColumnConfig {
	private final String[] headerLabels;
	private final int width;
	private final CellStyle style;
	private final String readFromVariable;
	private final String readFromProperty;
	private final int index;
	private final int colNum;
	
	private NodeDetailColumnConfigImpl(Builder builder) {
		this.headerLabels = builder.headerLabels;
		this.width = builder.width;
		this.style = builder.style;
		this.readFromVariable = builder.readFromVariable;
		this.readFromProperty = builder.readFromProperty;
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
	public String getReadFromVariable() {
		return readFromVariable;
	}

	@Override
	public String getReadFromProperty() {
		return readFromProperty;
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
		private static final String CONFIG_PREFIX = ".NodeDetailColumns.Column";

		private final Config config;
		private final String pageStructKey;
		private final int headerRowCount;
		private final CellStyle defaultNodeDetailColumnStyle;
		private final int nodeDetailColStart;
		
		private String[] headerLabels;
		private int width;
		private CellStyle style;
		private String readFromVariable;
		private String readFromProperty;
		private int index;
		
		public Builder(
				Config config, String pageStructKey, int headerRowCount, 
				CellStyle defaultNodeDetailColumnStyle, int nodeDetailColStart) {
			
			this.config = config;
			this.pageStructKey = pageStructKey;
			this.headerRowCount = headerRowCount;
			this.defaultNodeDetailColumnStyle = defaultNodeDetailColumnStyle;
			this.nodeDetailColStart = nodeDetailColStart;
		}
		
		public List<NodeDetailColumnConfig> buildAll() {
			List<NodeDetailColumnConfig> columns = new ArrayList<>();
			int count = config.getCount(pageStructKey + CONFIG_PREFIX);
			for (index = 0; index < count; index++) {
				NodeDetailColumnConfig column = build();
				columns.add(column);
			}
			return columns;
		}
	
		private NodeDetailColumnConfig build() {
			String indexPrefix = pageStructKey + CONFIG_PREFIX + "(" + index + ").";
			headerLabels = Util.getHeaderLabels(
					config, indexPrefix + "HeaderLabels", headerRowCount);
			width = config.getInt(indexPrefix + "Width", 0, true);
			style = new CellStyleImpl.Builder().setFromConfig(
					config, indexPrefix + "Style", defaultNodeDetailColumnStyle, false).build();
			readFromVariable = config.getString(
					indexPrefix + "ReadFromVariable", null, false);
			readFromProperty = config.getString(
					indexPrefix + "ReadFromProperty", null, false);
			if ((readFromVariable == null && readFromProperty == null) ||
				(readFromVariable != null && readFromProperty != null)	) {
				throw new IllegalStateException(
						"One and only one of ReadFromVariable or ReadFromProperty must be specified for: " + 
						indexPrefix);
			}
			return new NodeDetailColumnConfigImpl(this);
		}
	}
}
