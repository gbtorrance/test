package org.tdc.extension.mef.export;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.TaskConfig;
import org.tdc.config.util.Config;
import org.tdc.util.Addr;

/**
 * Configuration for {@link MeFExportTask}.
 */
public class MeFExportTaskConfig implements TaskConfig {
	private static final Logger log = LoggerFactory.getLogger(MeFExportTaskConfig.class);
	
	private final String taskID;
	private final Path exportRoot;
	private final String submissionIDVariable;
	private final String stateDocTypeName;
	private final String manifestDocTypeName;
	private final String federalDocTypeName;

	public MeFExportTaskConfig(
			String taskID, Path exportRoot, String submissionIDVariable, 
			String stateDocTypeName, String manifestDocTypeName, String federalDocTypeName) {
		
		this.taskID = taskID;
		this.exportRoot = exportRoot;
		this.submissionIDVariable = submissionIDVariable;
		this.stateDocTypeName = stateDocTypeName; 
		this.manifestDocTypeName = manifestDocTypeName;
		this.federalDocTypeName = federalDocTypeName;
	}

	@Override
	public String getTaskID() {
		return taskID;
	}

	@Override
	public String getTaskClassName() {
		return MeFExportTask.class.getName();
	}
	
	public Path getExportRoot() {
		return exportRoot;
	}
	
	public String getSubmissionIDVariable() {
		return submissionIDVariable;
	}
	
	public String getStateDocTypeName() {
		return stateDocTypeName;
	}
	
	public String getManifestDocTypeName() {
		return manifestDocTypeName;
	}
	
	public String getFederalDocTypeName() {
		return federalDocTypeName;
	}
	
	public static TaskConfig build(
			Config config, String key,
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		String taskID = config.getString(key + "[@id]", "export", false);
		String exportRootStr = config.getString(key + ".ExportRoot", null, true);
		Path exportRoot = getExportRootPath(bookConfigRoot, exportRootStr);
		String submissionIDVariable = config.getString(key + ".SubmissionIDVariable", "SUBMISSION_ID", false);
		String stateDocTypeName = config.getString(key + ".StateDocTypeName", null, true);
		String manifestDocTypeName = config.getString(key + ".ManifestDocTypeName", null, true);
		String federalDocTypeName = config.getString(key + ".FederalDocTypeName", null, true);
		return new MeFExportTaskConfig(
				taskID, exportRoot, submissionIDVariable, 
				stateDocTypeName, manifestDocTypeName, federalDocTypeName);
	}
	
	private static Path getExportRootPath(Path bookConfigRoot, String exportRootStr) {
		Path exportRoot = bookConfigRoot.resolve(exportRootStr).normalize();
		if (!Files.exists(exportRoot)) {
			throw new IllegalStateException(
					"Exporter root dir does not exist: " + exportRoot.toAbsolutePath());
		}
		return exportRoot;
	}
}
