package org.tdc.server;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfigFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.system.ServerConfig;
import org.tdc.process.BookProcessor;
import org.tdc.process.ModelProcessor;
import org.tdc.server.process.ServerProcessor;
import org.tdc.server.process.ServerProcessorImpl;

/**
 * Class containing general server/servlet functionality.
 * 
 * <p>It is responsible for general initialization, as well as starting and stopping
 * an embedded Jetty servlet container for hosting REST services.
 */
public class TDCServer {
	private static final Logger log = LoggerFactory.getLogger(TDCServer.class);

	private final ServerProcessor serverProcessor;
	private final Server server;
	private final Servlet servlet;
	private final ServletHolder servletHolder; 
	private final ServletContextHandler servletContextHandler;
	
	public TDCServer(
			ServerConfig serverConfig, 
			SchemaConfigFactory schemaConfigFactory, 
			ModelConfigFactory modelConfigFactory, 
			BookConfigFactory bookConfigFactory, 
			ModelProcessor modelProcessor, 
			BookProcessor bookProcessor) {
		
		serverProcessor = new ServerProcessorImpl
				.Builder(
						serverConfig, 
						schemaConfigFactory, 
						modelConfigFactory, 
						bookConfigFactory, 
						modelProcessor, 
						bookProcessor)
				.build();
		
		server = new Server(serverConfig.getServerPort());
		servlet = new HttpServletDispatcher();
		servletHolder = new ServletHolder(servlet);
		servletHolder.setInitParameter("javax.ws.rs.Application", TDCApplication.class.getName());
		servletContextHandler = new ServletContextHandler();
		servletContextHandler.addServlet(servletHolder, "/*");
		server.setHandler(servletContextHandler);
		
		servletContextHandler.setAttribute(
				TDCApplication.ATTRIB_SERVER_PROCESSOR, serverProcessor);
	}
	
	public void startAndWait() {
		try {
			server.start();
			server.join();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to start server", e);
		}
	}
	
	public void start() {
		try {
			server.start();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to start server", e);
		}
	}

	public void stop() {
		try {
			server.stop();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to stop server", e);
		}
	}
}
