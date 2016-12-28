package org.tdc.schemaparse.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.xs.XSElementDeclaration;
import org.tdc.config.util.Config;

/**
 * A {@link SchemaFilter} implementation.
 * 
 * <p>SchemaFilter configurations can be defined in a Model configuration
 * file using IncludePath and ExcludePath statements. These statements
 * are similar to XPath statements in that they are composed of a sequence
 * of Element names separated by "/" characters. They must always begin
 * with the root Element, and cannot contain any special XPath notation.
 * For example, a legal statement might look like this
 *  {@code /RootElement/ChildA/ChildB}.
 *
 * <p>Include and Exclude statements can be defined in almost any
 * combination, with the exception that an include and exclude cannot
 * be defined for the same element. Also, a high-level Element can 
 * be defined for exclusion, and a child of this Element can be defined
 * for inclusion. This class will determine which excluded parent Elements need
 * to be included to support inclusion of the child element, and will
 * only include the necessary excluded parent elements to make this happen.
 * Any other excluded higher-level elements will remain excluded. 
 * This enables easy exclusion of the majority of a schema at the high level, and then
 * inclusion of only the sub Elements that are of interest. 
 * 
 */
public class SchemaFilterImpl implements SchemaFilter {
	private final Map<String, SchemaFilterAction> includeExcludePathMap;
	
	private SchemaFilterImpl(Builder builder) {
		this.includeExcludePathMap = builder.includeExcludePathMap;
	}

	@Override
	public boolean includeElementInModel(String parentPath, XSElementDeclaration xsCurrentElement) {
		boolean includeElement = true;
		if (includeExcludePathMap != null) {
			String fullPath = parentPath + "/" + xsCurrentElement.getName();
			includeElement = includePath(fullPath, true);
		}
		return includeElement;
	}
	
	private boolean includePath(String path, boolean isPrimaryElement) {
		if (path.length() == 0) {
			return true;
		}
		SchemaFilterAction action = includeExcludePathMap.get(path);
		if (action == SchemaFilterAction.EXCLUDE) {
			return false;
		}
		else if (action == SchemaFilterAction.INCLUDE) {
			return true;
		}
		else if (isPrimaryElement) {
			if (action == SchemaFilterAction.INCLUDE_PATH) {
				return true;
			}
			else if (action == SchemaFilterAction.EXCLUDE_ON_INCLUDE_PATH) {
				return true;
			}
			else {
				// action == null (i.e. not found in map)
				String parentPath = getParentPath(path);
				return includePath(parentPath, false);
			}
		}
		else {
			if (action == SchemaFilterAction.INCLUDE_PATH) {
				String parentPath = getParentPath(path);
				return includePath(parentPath, false);
			}
			else if (action == SchemaFilterAction.EXCLUDE_ON_INCLUDE_PATH) {
				return false;
			}
			else {
				// action == null (i.e. not found in map)
				return true;
			}
		}
	}

	private static String getParentPath(String path) {
		int lastIndex = path.lastIndexOf("/");
		return path.substring(0, lastIndex);
	}

	public static class Builder {
		private List<String> includePathList;
		private List<String> excludePathList;
		public Map<String, SchemaFilterAction> includeExcludePathMap;

		public SchemaFilter buildFromConfig(Config config, String key) {
			includePathList = createPathListFromConfig(config, key + ".IncludePaths.IncludePath");
			excludePathList = createPathListFromConfig(config, key + ".ExcludePaths.ExcludePath");
			return build();
		}
		
		public SchemaFilter buildFromLists(List<String> includePathList, List<String> excludePathList) {
			this.includePathList = includePathList;
			this.excludePathList = excludePathList;
			return build();
		}
		
		private SchemaFilter build() {
			includeExcludePathMap = null;
			// if there are no excludes, everything is, by definition, included
			if (excludePathList != null && excludePathList.size() != 0) {
				includeExcludePathMap = new HashMap<>();
				addIncludePathsToMap();
				addExcludePathsToMap();
			}
			return new SchemaFilterImpl(this);
		}

		private void addIncludePathsToMap() {
			if (includePathList != null) {
				for (String path : includePathList) {
					String normalizedPath = normalize(path);
					addIncludePathToMap(normalizedPath, SchemaFilterAction.INCLUDE);
				}
			}
		}

		private void addIncludePathToMap(String path, SchemaFilterAction action) {
			if (path == null || path.length() == 0) {
				return;
			}
			SchemaFilterAction existingAction = includeExcludePathMap.get(path);
			if (existingAction == SchemaFilterAction.INCLUDE) {
				// INCLUDE takes precedence over INCLUDE_PATH
				action = SchemaFilterAction.INCLUDE;
			}
			includeExcludePathMap.put(path, action);
			String parentPath = getParentPath(path);
			addIncludePathToMap(parentPath, SchemaFilterAction.INCLUDE_PATH);
		}
		
		private void addExcludePathsToMap() {
			if (excludePathList != null) {
				for (String path : excludePathList) {
					String normalizedPath = normalize(path);
					addExcludePathToMap(normalizedPath);
				}
			}
		}

		private void addExcludePathToMap(String path) {
			if (path == null || path.length() == 0) {
				return;
			}
			SchemaFilterAction action;
			SchemaFilterAction existingAction = includeExcludePathMap.get(path);
			if (existingAction == SchemaFilterAction.INCLUDE) {
				throw new IllegalStateException(
						"Include/Exclude filter actions conflict for: " + path);
			}
			else if (existingAction == SchemaFilterAction.INCLUDE_PATH) {
				action = SchemaFilterAction.EXCLUDE_ON_INCLUDE_PATH;
			}
			else {
				action = SchemaFilterAction.EXCLUDE;
			}
			includeExcludePathMap.put(path, action);
		}

		private static String normalize(String path) {
			String[] splitPath = path.split("/");
			StringBuilder concatPath = new StringBuilder();
			for (int i = 0; i < splitPath.length; i++) {
				String element = splitPath[i].trim(); 
				if (element.length() != 0) {
					concatPath.append("/").append(element);
				}
			}
			return concatPath.toString();
		}
		
		private List<String> createPathListFromConfig(Config config, String key) {
			List<String> pathList = null;;
			int numPaths = config.getCount(key);
			if (numPaths > 0) {
				pathList = new ArrayList<>();
			}
			for (int i = 0; i < numPaths; i++) {
				String path = config.getString(key + "(" + i + ")", null, true);
				pathList.add(path);
			}
			return pathList;
		}
	}
}
