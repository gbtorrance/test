package org.tdc.config.book;

import org.tdc.config.model.ModelConfig;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.book.Page Pages}.
 */
public interface PageConfig {
	String getPageName();
	ModelConfig getModelConfig();
	DocTypeConfig getDocTypeConfig();
	PageStructConfig getPageStructConfig();
}
