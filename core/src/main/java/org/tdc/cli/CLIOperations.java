package org.tdc.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdc.config.book.BookConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.process.BookProcessor;
import org.tdc.process.BookProcessorImpl;
import org.tdc.process.ModelProcessor;
import org.tdc.process.ModelProcessorImpl;
import org.tdc.process.SystemInitializer;
import org.tdc.process.SystemInitializerImpl;
import org.tdc.server.TDCServer;
import org.tdc.util.Addr;
import org.tdc.util.Util;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.KeyValuePair;
import joptsimple.util.PathConverter;

/**
 * Handles processing of command-line operations. 
 */
public class CLIOperations {
	private static final String OP_H = "h";
	private static final String OP_HELP = "help";
	private static final String OP_QUESTION = "?";
	
	private static final String OP_S = "s";
	private static final String OP_SYSTEM_CONFIG_ROOT = "system-config-root";
	
	private static final String OP_L = "l";
	private static final String OP_LIST = "list";
	
	private static final String OP_Z = "z";
	private static final String OP_CREATE_CUSTOMIZER = "create-customizer";
	private static final String OP_BASED_ON_MODEL = "based-on-model";
	
	private static final String OP_C = "c";
	private static final String OP_CREATE_BOOK = "create-book";
	private static final String OP_BASED_ON_BOOK = "based-on-book";
	
	private static final String OP_P = "p";
	private static final String OP_PROCESS_BOOK = "process-book";
	private static final String OP_NO_LOG = "no-log";
	private static final String OP_SCHEMA_VALIDATE = "schema-validate";
	private static final String OP_PROCESS_TASKS = "process-tasks";
	private static final String OP_TASK_PARAM = "task-param";
	
	private static final String OP_V = "v";
	private static final String OP_SERVER = "server";
	
	private static final String OP_TARGET = "target"; // applies to "z", "c", and "p"

	private static final int INDENT_SPACES = 3;
	private static final int MAX_BACKUPS = 3;
	
	private final OptionParser parser = new OptionParser();
	private final boolean admin;

	private OptionSpec<?> opHelp;
	private OptionSpec<Path> opSystemConfigRoot;
	private OptionSpec<CLIArgsConfigType> opList;
	private OptionSpec<Addr> opCreateCustomizer;
	private OptionSpec<Addr> opBasedOnModel;
	private OptionSpec<Addr> opCreateBook;
	private OptionSpec<Path> opBasedOnBook;
	private OptionSpec<Path> opProcessBook;
	private OptionSpec<?> opNoLog;
	private OptionSpec<?> opSchemaValidate;
	private OptionSpec<String> opProcessTasks;
	private OptionSpec<KeyValuePair> opTaskParam;
	private OptionSpec<Path> opTarget;
	private OptionSpec<?> opServer;

	private Path systemConfigRoot;
	private SystemInitializer init;
	private ModelProcessor modelProcessor;
	private BookProcessor bookProcessor;

	public CLIOperations(boolean admin) {
		this.admin = admin;
		initParser();
	}
	
	private void initParser() {
		initParserHelp();
		initParserSystemConfigRoot();
		initParserList();
		initParserCreateCustomizer();
		initParserCreateBook();
		initParserProcessBook();
		initParserProcessTarget();
		initParserServer();
	}
	
	private void initParserHelp() {
		opHelp = parser.acceptsAll(Arrays.asList(
				OP_H, OP_HELP, OP_QUESTION), "help")
				.forHelp();
	}
	
	private void initParserSystemConfigRoot() {
		opSystemConfigRoot = parser.acceptsAll(Arrays.asList(
				OP_S, OP_SYSTEM_CONFIG_ROOT), "system config root dir")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.required();
	}
	
	private void initParserList() {
		opList = parser.acceptsAll(Arrays.asList(
				OP_L, OP_LIST), "list config schema|model|book")
				.withRequiredArg()
				.ofType(CLIArgsConfigType.class);
	}
	
	private void initParserCreateCustomizer() {
		opCreateCustomizer = parser.acceptsAll(Arrays.asList(
				OP_Z, OP_CREATE_CUSTOMIZER), "create customizer")
				.withRequiredArg()
				.ofType(Addr.class)
				.describedAs("model address");
		opBasedOnModel = parser.accepts(
				OP_BASED_ON_MODEL, "model address for model on which to based new customizer")
				.availableIf(opCreateCustomizer)
				.withRequiredArg()
				.ofType(Addr.class)
				.describedAs("based-on model address");
	}
	
	private void initParserCreateBook() {
		opCreateBook = parser.acceptsAll(Arrays.asList(
				OP_C, OP_CREATE_BOOK), "create book")
				.withRequiredArg()
				.ofType(Addr.class)
				.describedAs("book address");
		opBasedOnBook = parser.accepts(
				OP_BASED_ON_BOOK, "book file with test data for book creation")
				.availableIf(opCreateBook)
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.describedAs("based-on book file");
	}
	
	private void initParserProcessBook() {
		opProcessBook = parser.acceptsAll(Arrays.asList(
				OP_P, OP_PROCESS_BOOK), "process book")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.describedAs("book file to process");
		opNoLog = parser.accepts(
				OP_NO_LOG, "do not write out log info")
				.availableIf(opProcessBook);
		opSchemaValidate = parser.accepts(
				OP_SCHEMA_VALIDATE, "schema validate")
				.availableIf(opProcessBook);
		opProcessTasks = parser.accepts(
				OP_PROCESS_TASKS, "process tasks")
				.availableIf(opProcessBook)
				.withOptionalArg()
				.ofType(String.class);
		opTaskParam = parser.accepts(
				OP_TASK_PARAM, "task parameter")
				.availableIf(opProcessTasks)
				.withRequiredArg()
				.ofType(KeyValuePair.class);
	}
	
	private void initParserProcessTarget() {
		// used by customizer creation, book creation, and book processing)
		opTarget = parser.accepts(
				OP_TARGET, "target file for book/customizer creation")
				.availableIf(opCreateCustomizer, opCreateBook, opProcessBook)
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.describedAs("target file name");
	}

	private void initParserServer() {
		opServer = parser.acceptsAll(Arrays.asList(
				OP_V, OP_SERVER), "run as server");
	}

	public void execute(String[] args) {
		try {
			OptionSet options = parser.parse(args);
			executeOptions(options);
		}
		catch (OptionException e) {
			outputAndEnd(e.getMessage());
		}
	}
	
	private void executeOptions(OptionSet options) {
		if (!options.hasOptions() || options.has(opHelp)) {
			executeHelp();
			System.exit(0);
		}

		initConfigDir(options);
		initProcessors();

		if (options.has(opList)) {
			CLIArgsConfigType configType = (CLIArgsConfigType)options.valueOf(OP_LIST);
			executeListConfig(configType);
		}
		else if (options.has(opCreateCustomizer)) {
			Addr modelAddr = options.valueOf(opCreateCustomizer);
			Path targetPath = options.valueOf(opTarget);
			Addr basedOnModelAddr = options.valueOf(opBasedOnModel);
			executeCreateCustomizer(modelAddr, targetPath, basedOnModelAddr);
		}
		else if (options.has(opCreateBook)) {
			Addr bookAddr = options.valueOf(opCreateBook);
			Path targetPath = options.valueOf(opTarget);
			Path basedOnBookPath = options.valueOf(opBasedOnBook);
			executeCreateBook(bookAddr, targetPath, basedOnBookPath);
		}
		else if (options.has(opProcessBook)) {
			Path bookPath = options.valueOf(opProcessBook);
			Path targetPath = options.valueOf(opTarget);
			boolean noLog = options.has(opNoLog);
			boolean schemaValidate = options.has(opSchemaValidate);
			boolean processTasks = options.has(opProcessTasks);
			List<String> taskIDsToProcess = options.hasArgument(opProcessTasks) ? 
					options.valuesOf(opProcessTasks) : null;
			List<KeyValuePair> keyValues = options.hasArgument(opTaskParam) ? 
					options.valuesOf(opTaskParam) : null;
			Map<String, String> taskParams = null;
			if (keyValues != null) {
				taskParams = new HashMap<>();
				for (KeyValuePair keyValue : keyValues) {
					taskParams.put(keyValue.key, keyValue.value);
				}
			}
			executeProcessBook(bookPath, schemaValidate, processTasks, 
					taskIDsToProcess, taskParams, targetPath, noLog);
		}
		else if (options.has(opServer)) {
			executeServer();
		}
		else {
			outputAndEnd("No commands specified");
		}
	}

	private void initConfigDir(OptionSet options) {
		systemConfigRoot = options.valueOf(opSystemConfigRoot);
		if (systemConfigRoot == null || !Files.isDirectory(systemConfigRoot)) {
			outputAndEnd("A valid System Config Root dir must be specified with option -" + 
					OP_S + " or --" + OP_SYSTEM_CONFIG_ROOT);
		}
	}

	private void initProcessors() {
		init = new SystemInitializerImpl
				.Builder()
				.defaultFactories(systemConfigRoot)
				.build();
		modelProcessor = 
				new ModelProcessorImpl(
						init.getModelConfigFactory(), 
						init.getModelDefFactory(), 
						init.getSpreadsheetFileFactory());
		bookProcessor = 
				new BookProcessorImpl(
						init.getBookConfigFactory(),
						init.getModelInstFactory(),
						init.getBookFactory(),
						init.getSpreadsheetFileFactory(),
						init.getFilterFactory(),
						init.getTaskFactory(),
						init.getSchemaValidatorFactory());
	}

	private void executeHelp() {
		try {
			// TODO improve output format; include header info
			parser.printHelpOn(System.out);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void executeListConfig(CLIArgsConfigType configType) {
		if (configType == CLIArgsConfigType.schema) {
			outputSchemaConfigsList();
		}
		else if (configType == CLIArgsConfigType.model) {
			outputModelConfigsList();
		}
		else if (configType == CLIArgsConfigType.book) {
			outputBookConfigsList();
		}
	}

	private void outputSchemaConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<SchemaConfig> schemaConfigs = 
				init.getSchemaConfigFactory().getAllSchemaConfigs(errors);
		output("Schema Config addresses:");
		for (SchemaConfig schemaConfig : schemaConfigs) {
			String addr = schemaConfig.getAddr().toString();
			output(1, addr);
		}
		outputErrors(errors);
	}

	private void outputModelConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<ModelConfig> modelConfigs = 
				init.getModelConfigFactory().getAllModelConfigs(errors);
		output("Model Config addresses:");
		for (ModelConfig modelConfig : modelConfigs) {
			String addr = modelConfig.getAddr().toString();
			String name = modelConfig.getModelName();
			String desc = modelConfig.getModelDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}

	private void outputBookConfigsList() {
		Map<Addr, Exception> errors = new HashMap<>();
		List<BookConfig> bookConfigs = 
				init.getBookConfigFactory().getAllBookConfigs(errors);
		output("Book Config addresses:");
		for (BookConfig bookConfig : bookConfigs) {
			String addr = bookConfig.getAddr().toString();
			String name = bookConfig.getBookName();
			String desc = bookConfig.getBookDescription();
			String nameDesc = name + (desc.length() == 0 ? "" : ": " + desc);
			output(1, addr + " (" + nameDesc + ")");
		}
		outputErrors(errors);
	}

	private void executeCreateCustomizer(Addr modelAddr, Path targetPath, Addr basedOnModelAddr) {
		verifyModelAddr(modelAddr);
		ModelConfig config = 
				init.getModelConfigFactory().getModelConfig(modelAddr); 
		verifyModelCustomizerConfigured(config);
		String customizerName = getCustomizerName(config);
		targetPath = verifyTargetOrProvideDefault(targetPath, customizerName, "xlsx");
		verifyBasedOnModelIfExists(basedOnModelAddr);
		modelProcessor.createCustomizer(modelAddr, targetPath, basedOnModelAddr, false);
	}

	private String getCustomizerName(ModelConfig config) {
		Path fullPath = config.getModelCustomizerConfig().getFilePath();
		String fileName = fullPath.getName(fullPath.getNameCount() - 1).toString();
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	private void verifyModelCustomizerConfigured(ModelConfig config) {
		ModelCustomizerConfig modelCustomizerConfig = config.getModelCustomizerConfig();
		if (modelCustomizerConfig == null) {
			outputAndEnd(config.getAddr() + " is not configured for Customizer creation");
		}
	}

	private void verifyModelAddr(Addr modelAddr) {
		if (!init.getModelConfigFactory().isModelConfig(modelAddr)) {
			outputAndEnd(modelAddr + " does not represent a Model Config address");
		}
	}

	private void verifyBasedOnModelIfExists(Addr basedOnModelAddr) {
		if (basedOnModelAddr != null && 
				!init.getModelConfigFactory().isModelConfig(basedOnModelAddr)) {
			outputAndEnd(basedOnModelAddr + " does not represent a Model Config address");
		}
	}
	
	private void executeCreateBook(Addr bookAddr, Path targetPath, Path basedOnBookPath) {
		verifyBookAddr(bookAddr);
		String bookName = init.getBookConfigFactory().getBookConfig(bookAddr).getBookName();
		String targetExtension = bookProcessor.getTargetBookFileExtension(bookAddr);
		targetPath = verifyTargetOrProvideDefault(targetPath, bookName, targetExtension);
		verifyBasedOnBookIfExists(basedOnBookPath);
		bookProcessor.createBook(bookAddr, targetPath, basedOnBookPath, false);
	}

	private void verifyBookAddr(Addr bookAddr) {
		if (!init.getBookConfigFactory().isBookConfig(bookAddr)) {
			outputAndEnd(bookAddr + " does not represent a Book Config address");
		}
	}

	private void verifyBasedOnBookIfExists(Path basedOnBookPath) {
		if (basedOnBookPath != null && Files.notExists(basedOnBookPath)) {
			outputAndEnd("Based-on book " + basedOnBookPath + " does not exists");
		}
	}
	
	private Path verifyTargetOrProvideDefault(Path targetPath, String name, String targetExtension) {
		if (targetPath == null) {
			String defaultName = Util.legalizeName(name) + "." + targetExtension;
			targetPath = Paths.get(defaultName);
		}
		if (!targetPath.toString().endsWith("." + targetExtension)) {
			outputAndEnd("Target file " + targetPath + " must end with ." + targetExtension + " extension");
		}
		if (Files.exists(targetPath)) {
			outputAndEnd("Target file " + targetPath + 
					" already exists; delete first or specify an alternative file name with --target option");
		}
		targetPath = targetPath.toAbsolutePath();
		if (!Files.isDirectory(targetPath.getParent())) {
			outputAndEnd("Target directory " + targetPath.getParent() + " does not exist");
		}
		return targetPath;
	}
	
	private void executeProcessBook(
			Path bookPath, boolean schemaValidate, boolean processTasks, 
			List<String> taskIDsToProcess, Map<String, String> taskParams, 
			Path targetPath, boolean noLog) {
		
		if (noLog) {
			bookProcessor.loadAndProcessBook(
					bookPath, schemaValidate, processTasks, 
					taskIDsToProcess, taskParams, null, false);
		}
		else {
			if (targetPath != null) {
				String bookExtension = Util.getExtension(bookPath);
				verifyTargetOrProvideDefault(targetPath, null, bookExtension);
				bookProcessor.loadAndProcessBook(
						bookPath, schemaValidate, processTasks, 
						taskIDsToProcess, taskParams, targetPath, false);
			}
			else {
				ensureFileNotCurrentlyInUse(bookPath);
				Path tempPath = createTempCopy(bookPath);
				bookProcessor.loadAndProcessBook(
						tempPath, schemaValidate, processTasks, 
						taskIDsToProcess, taskParams, tempPath, true);
				backupAndRenameFiles(bookPath, tempPath);
			}
		}
	}

	private void ensureFileNotCurrentlyInUse(Path bookPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		Path excelBackup = parentPath.resolve("~$" + fileName);
		Path libreOfficeBackup = parentPath.resolve(".~lock." + fileName + "#");
		// do initial test for existence of Excel files; 
		// if backup file can be deleted, that means it is not locked, 
		//		and the backup file is likely an orphaned file;
		// if backup file cannot be deleted (i.e. exception), that means
		// 		it is locked, and we definitely want to output an error;
		// after the delete attempt, we'll test for existence again;
		// note: don't attempt to delete Libre Office backup files, as these
		// will more than likely be on a Linux file system, so there will be
		// no locking, and the delete will almost always succeed;
		// testing for 'in use' files is quite a tricky task; 
		// this is not ideal, but it's better than nothing
		try {
			Files.deleteIfExists(excelBackup);
		}
		catch (Exception e) {
			// intentionally do nothing
		}
		boolean found = Files.exists(excelBackup) || Files.exists(libreOfficeBackup);
		if (found) {
			outputAndEnd("It appears this Book file is in use. Please close then try again.");
		}
	}

	private Path createTempCopy(Path bookPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		Path tempPath = parentPath.resolve(fileName + ".tmp");
		try {
			Files.copy(bookPath, tempPath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to create temporary file: " + tempPath, e);
		}
		return tempPath;
	}

	private void backupAndRenameFiles(Path bookPath, Path tempPath) {
		Path parentPath = bookPath.toAbsolutePath().getParent();
		String fileName = bookPath.getFileName().toString();
		String timestamp = LocalDateTime.now().format(Util.DATE_TIME_FORMATTER);
		Path backupPath = parentPath.resolve(fileName + "." + timestamp + ".backup");
		try {
			Files.move(bookPath, backupPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to rename original file to backup: " + backupPath);
		}
		try {
			Files.move(tempPath, bookPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to rename temp file to original : " + bookPath);
		}
		deleteOldBackups(parentPath, fileName + ".", ".backup");
	}

	private void deleteOldBackups(Path parentPath, String matchPrefix, String matchSuffix) {
		try {
			List<Path> backupFiles = new ArrayList<>();
			Files.find(parentPath, 1, 
					(path, attrib) -> 
							path.getFileName().toString().startsWith(matchPrefix) &&
							path.getFileName().toString().endsWith(matchSuffix))
					.sorted()
					.forEach(path -> backupFiles.add(path));
			for (int i = 0; i < backupFiles.size() - MAX_BACKUPS; i++) {
				Files.delete(backupFiles.get(i));
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to delete old backup files", e);
		}
	}

	private void executeServer() {
		TDCServer server = new TDCServer(
				init.getSystemConfig().getServerConfig(), 
				init.getSchemaConfigFactory(),
				init.getModelConfigFactory(),
				init.getBookConfigFactory(),
				modelProcessor, 
				bookProcessor);
		server.startAndWait();
	}

	private void outputErrors(Map<Addr, Exception> errors) {
		if (errors.size() > 0) {
			output("Errors:");
			for (Entry<Addr, Exception> e : errors.entrySet()) {
				output(1, e.getKey() + " (" + e.getValue().getMessage() + ")");
			}
		}
	}

	private void output(String message) {
		output(0, message);
	}

	private void output(int indent, String message) {
		System.out.println(Util.spaces(indent * INDENT_SPACES) + message);
	}

	private void outputAndEnd(String message) {
		output(message);
		output("");
		executeHelp();
		System.exit(1);
	}
}
