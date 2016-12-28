package org.tdc.config.book;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tdc.config.util.Config;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Util;

/**
 * A {@link PageStructConfig} implementation.
 */
public class PageStructConfigImpl implements PageStructConfig {
	
	private final String pageStructName;
	private final List<NodeDetailColumnConfig> nodeDetailColumnConfigs;
	private final List<DocIDRowConfig> docIDRowConfigs;
	private final DocIDRowConfig caseNumDocIDRowConfig;
	private final DocIDRowConfig setNameDocIDRowConfig;
	private final List<DocIDRowConfig> varDocIDRowConfigs;
	private final int nodeColumnCount;
	private final int nodeColumnWidth;
	private final int headerRowCount;
	private final String[] nodeHeaderLabels;
	private final int docIDRowStart;
	private final int docIDRowLabelCol;
	private final int headerRowStart;
	private final int nodeRowStart;
	private final int nodeColStart;
	private final int nodeDetailColStart;
	private final int testDocColStart;
	
	private PageStructConfigImpl(Builder builder) {
		this.pageStructName = builder.pageStructName;
		this.nodeDetailColumnConfigs = Collections.unmodifiableList(builder.nodeDetailColumnConfigs); // unmodifiable
		this.docIDRowConfigs = Collections.unmodifiableList(builder.docIDRowConfigs); // unmodifiable
		this.caseNumDocIDRowConfig = builder.caseNumDocIDRowConfig;
		this.setNameDocIDRowConfig = builder.setNameDocIDRowConfig;
		this.varDocIDRowConfigs = Collections.unmodifiableList(builder.varDocIDRowConfigs); // unmodifiable
		this.nodeColumnCount = builder.nodeColumnCount;
		this.nodeColumnWidth = builder.nodeColumnWidth;
		this.headerRowCount = builder.headerRowCount;
		this.nodeHeaderLabels = builder.nodeHeaderLabels;
		this.docIDRowStart = builder.docIDRowStart;
		this.docIDRowLabelCol = builder.docIDRowLabelCol;
		this.headerRowStart = builder.headerRowStart;
		this.nodeRowStart = builder.nodeRowStart;
		this.nodeColStart = builder.nodeColStart;
		this.nodeDetailColStart = builder.nodeDetailColStart;
		this.testDocColStart = builder.testDocColStart;
	}

	@Override
	public String getPageStructName() {
		return pageStructName;
	}

	@Override
	public List<NodeDetailColumnConfig> getNodeDetailColumnConfigs() {
		return nodeDetailColumnConfigs;
	}
	
	@Override
	public List<DocIDRowConfig> getDocIDRowConfigs() {
		return docIDRowConfigs;
	}
	
	@Override
	public DocIDRowConfig getCaseNumDocIDRowConfig() {
		return caseNumDocIDRowConfig; 
	}
	
	@Override
	public DocIDRowConfig getSetNameDocIDRowConfig() {
		return setNameDocIDRowConfig; 
	}
	
	@Override
	public List<DocIDRowConfig> getVarDocIDRowConfigs() {
		return varDocIDRowConfigs; 
	}
	
	@Override
	public int getNodeColumnCount() {
		return nodeColumnCount;
	}

	@Override
	public int getNodeColumnWidth() {
		return nodeColumnWidth;
	}

	@Override
	public int getHeaderRowCount() {
		return headerRowCount;
	}
	
	@Override
	public String getNodeHeaderLabel(int headerRowNum) {
		return nodeHeaderLabels[headerRowNum-1];
	}
	
	@Override
	public int getDocIDRowStart() {
		return docIDRowStart;
	}
	
	@Override
	public int getDocIDRowLabelCol() {
		return docIDRowLabelCol;
	}

	@Override
	public int getHeaderRowStart() {
		return headerRowStart;
	}

	@Override
	public int getNodeRowStart() {
		return nodeRowStart;
	}

	@Override
	public int getNodeColStart() {
		return nodeColStart;
	}

	@Override
	public int getNodeDetailColStart() {
		return nodeDetailColStart;
	}

	@Override
	public int getTestDocColStart() {
		return testDocColStart;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "PageStructs.PageStruct";

		private final Config config;
		private final CellStyle defaultNodeDetailColumnStyle;
		private final int docIDRowStart;
		private final int docIDRowLabelCol;
		private final int nodeColStart;

		private String pageStructName;
		private List<DocIDRowConfig> docIDRowConfigs;
		private List<NodeDetailColumnConfig> nodeDetailColumnConfigs;
		private DocIDRowConfig caseNumDocIDRowConfig;
		private DocIDRowConfig setNameDocIDRowConfig;
		private List<DocIDRowConfig> varDocIDRowConfigs;
		private int nodeColumnCount;
		private int nodeColumnWidth;
		private int headerRowCount;
		private String[] nodeHeaderLabels;
		private int nodeDetailColStart;
		private int headerRowStart;
		private int nodeRowStart;
		private int testDocColStart;
		
		public Builder(Config config,
				CellStyle defaultNodeDetailColumnStyle) {
			
			this.config = config;
			this.defaultNodeDetailColumnStyle = defaultNodeDetailColumnStyle;
			this.docIDRowStart = 1;
			this.docIDRowLabelCol = 1;
			this.nodeColStart = 1;
		}
		
		public Map<String, PageStructConfig> buildAll() {
			Map<String, PageStructConfig> map = new LinkedHashMap<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				PageStructConfig pageStructConfig = build(i);
				map.put(pageStructConfig.getPageStructName(), pageStructConfig);
			}
			return map;
		}

		private PageStructConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ")";
			pageStructName = config.getString(indexPrefix + ".PageStructName", null, true);
			
			nodeColumnCount = config.getInt(indexPrefix + ".NodeColumnCount", 0, true);
			nodeColumnWidth = config.getInt(indexPrefix + ".NodeColumnWidth", 0, true);
			headerRowCount = config.getInt(indexPrefix + ".HeaderRowCount", 1, false);
			nodeHeaderLabels = Util.getHeaderLabels(
					config, indexPrefix + ".NodeHeaderLabels", headerRowCount);
			nodeDetailColStart = nodeColStart + nodeColumnCount;
			
			nodeDetailColumnConfigs = new NodeDetailColumnConfigImpl.Builder(
					config, indexPrefix, headerRowCount, 
					defaultNodeDetailColumnStyle, nodeDetailColStart).buildAll();

			docIDRowConfigs = new DocIDRowConfigImpl.Builder(
					config, indexPrefix, docIDRowStart).buildAll();
			processDocIDRowConfigs();

			headerRowStart = docIDRowStart + docIDRowConfigs.size();
			nodeRowStart = headerRowStart + headerRowCount;
			testDocColStart = nodeDetailColStart + nodeDetailColumnConfigs.size();
			
			return new PageStructConfigImpl(this);
		}

		private void processDocIDRowConfigs() {
			caseNumDocIDRowConfig = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType() == DocIDType.CASE_NUM)
					.findFirst().get(); // guaranteed to have CASE_NUM row
			setNameDocIDRowConfig = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType() == DocIDType.SET_NAME)
					.findFirst().orElse(null); // SET_NAME may not exist
			varDocIDRowConfigs = docIDRowConfigs.stream()
					.filter(docIDRowConfig -> docIDRowConfig.getType().isVariableType())
					.collect(Collectors.toList()); // collect all variable rows
		}
	}
}
