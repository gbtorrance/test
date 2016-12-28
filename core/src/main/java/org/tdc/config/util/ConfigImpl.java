package org.tdc.config.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.configuration2.tree.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for Apache Commons Configuration library.
 * 
 * <p>Implements various helper getter methods, and ensures that, if a required
 * configuration item is not specified, an exception will be thrown.
 * 
 * <p>Supports "include" files through the use of the configuration property defined
 * with the INCLUDE_FILE_KEY constant. When a file is specified using this 
 * property name, it will automatically be merged with the current file. 
 * When inclusion/merging takes place, any values in included files will be 
 * "overridden" by any properties in the file containing the inclusion statement.
 * 
 * <p>Note: When overriding property values from included files, if those properties
 * are members of a list, matching will be done based on the values of matching 
 * attributes. So, for example, if a a configuration has 3 "ListItem" properties in an
 * included file, and those properties each have a "name" attribute, in order to override
 * the second "ListItem", which has the attribute name="Item2", it would be necessary to 
 * have the override property also include the attribute name="Item2". 
 * (This functionality takes advantage of the MergeCombiner in Apache Commons Configuration.)
 * 
 * <p>Supports "variable interpolation". Or, put another way, "variable expansion".
 * This is supported using a ${prefix:name} notation, where "prefix" is optional.
 * (This is very similar to what is supported by Ant and Maven.)
 * Most of the functionality is provided by Apache Commons Configuration, and is
 * simply used by this class. (Refer to Apache documentation for further details,
 * including how to use the "sys", "const", or "env" prefixes.)
 * If no prefix is specified, the "name" specified will refer to another property
 * in the configuration file (or included files). (See below for how this can 
 * be used with "user defined" properties.)
 * In addition to the above -- through the use of the {@link TDCLookup} class and
 * the {@link ConfigImpl.Builder.addLookup} method -- this class supports the ability to add other
 * prefixes, so that custom variables can be added. All of the main configuration
 * classes make use of this (with the "tdc" prefix) to provide application specific
 * variables (such as the location of various configuration directories).
 * 
 * <p>Supports configuration file validation. This is done by making a list of all
 * keys in a file (and included files). As values are queried from the Config object,
 * each key will be marked as visited. At the end, the ensureNoUnprocessedKeys()
 * method is called. If any keys have not been checked by the system 
 * (i.e. they are unknown to the system), then an exception will be thrown.
 * 
 * <p>Supports "user defined" properties. These can be added to a configuration
 * file as child properties of the property defined by the USER_DEFINED_KEY constant.
 * Any child property of this property will be ignored by the system during 
 * validation (see above). However, these user-defined properties can still be 
 * referenced within variables in other places throughout the configuration (see above).
 * (This is particularly useful when an include file contains a complex section of
 * configuration that needs to be customized by including configurations. This can
 * either by done via overriding properties, or by defining user-defined properties
 * that are then referenced using variables in the included configuration.)
 */
public class ConfigImpl implements Config {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigImpl.class);
	private static final String INCLUDE_FILE_KEY = "IncludeFile";
	private static final String USER_DEFINED_KEY = "UserProp";
	
	private final Path primaryConfigPath;
	private final CombinedConfiguration combinedConfig;
	private final Set<String> keySet;
	private final Map<ImmutableNode, String> nodeCache;
	
	private ConfigImpl(Builder builder) {
		this.primaryConfigPath = builder.primaryConfigPath;
		this.combinedConfig = builder.combinedConfig;
		this.keySet = builder.keySet;
		this.nodeCache = new HashMap<>();
	}
	
	@Override
	public String getString(String key, String defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		String result = combinedConfig.getString(key, defaultValue);
		logKeyValue(key, result, !found);
		return result;
	}

	@Override
	public int getInt(String key, int defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		int result = combinedConfig.getInt(key, defaultValue);
		logKeyValue(key, Integer.toString(result), !found);
		return result;
	}
	
	@Override
	public Integer getInt(String key, Integer defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		Integer result = combinedConfig.getInt(key, defaultValue);
		logKeyValue(key, "" + result, !found);
		return result;
	}
	
	@Override
	public double getDouble(String key, double defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		double result = combinedConfig.getDouble(key, defaultValue);
		logKeyValue(key, Double.toString(result), !found);
		return result;
	}

	@Override
	public Double getDouble(String key, Double defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		Double result = combinedConfig.getDouble(key, defaultValue);
		logKeyValue(key, "" + result, !found);
		return result;
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		boolean result = combinedConfig.getBoolean(key, defaultValue);
		logKeyValue(key, Boolean.toString(result), !found);
		return result;
	}
	
	@Override
	public Boolean getBoolean(String key, Boolean defaultValue, boolean required) {
		markKeyAsQueried(key);
		boolean found = validateRequired(key, required);
		Boolean result = combinedConfig.getBoolean(key, defaultValue);
		logKeyValue(key, "" + result, !found);
		return result;
	}
	
	@Override
	public boolean hasNode(String key) {
		// returns whether a node exists, regardless of whether there is a value associated with that node;
		// suitable for detecting whether a parent node exists
		markKeyAsQueried(key);
		return combinedConfig.configurationsAt(key).size() > 0;
	}
	
	@Override
	public int getMaxIndex(String key) {
		markKeyAsQueried(key);
		return combinedConfig.getMaxIndex(key);
	}
	
	@Override
	public int getCount(String key) {
		markKeyAsQueried(key);
		return combinedConfig.getMaxIndex(key) + 1;
	}
	
	@Override
	public String getEffectiveConfig() {
		XMLConfiguration xc = new ExtendedXMLConfiguration(combinedConfig);
		FileHandler fh = new FileHandler(xc);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fh.save(out);
			return new String(out.toByteArray(), Charset.defaultCharset());
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to output effective XML", e);
		}
	}
	
	@Override
	public Set<String> getUnprocessedKeys() {
		return Collections.unmodifiableSet(keySet);
	}

	@Override
	public void ensureNoUnprocessedKeys() {
		if (keySet.size() > 0) {
			for (String key : keySet) {
				log.debug("Unprocessed key: {}", key);
			}
			throw new IllegalStateException(
					"Unrecognized configuration key '" + keySet.iterator().next() + 
					"' in file (or included file): " + primaryConfigPath.toString());
		}
	}
	
	@Override
	public void throwConfigItemNotFoundException(String key) {
		throw new NoSuchElementException("Required config item '" + key + 
				"' not found in: " + primaryConfigPath.toString());
	}
	
	@Override
	public void logKeyValue(String key, String value, boolean isDefault) {
		log.info(">: {}, {} {}", key, value, isDefault ? " ** DEFAULT **" : "");	
	}
	
	private boolean validateRequired(String key, boolean required) {
		boolean found = combinedConfig.containsKey(key);
		if (required && !found) {
			throwConfigItemNotFoundException(key);
		}
		return found;
	}
	
	private void markKeyAsQueried(String key) {
		String ordinalKey = getOrdinalKey(key);
		if (ordinalKey != null) {
			String partialKey;
			int index = -1;
			boolean more = true;
			while (more) {
				index = ordinalKey.indexOf('.', index + 1);
				if (index != -1) {
					partialKey = ordinalKey.substring(0, index);
					keySet.remove(partialKey);
				}
				else {
					more = false;
				}
			}
			index = ordinalKey.indexOf('[');
			if (index != -1) {
				partialKey = ordinalKey.substring(0, index);
				keySet.remove(partialKey);
			}
			keySet.remove(ordinalKey);
		}
	}
	
	private String getOrdinalKey(String key) {
		String ordinalKey = null;
		List<QueryResult<ImmutableNode>> qResults = 
				combinedConfig.resolveKey(
						combinedConfig.getNodeModel().getRootNode(), 
						key, 
						combinedConfig.getNodeModel().getNodeHandler());
		if (qResults.size() > 0) {
			QueryResult<ImmutableNode> qResult = qResults.get(0);
			ImmutableNode node = qResult.getNode();
			ordinalKey = combinedConfig.nodeKey(
					node, 
					nodeCache, 
					combinedConfig.getNodeModel().getNodeHandler());
			ordinalKey = ordinalKey.substring(ordinalKey.indexOf('.') + 1);
			if (qResult.isAttributeResult()) {
				ordinalKey += "[@" + qResult.getAttributeName() + "]";
			}
		}
		return ordinalKey;
	}

	public static class Builder {
		private final Path primaryConfigPath;
		
		private CombinedConfiguration combinedConfig;
		private Set<String> keySet;
		private Map<String, String> lookupMap;
		private TDCLookup lookup;
		private List<ResolvablePath> childResolvables;
		
		public Builder(Path primaryConfigPath) {
			this.primaryConfigPath = primaryConfigPath.toAbsolutePath().normalize();
		}
		
		public Builder addLookup(String key, String value) {
			if (lookupMap == null) {
				lookupMap = new HashMap<>();
			}
			lookupMap.put(key, value);
			return this;
		}
		
		public ConfigImpl build() {
			lookup = new TDCLookup(lookupMap);
			buildCombinedConfig();
			extractKeySet();
			return new ConfigImpl(this);
		}

		private void buildCombinedConfig() {
			NodeCombiner combiner = new MergeCombiner();
			combinedConfig = new CombinedConfiguration(combiner);
			combinedConfig.getInterpolator().registerLookup("tdc", lookup);
			buildAllChildConfigs();
		}

		private void buildAllChildConfigs() {
			Path childConfigPath = null;
			XMLConfiguration childConfig = null;
			int pathPos = 0;
			childResolvables = new ArrayList<>();
			childResolvables.add(new ResolvablePath(primaryConfigPath));
			while (pathPos < childResolvables.size()) {
				childConfigPath = resolvePathToProcess(pathPos);
				log.debug("Processing config path at pos {}: {}", pathPos, childConfigPath);
				if (isAlreadyProcessed(childConfigPath, pathPos)) {
					log.debug("Config path already processed; skipping: {}", childConfigPath);
					break;
				}
				pathPos++;
				if (Files.notExists(childConfigPath)) {
					throw new IllegalStateException(
							"Unable to locate config file: " + childConfigPath);
				}
				try {
					Parameters params = new Parameters();
					childConfig = 
							new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
									.configure(params.xml().setFileName(childConfigPath.toString()))
									.getConfiguration();
				}
				catch (ConfigurationException e) {
					throw new IllegalStateException(
							"Unable to load config file: " + childConfigPath, e);
				}
				extractIncludePaths(childConfig, childConfigPath, pathPos);
				combinedConfig.addConfiguration(childConfig);
			}
		}

		private Path resolvePathToProcess(int pathPos) {
			ResolvablePath r = childResolvables.get(pathPos);
			if (r.resolvedPath == null) {
				r.other = (String)combinedConfig.getInterpolator().interpolate(r.other);
				r.resolvedPath =  r.parentPath.resolve(r.other).normalize();
			}
			return r.resolvedPath;
		}

		private boolean isAlreadyProcessed(Path childConfigPath, int pathPos) {
			for (int i = 0; i < pathPos; i++) {
				if (childResolvables.get(i).equals(childConfigPath)) {
					return true;
				}
			}
			return false;
		}

		private void extractIncludePaths(
				XMLConfiguration childConfig, Path childConfigPath, int pathPos) {
			
			List<String> includes = childConfig.getList(
					String.class, INCLUDE_FILE_KEY, new ArrayList<>());
			childConfig.clearTree(INCLUDE_FILE_KEY);
			Path parentPath = childConfigPath.getParent();
			List<ResolvablePath> includeResolvables = new ArrayList<>();
			for (String include : includes) {
				ResolvablePath resolvable = new ResolvablePath(parentPath, include);
				log.debug("Including config path at pos {}: {}: {}", pathPos, parentPath, include);
				includeResolvables.add(resolvable);
			}
			childResolvables.addAll(pathPos, includeResolvables);
		}
		
		private void extractKeySet() {
			keySet = new LinkedHashSet<>();
			String key = "";
			ImmutableNode node = combinedConfig.getNodeModel().getRootNode();
			extractKeySet(key, node, true);
		}

		private void extractKeySet(String key, ImmutableNode node, boolean isRootNode) {
			String keyPlusIndex = "";
			if (!isRootNode) {
				keyPlusIndex = getKeyPlusIndex(key, node.getNodeName());
				if (keyPlusIndex.startsWith(INCLUDE_FILE_KEY + "(") ||
						keyPlusIndex.startsWith(USER_DEFINED_KEY + "(")) {
					return;
				}
				addNodeKey(keyPlusIndex);
			}
			for (String attribName: node.getAttributes().keySet()) {
				addAttribKey(keyPlusIndex, attribName);
			}
			for (ImmutableNode child : node.getChildren()) {
				extractKeySet(keyPlusIndex, child, false);
			}
		}
		
		private String getKeyPlusIndex(String key, String nodeName) {
			String keyPlusIndex = null;
			boolean availableIndexFound = false;
			// increment index until we find one that has not yet 
			// been used by another key
			for (int i = 0; !availableIndexFound; i++) {
				keyPlusIndex = 
						(key.length() == 0 ? "" : key + ".") +
						nodeName + "(" + i + ")";
				availableIndexFound = !keySet.contains(keyPlusIndex);
			}
			return keyPlusIndex;
		}

		private String addNodeKey(String nodeKey) {
			keySet.add(nodeKey);
			return nodeKey;
		}

		private String addAttribKey(String nodeKey, String attribName) {
			String attribKey = nodeKey + "[@" + attribName + "]";
			keySet.add(attribKey);
			return attribKey;
		}
		
		private static class ResolvablePath {
			public Path parentPath;
			public String other;
			public Path resolvedPath;
			
			public ResolvablePath(Path parentPath, String other) {
				this.parentPath = parentPath;
				this.other = other;
			}
			
			public ResolvablePath(Path resolvedPath) {
				this.resolvedPath = resolvedPath;
			}
		}
	}
}
