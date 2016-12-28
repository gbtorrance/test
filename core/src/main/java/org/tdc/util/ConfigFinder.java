package org.tdc.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.schema.SchemaConfig;

/**
 * Helper class for returning a list of {@link Addr} objects corresponding
 * to configuration folders.
 * These configuration folders are identified by the presence of 
 * a particular named configuration file (which is provided as a parameter). 
 * 
 * <p>Useful for finding and creating all 
 * {@link SchemaConfig}, {@link ModelConfig}, and {@link BookConfig} objects.
 * 
 * <p>Ignores all folders containing a "TDCIgnore" file.
 */
public class ConfigFinder {
	private static final Logger log = LoggerFactory.getLogger(ConfigFinder.class);

	/**
	 * Returns a list of {@link Addr} objects for config folders under the 
	 * specified root Path containing the specified file name.
	 * 
	 * <p>Note: Any folder containing a "TDCIgnore" file will be ignored.
	 * 
	 * @param configRoot Root path.
	 * @param fileNameIdentifyingConfigFolder File name to use for identifying config folders to return in list
	 * @return List of {@link Addr} objects identifying the appropriate configurations.
	 */
	public static List<Addr> findAllConfigsContainingConfigFile(
			Path configRoot, String fileNameIdentifyingConfigFolder) {
		
		List<Addr> addrList = new ArrayList<>();
		ConfigFileVisitor visitor = 
				new ConfigFileVisitor(configRoot, fileNameIdentifyingConfigFolder, addrList);
		try {
			Files.walkFileTree(configRoot, visitor);
		}
		catch (IOException ex) {
			throw new RuntimeException("Exception encountered while traversing config tree", ex);
		}
		Collections.sort(addrList);
		return addrList;
	}
	
	private static class ConfigFileVisitor extends SimpleFileVisitor<Path> {
		
		private static final String IGNORE_FILE = "TDCIgnore";
		
		private final Path configRoot;
		private final String fileNameIdentifyingConfigFolder;
		private final List<Addr> list;
		private final Deque<FolderInfo> stack = new ArrayDeque<>();
		
		public ConfigFileVisitor(Path configRoot, String fileNameIdentifyingConfigFolder, List<Addr> addrList) {
			this.configRoot = configRoot;
			this.fileNameIdentifyingConfigFolder = fileNameIdentifyingConfigFolder;
			this.list = addrList;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			push();
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			if (file.endsWith(fileNameIdentifyingConfigFolder)) {
				peek().foundConfigFolder = true;
			}
			if (file.endsWith(IGNORE_FILE)) {
				peek().ignoreFolder = true;
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
			if (ex != null) {
				throw ex;
			}
			if (peek().foundConfigFolder && !peek().ignoreFolder) {
				Path addrPath = configRoot.relativize(dir);
				Addr addr = new Addr(addrPath);
				list.add(addr);
			}
			pop();
			return FileVisitResult.CONTINUE;
		}
		
		private FolderInfo push() {
			FolderInfo info = new FolderInfo();
			stack.push(info);
			return info;
		}
		
		private FolderInfo pop() {
			return stack.pop();
		}
		
		private FolderInfo peek() {
			return stack.peek();
		}
		
		private static class FolderInfo {
			private boolean foundConfigFolder;
			private boolean ignoreFolder;
		}
	}
}

