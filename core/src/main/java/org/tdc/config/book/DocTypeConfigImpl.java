package org.tdc.config.book;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tdc.config.util.Config;

/**
 * A {@link DocTypeConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class DocTypeConfigImpl implements DocTypeConfig {
	
	private final String docTypeName;
	private final int minPerTestCase;
	private final int maxPerTestCase;
	
	private DocTypeConfigImpl(Builder builder) {
		this.docTypeName = builder.docTypeName;
		this.minPerTestCase = builder.minPerTestCase;
		this.maxPerTestCase = builder.maxPerTestCase;
	}

	@Override
	public String getDocTypeName() {
		return docTypeName;
	}

	@Override
	public int getMinPerTestCase() {
		return minPerTestCase;
	}

	@Override
	public int getMaxPerTestCase() {
		return maxPerTestCase;
	}
	
	public static class Builder {
		private static final String CONFIG_PREFIX = "DocTypes.DocType";

		private final Config config;

		private String docTypeName;
		private int minPerTestCase;
		private int maxPerTestCase;
		
		public Builder(Config config) {
			this.config = config;
		}
		
		public Map<String, DocTypeConfig> buildAll() {
			Map<String, DocTypeConfig> map = new LinkedHashMap<>();
			int count = config.getCount(CONFIG_PREFIX);
			for (int i = 0; i < count; i++) {
				DocTypeConfig docTypeConfig = build(i);
				map.put(docTypeConfig.getDocTypeName(), docTypeConfig);
			}
			return map;
		}

		private DocTypeConfig build(int index) {
			String indexPrefix = CONFIG_PREFIX + "(" + index + ").";
			docTypeName = config.getString(indexPrefix + "DocTypeName", null, true);
			minPerTestCase = config.getInt(indexPrefix + "MinPerTestCase", 1, false);
			maxPerTestCase = config.getInt(indexPrefix + "MaxPerTestCase", 1, false);
			return new DocTypeConfigImpl(this);
		}
	}
}
