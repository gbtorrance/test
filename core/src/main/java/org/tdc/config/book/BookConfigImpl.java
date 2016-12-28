package org.tdc.config.book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.util.Config;
import org.tdc.config.util.ConfigImpl;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.util.Addr;

/**
 * A {@link BookConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class BookConfigImpl implements BookConfig {
	
	public static final String CONFIG_FILE = "TDCBookConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(BookConfigImpl.class);
	private static final String BOOKS_CONFIG_ROOT_PROP_KEY = "booksConfigRoot";
	private static final String BOOK_CONFIG_ROOT_PROP_KEY = "bookConfigRoot";

	private final Path booksConfigRoot;
	private final Addr addr;
	private final Path bookConfigRoot;
	private final String bookName;
	private final String bookDescription;
	private final Path bookTemplateFile;
	private final Map<String, DocTypeConfig> docTypeConfigs;
	private final Map<String, PageStructConfig> pageStructConfigs;
	private final Map<String, PageConfig> pageConfigs;
	private final List<FilterConfig> filterConfigs;
	private final List<TaskConfig> taskConfigs;
	private final CellStyle defaultStyle;
	private final CellStyle nodeHeaderStyle; 			// based on defaultStyle
	private final CellStyle defaultNodeStyle; 			// based on defaultStyle
	private final CellStyle parentNodeStyle;			// based on defaultNodeStyle
	private final CellStyle attribNodeStyle;			// based on defaultNodeStyle
	private final CellStyle compositorNodeStyle;		// based on defaultNodeStyle
	private final CellStyle choiceMarkerNodeStyle;		// based on defaultNodeStyle
	private final CellStyle occurMarkerNodeStyle;		// based on defaultNodeStyle
	private final CellStyle nodeDetailHeaderStyle;		// based on defaultStyle
	private final CellStyle defaultNodeDetailStyle;		// based on defaultStyle (parent to detail column styles)
	private final CellStyle docIDRowLabelStyle;			// based on defaultStyle
	private final CellStyle conversionNewRowStyle;		// based on defaultStyle
	private final CellStyle conversionPrevNewRowStyle;	// based on defaultStyle
	private final CellStyle defaultLogStyle;			// based on defaultStyle
	private final CellStyle headerLogStyle;				// based on defaultLogStyle
	private final CellStyle errorLogStyle;				// based on defaultLogStyle

	private BookConfigImpl(Builder builder) {
		this.booksConfigRoot = builder.booksConfigRoot;
		this.addr = builder.addr;
		this.bookConfigRoot = builder.bookConfigRoot;
		this.bookName = builder.bookName;
		this.bookDescription = builder.bookDescription;
		this.bookTemplateFile = builder.bookTemplateFile;
		this.docTypeConfigs = Collections.unmodifiableMap(builder.docTypeConfigs); // unmodifiable
		this.pageStructConfigs = Collections.unmodifiableMap(builder.pageStructConfigs); // unmodifiable
		this.pageConfigs = Collections.unmodifiableMap(builder.pageConfigs); // unmodifiable
		this.filterConfigs = Collections.unmodifiableList(builder.filterConfigs); // unmodifiable
		this.taskConfigs = Collections.unmodifiableList(builder.taskConfigs); // unmodifiable
		this.defaultStyle = builder.defaultStyle;
		this.nodeHeaderStyle = builder.nodeHeaderStyle;
		this.defaultNodeStyle = builder.defaultNodeStyle;
		this.parentNodeStyle = builder.parentNodeStyle;
		this.attribNodeStyle = builder.attribNodeStyle;
		this.compositorNodeStyle = builder.compositorNodeStyle;
		this.choiceMarkerNodeStyle = builder.choiceMarkerNodeStyle;
		this.occurMarkerNodeStyle = builder.occurMarkerNodeStyle;
		this.nodeDetailHeaderStyle = builder.nodeDetailHeaderStyle;
		this.defaultNodeDetailStyle = builder.defaultNodeDetailStyle;
		this.docIDRowLabelStyle = builder.docIDRowLabelStyle;
		this.conversionNewRowStyle = builder.conversionNewRowStyle;
		this.conversionPrevNewRowStyle = builder.conversionPrevNewRowStyle;
		this.defaultLogStyle = builder.defaultLogStyle;
		this.headerLogStyle = builder.headerLogStyle;
		this.errorLogStyle = builder.errorLogStyle;
	}
	
	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public String getBooksConfigRootPropKey() {
		return BOOKS_CONFIG_ROOT_PROP_KEY;
	}

	@Override
	public Addr getAddr() {
		return addr;
	}

	@Override
	public Path getBookConfigRoot() {
		return bookConfigRoot;
	}
	
	@Override
	public String getBookConfigRootPropKey() {
		return BOOK_CONFIG_ROOT_PROP_KEY;
	}

	@Override
	public String getBookName() {
		return bookName;
	}

	@Override
	public String getBookDescription() {
		return bookDescription;
	}

	@Override
	public Path getBookTemplateFile() {
		return bookTemplateFile;
	}

	@Override
	public String getBookTemplateFileExtension() {
		if (bookTemplateFile == null) {
			return null;
		}
		String filename = bookTemplateFile.toString();
		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	@Override
	public Map<String, DocTypeConfig> getDocTypeConfigs() {
		return docTypeConfigs;
	}

	@Override
	public Map<String, PageStructConfig> getPageStructConfigs() {
		return pageStructConfigs;
	}

	@Override
	public Map<String, PageConfig> getPageConfigs() {
		return pageConfigs;
	}
	
	@Override
	public List<FilterConfig> getFilterConfigs() {
		return filterConfigs;
	}

	@Override
	public List<TaskConfig> getTaskConfigs() {
		return taskConfigs;
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
	public CellStyle getOccurMarkerNodeStyle() {
		return occurMarkerNodeStyle;
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
	public CellStyle getDocIDRowLabelStyle() {
		return docIDRowLabelStyle;
	}

	@Override
	public CellStyle getConversionNewRowStyle() {
		return conversionNewRowStyle;
	}

	@Override
	public CellStyle getConversionPrevNewRowStyle() {
		return conversionPrevNewRowStyle;
	}

	@Override
	public CellStyle getDefaultLogStyle() {
		return defaultLogStyle;
	}

	@Override
	public CellStyle getHeaderLogStyle() {
		return headerLogStyle;
	}

	@Override
	public CellStyle getErrorLogStyle() {
		return errorLogStyle;
	}

	public static class Builder {
		private final Path booksConfigRoot;
		private final Addr addr;
		private final Path bookConfigRoot;
		private final Config config;
		private final ModelConfigFactory modelConfigFactory;
		private final FilterConfigFactory filterConfigFactory;
		private final TaskConfigFactory taskConfigFactory;
		
		private String bookName;
		private String bookDescription;
		private Path bookTemplateFile;
		private Map<String, DocTypeConfig> docTypeConfigs;
		private Map<String, PageStructConfig> pageStructConfigs;
		private Map<String, PageConfig> pageConfigs;
		private List<FilterConfig> filterConfigs;
		private List<TaskConfig> taskConfigs;
		private CellStyle defaultStyle;
		private CellStyle nodeHeaderStyle;
		private CellStyle defaultNodeStyle;
		private CellStyle parentNodeStyle;
		private CellStyle attribNodeStyle;
		private CellStyle compositorNodeStyle;
		private CellStyle choiceMarkerNodeStyle;
		private CellStyle occurMarkerNodeStyle;
		private CellStyle nodeDetailHeaderStyle;
		private CellStyle defaultNodeDetailStyle;
		private CellStyle docIDRowLabelStyle;
		private CellStyle conversionNewRowStyle;
		private CellStyle conversionPrevNewRowStyle;
		private CellStyle defaultLogStyle;
		private CellStyle headerLogStyle;
		private CellStyle errorLogStyle;
		
		public Builder(Path booksConfigRoot, Addr addr, 
				ModelConfigFactory modelConfigFactory, 
				FilterConfigFactory filterConfigFactory,
				TaskConfigFactory taskConfigFactory) {
			
			log.info("Creating BookConfig: {}", addr);
			this.booksConfigRoot = booksConfigRoot;
			this.addr = addr;
			this.bookConfigRoot = booksConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(bookConfigRoot)) {
				throw new IllegalStateException("BookConfig root dir does not exist: " + bookConfigRoot.toString());
			}
			Path bookConfigFile = bookConfigRoot.resolve(CONFIG_FILE);
			this.modelConfigFactory = modelConfigFactory;
			this.filterConfigFactory = filterConfigFactory;
			this.taskConfigFactory = taskConfigFactory;
			this.config = new ConfigImpl
					.Builder(bookConfigFile)
					.addLookup(BOOKS_CONFIG_ROOT_PROP_KEY, booksConfigRoot.toString())
					.addLookup(BOOK_CONFIG_ROOT_PROP_KEY, bookConfigRoot.toString())
					.build();
		}

		public BookConfig build() {
			bookName = config.getString("BookName", null, true);
			bookDescription = config.getString("BookDescription", "", false);
			String templateFileStr = config.getString("BookTemplateFile", null, false);
			bookTemplateFile = templateFileStr == null ? null : bookConfigRoot.resolve(templateFileStr);
			if (bookTemplateFile != null && Files.notExists(bookTemplateFile)) {
				throw new IllegalStateException("BookTemplateFile does not exist: " + bookTemplateFile.toString());
			}
			defaultStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "DefaultStyle", null, true).build();
			nodeHeaderStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "NodeHeaderStyle", defaultStyle, false).build();
			defaultNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "DefaultNodeStyle", defaultStyle, false).build();
			parentNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "ParentNodeStyle", defaultNodeStyle, false).build();
			attribNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "AttribNodeStyle", defaultNodeStyle, false).build();
			compositorNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "CompositorNodeStyle", defaultNodeStyle, false).build();
			choiceMarkerNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "ChoiceMarkerNodeStyle", defaultNodeStyle, false).build();
			occurMarkerNodeStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "OccurMarkerNodeStyle", defaultNodeStyle, false).build();
			nodeDetailHeaderStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "NodeDetailHeaderStyle", defaultStyle, false).build();
			defaultNodeDetailStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "DefaultNodeDetailStyle", defaultStyle, false).build();
			docIDRowLabelStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "DocIDRowLabelStyle", defaultStyle, false).build();
			conversionNewRowStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "ConversionNewRowStyle", defaultStyle, false).build();
			conversionPrevNewRowStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "ConversionPrevNewRowStyle", defaultStyle, false).build();
			defaultLogStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "DefaultLogStyle", defaultStyle, false).build();
			headerLogStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "HeaderLogStyle", defaultLogStyle, false).build();
			errorLogStyle = new CellStyleImpl.Builder().setFromConfig(
					config, "ErrorLogStyle", defaultLogStyle, false).build();
			docTypeConfigs = new DocTypeConfigImpl.Builder(config).buildAll();
			pageStructConfigs = new PageStructConfigImpl.Builder(
					config, defaultNodeDetailStyle).buildAll();
			pageConfigs = new PageConfigImpl.Builder(
					config, docTypeConfigs, pageStructConfigs, modelConfigFactory).buildAll();
			filterConfigs = filterConfigFactory.createFilterConfigs(
					config, "Filters", bookConfigRoot, addr, bookName);
			taskConfigs = taskConfigFactory.createTaskConfigs(
					config, "Tasks", bookConfigRoot, addr, bookName);
			config.ensureNoUnprocessedKeys();
			return new BookConfigImpl(this);
		}
	}
}
