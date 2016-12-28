package org.tdc.server;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.tdc.server.process.ServerProcessor;
import org.tdc.server.resource.BooksResource;
import org.tdc.server.resource.ConfigBooksResource;
import org.tdc.server.resource.ConfigModelsResource;
import org.tdc.server.resource.ConfigSchemasResource;

/**
 * REST application for providing TDC services.
 */
public class TDCApplication extends Application {
	public static final String ATTRIB_SERVER_PROCESSOR = "tdc.serverProcessor";
	
	private final Set<Object> singletons = new HashSet<>();
	private final ServletContext servletContext;
	private final ServerProcessor serverProcessor;
	
	public TDCApplication(@Context ServletContext servletContext) {
		this.servletContext = servletContext;

		serverProcessor = (ServerProcessor)servletContext.getAttribute(ATTRIB_SERVER_PROCESSOR);
		
		ConfigSchemasResource configSchemasResource = new ConfigSchemasResource(serverProcessor);
		ConfigModelsResource configModelsResource = new ConfigModelsResource(serverProcessor);
		ConfigBooksResource configBooksResource = new ConfigBooksResource(serverProcessor);
		BooksResource booksResource =  new BooksResource(serverProcessor); 

		singletons.add(configSchemasResource);
		singletons.add(configModelsResource);
		singletons.add(configBooksResource);
		singletons.add(booksResource);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
