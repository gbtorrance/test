package org.tdc.schemaparse.extractor;

import java.util.List;

import org.tdc.config.util.Config;

/**
 * Interface defining factory for creating SchemaExtractor instances based
 * on information extracted from an XML config file.
 */
public interface SchemaExtractorFactory {
	SchemaExtractor createSchemaExtractor(Config config, String extractorKey);
	List<SchemaExtractor> createSchemaExtractors(Config config, String extractorsKey);
}
