package org.tdc.export;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.TaskConfig;
import org.tdc.config.util.Config;
import org.tdc.util.Addr;

/**
 * Configuration for {@link DefaultExportTask}.
 */
public class DefaultExportTaskConfig implements TaskConfig {
	private static final Logger log = LoggerFactory.getLogger(DefaultExportTaskConfig.class);
	
	private final String taskID;
	private final Path exportRoot;

	public DefaultExportTaskConfig(String taskID, Path exportRoot) {
		this.taskID = taskID;
		this.exportRoot = exportRoot;
	}

	@Override
	public String getTaskID() {
		return taskID;
	}

	@Override
	public String getTaskClassName() {
		return DefaultExportTask.class.getName();
	}
	
	public Path getExportRoot() {
		return exportRoot;
	}
	
	public static TaskConfig build(
			Config config, String key, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
	
		String taskID = config.getString(key + "[@id]", "export", false);
		String exportRootStr = config.getString(key + ".ExportRoot", null, true);
		Path exportRoot = getExportRootPath(bookConfigRoot, exportRootStr);
		return new DefaultExportTaskConfig(taskID, exportRoot);
	}
	
	private static Path getExportRootPath(Path bookConfigRoot, String exportRootStr) {
		Path exportRoot = bookConfigRoot.resolve(exportRootStr).normalize();
		if (!Files.exists(exportRoot)) {
			throw new IllegalStateException(
					"Exporter root dir does not exist: " + exportRoot);
		}
		return exportRoot;
	}
}
