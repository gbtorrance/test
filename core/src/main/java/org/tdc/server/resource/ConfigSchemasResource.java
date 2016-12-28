package org.tdc.server.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.tdc.server.dto.SchemaConfigDTO;
import org.tdc.server.process.ServerProcessor;

/**
 * REST Resource for providing Schema Config info to the client.
 */
@Path("/tdc/config/schemas")
public class ConfigSchemasResource {
	private final ServerProcessor processor;
	
	public ConfigSchemasResource(ServerProcessor processor) {
		this.processor = processor;
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public List<SchemaConfigDTO> getConfigs() {
		return processor.getSchemaConfigDTOs();
	}
}
