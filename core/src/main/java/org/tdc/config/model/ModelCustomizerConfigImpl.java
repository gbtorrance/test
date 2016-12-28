package org.tdc.config.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.tdc.config.util.Config;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.util.Util;

/**
 * A {@link ModelCustomizerConfig} implementation.
 */
public class ModelCustomizerConfigImpl implements ModelCustomizerConfig {
	
	private final Path filePath;
	private final CellStyle defaultStyle;
	private final CellStyle nodeHeaderStyle; 		// based on defaultStyle
	private final CellStyle defaultNodeStyle; 		// based on defaultStyle
	private final CellStyle parentNodeStyle;		// based on defaultNodeStyle
	private final CellStyle attribNodeStyle;		// based on defaultNodeStyle
	private final CellStyle compositorNodeStyle;	// based on defaultNodeStyle
	private final CellStyle choiceMarkerNodeStyle;	// based on defaultNodeStyle
	private final CellStyle nodeDetailHeaderStyle;	// based on defaultStyle
	private final CellStyle defaultNodeDetailStyle;	// based on defaultStyle (parent to detail column styles)
	private final int nodeColumnCount;
	private final int nodeColumnWidth;
	private final int headerRowCount;
	private final String[] nodeHeaderLabels;
	private final String readOccursCountOverrideFromVariable;
	private final boolean allowMinMaxInvalidOccursCountOverride;
	private final int defaultOccursCount;
	private final List<ModelCustomizerColumnConfig> nodeDetailcolumns;
	private final int headerRowStart;
	private final int nodeRowStart;
	private final int nodeColStart;
	private final int nodeDetailColStart;
	
	private ModelCustomizerConfigImpl(Builder builder) {
		this.filePath = builder.filePath;
		this.defaultStyle = builder.defaultStyle;
		this.nodeHeaderStyle = builder.nodeHeaderStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerNodeStyle = builder.choiceMarkerNodeStyle;
		this.nodeDetailHeaderStyle = builder.nodeDetailHeaderStyle;
		this.defaultNodeDetailStyle = builder.defaultNodeDetailStyle;
		this.nodeColumnCount = builder.nodeColumnCount;
		this.nodeColumnWidth = builder.nodeColumnWidth;
		this.headerRowCount = builder.headerRowCount;
		this.nodeHeaderLabels = builder.nodeHeaderLabels;
		this.readOccursCountOverrideFromVariable = builder.readOccursCountOverrideFromVariable;
		this.allowMinMaxInvalidOccursCountOverride = builder.allowMinMaxInvalidOccursCountOverride;
		this.defaultOccursCount = builder.defaultOccursCount;
		this.nodeDetailcolumns = Collections.unmodifiableList(builder.nodeDetailColumns); // unmodifiable
		this.headerRowStart = builder.headerRowStart;
		this.nodeRowStart = builder.nodeRowStart;
		this.nodeColStart = builder.nodeColStart;
		this.nodeDetailColStart = builder.nodeDetailColStart;
	}
	
	@Override
	public Path getFilePath() {
		return filePath;
	}
	
	@Override
	public CellStyle getDefaultStyle() {
		return defaultStyle;
	}
	
	@Override
	public CellStyle getNodeHeaderStyle() {
		return nodeHeaderStyle;
	}

	@Override
	public CellStyle getDefaultNodeStyle() {
		return defaultNodeStyle;
	}
	
	@Override
	public CellStyle getParentNodeStyle() {
		return parentNodeStyle;
	}
	
	@Override
	public CellStyle getAttribNodeStyle() {
		return attribNodeStyle;
	}
	
	@Override
	public CellStyle getCompositorNodeStyle() {
		return compositorNodeStyle;
	}
	
	@Override
	public CellStyle getChoiceMarkerNodeStyle() {
		return choiceMarkerNodeStyle;
	}

	@Override
	public CellStyle getNodeDetailHeaderStyle() {
		return nodeDetailHeaderStyle;
	}

	@Override
	public CellStyle getDefaultNodeDetailStyle() {
		return defaultNodeDetailStyle;
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
	public String getReadOccursCountOverrideFromVariable() {
		return readOccursCountOverrideFromVariable;
	}

	@Override
	public boolean getAllowMinMaxInvalidOccursCountOverride() {
		return allowMinMaxInvalidOccursCountOverride;
	}
	
	@Override
	public int getDefaultOccursCount() {
		return defaultOccursCount;
	}

	@Override
	public List<ModelCustomizerColumnConfig> getNodeDetailColumns() {
		return nodeDetailcolumns;
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
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "Customizer";

		private final Config config;
		private final Path modelConfigRoot;
		private final int defaultOccursCount;
		private final String modelName;
		private final GeneralEvaluatorFactory evaluatorFactory;
		private final int headerRowStart;
		private final int nodeColStart;

		private Path filePath;
		private CellStyle defaultStyle;
		private CellStyle nodeHeaderStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerNodeStyle;
		private CellStyle nodeDetailHeaderStyle;
		private CellStyle defaultNodeDetailStyle;
		private int nodeColumnCount;
		private int nodeColumnWidth;
		private int headerRowCount;
		private String[] nodeHeaderLabels;
		private String readOccursCountOverrideFromVariable;
		private boolean allowMinMaxInvalidOccursCountOverride;
		private int nodeDetailColStart;
		private List<ModelCustomizerColumnConfig> nodeDetailColumns; 
		private int nodeRowStart;
		
		public Builder(
				Config config, Path modelConfigRoot, int defaultOccursCount,
				String modelName, GeneralEvaluatorFactory evaluatorFactory) {
			this.config = config;
			this.modelConfigRoot = modelConfigRoot;
			this.defaultOccursCount = defaultOccursCount;
			this.modelName = modelName;
			this.evaluatorFactory = evaluatorFactory;
			this.headerRowStart = 1;
			this.nodeColStart = 1;
		}
	
		public ModelCustomizerConfig build() {
			ModelCustomizerConfig custConfig = null;
			if (config.hasNode(CONFIG_PREFIX)) {
				String fileNamePrefix = config.getString(
						CONFIG_PREFIX + ".FileNamePrefix", "Customizer_", false);
				String fileName = Util.legalizeName(fileNamePrefix + modelName) + ".xlsx";
				filePath = modelConfigRoot.resolve(fileName);
				defaultStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".DefaultStyle", null, true).build();
				nodeHeaderStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".NodeHeaderStyle", defaultStyle, false).build();
				defaultNodeStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".DefaultNodeStyle", defaultStyle, false).build();
				parentNodeStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".ParentNodeStyle", defaultNodeStyle, false).build();
				attribNodeStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".AttribNodeStyle", defaultNodeStyle, false).build();
				compositorNodeStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".CompositorNodeStyle", defaultNodeStyle, false).build();
				choiceMarkerNodeStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".ChoiceMarkerNodeStyle", defaultNodeStyle, false).build();
				nodeDetailHeaderStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".NodeDetailHeaderStyle", defaultStyle, false).build();
				defaultNodeDetailStyle = new CellStyleImpl.Builder().setFromConfig(
						config, CONFIG_PREFIX + ".DefaultNodeDetailStyle", defaultStyle, false).build();
				nodeColumnCount = config.getInt(
						CONFIG_PREFIX + ".NodeColumnCount", 0, true);
				nodeColumnWidth = config.getInt(
						CONFIG_PREFIX + ".NodeColumnWidth", 0, true);
				headerRowCount = config.getInt(
						CONFIG_PREFIX + ".HeaderRowCount", 1, false);
				nodeHeaderLabels = Util.getHeaderLabels(
						config, CONFIG_PREFIX + ".NodeHeaderLabels", headerRowCount);
				readOccursCountOverrideFromVariable = config.getString(
						CONFIG_PREFIX + ".ReadOccursCountOverrideFromVariable", null, true);
				allowMinMaxInvalidOccursCountOverride = config.getBoolean(
						CONFIG_PREFIX + ".AllowMinMaxInvalidOccursCountOverride", false, false);
				nodeDetailColStart = nodeColStart + nodeColumnCount;
				nodeDetailColumns = new ModelCustomizerColumnConfigImpl.Builder(
						config, evaluatorFactory, headerRowCount, 
						defaultNodeDetailStyle, nodeDetailColStart).buildAll();
				nodeRowStart = headerRowStart + headerRowCount;
				custConfig = new ModelCustomizerConfigImpl(this);
			}
			return custConfig;
		}
	}
}
