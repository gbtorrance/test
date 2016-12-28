package org.tdc.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.tdc.config.util.Config;

/**
 * Static helper members and methods.
 */
public class Util {
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("yyMMdd_kkmmss_SSS");
	
	public static final int UNDEFINED = -1;
	public static final int NO_LIMIT = -1;
	
	/**
	 * Returns a certain number of spaces.
	 * 
	 * @param count Number of spaces to return.
	 * @return String containing spaces.
	 */
	public static String spaces(int count) {
		return new String(new char[count]).replace('\0', ' ');
	}
	
	/**
	 * Replaces any character in a string that is not a 
	 * letter, number, or underscore with an underscore.
	 * 
	 * <p>This doesn't guaranteed that any resulting folder or file name will be legal;
	 * the various file system rules about legal names are extremely complex, but it's a start.
	 * 
	 * @param name A name that might contain illegal characters.
	 * @return The name with potentially illegal characters stripped out.
	 */
	public static String legalizeName(String name) {
		return name.replaceAll("\\W+", "_");
	}

	/**
	 * Extracts a "property" value from an object using Reflection. 
	 * The property name prefixed with "get" indicates the name of the method to call.
	 * 
	 * <p>If the property does not exist for the given object, the default value will be returned.
	 * If the property does exist, but there is an error when calling the corresponding method,
	 * an exception will be thrown.
	 * 
	 * <p>To allow for extracting properties that have corresponding methods that do not begin
	 * with "get", the full method name can be specified as the property name (e.g. "hasChild").
	 * 
	 * @param object Object from which the property will be extracted. 
	 * @param propertyName Property name.
	 * @param defaultValue Default value (if the object does not have the given property).
	 * @return Property value (or default value, if the property does not exist).
	 */
	public static String getStringValueFromProperty(Object object, String propertyName, String defaultValue) {
		String value = defaultValue;

		Method method = null;
		try {
			// check if full method name was provided 
			method = object.getClass().getMethod(propertyName);
		}
		catch (NoSuchMethodException | SecurityException e) {
			// no such method found; no problem
		}
		try {
			// check if property name (without "get" prefix) was provided 
			String methodName = 
					"get" + 
					propertyName.substring(0, 1).toUpperCase() + 
					(propertyName.length() > 1 ? propertyName.substring(1, propertyName.length()) : "");
			method = object.getClass().getMethod(methodName);
		}
		catch (NoSuchMethodException | SecurityException e) {
			// no such method found; no problem
		}
		try {
			if (method != null) {
				Object result = method.invoke(object);
				value = result.toString();
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Unable to call property '" + propertyName + "'", e);
		}
		return value;
	}
	
	/**
	 * Purges the content of a directory of any files or sub-directories.
	 * 
	 * @param dir Path of directory to purge.
	 */
	public static void purgeDirectory(Path dirToPurge) {
		if (Files.isDirectory(dirToPurge)) {
			try {
				Files.walkFileTree(dirToPurge, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						if (!Files.isSameFile(dirToPurge, dir)) {
							Files.delete(dir);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} 
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns the extension of the provided file path.
	 * 
	 * @param path Path to a file (for which extension will be provided). 
	 * @return File extension string.
	 */
	public static String getExtension(Path path) {
		String pathStr = path.toString();
		int index = pathStr.lastIndexOf(".");
		return index == -1 ? "" : pathStr.substring(index + 1);
	}
	
	/**
	 * Create a directory; throw RuntimeException in case of error.
	 * 
	 * @param dirPath Directory to create.
	 */
	public static void createDirectory(Path dirPath) {
		try {
			Files.createDirectory(dirPath);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Create a directory with a particular prefix within a root directory;
	 * the directory will be suffixed with a date/time stamp.
	 * 
	 * @param root Root directory in which batch directory will be created.
	 * @param prefix Prefix for new batch directory
	 * @return The path of the new directory that was created.
	 */
	public static Path createBatchDir(Path root, String prefix) {
		String batchDirName = 
				Util.legalizeName(prefix) + "_" + 
				LocalDateTime.now().format(Util.DATE_TIME_FORMATTER);
		Path batchDir = root.resolve(batchDirName);
		Util.createDirectory(batchDir);
		return batchDir;
	}
	
	/**
	 * Get an array of label strings from a configuration key.
	 * Also ensures that the maximum number of labels is not exceeded.
	 * 
	 * @param config Config from which to read the label strings.
	 * @param key Key pointing to the location of the label strings.
	 * @param rowCount Maximum number of header rows.
	 * @return Array of rowCount label strings, with blank strings for padding.
	 */
	public static String[] getHeaderLabels(Config config, String key, int rowCount) {
		// the following statement is necessary to make sure that, even if there are 
		// no child elements, that the parent key will be marked as having been
		// visited/recognized by the system
		config.hasNode(key);
		final String labelKey = key + ".Label";
		String[] labels = new String[rowCount];
		int labelCount = config.getCount(labelKey);
		if (labelCount > rowCount) {
			throw new IllegalStateException("Number of " + labelKey + " entries (" + labelCount + 
					") exceeds maximum of (" + rowCount + ")");
		}
		for (int i = 0; i < rowCount; i++) {
			labels[i] = rowCount - labelCount > i ? 
					"" :  
					config.getString(labelKey + "(" + (i - rowCount + labelCount) + ")", null, true);
		}
		return labels;
	}
}
