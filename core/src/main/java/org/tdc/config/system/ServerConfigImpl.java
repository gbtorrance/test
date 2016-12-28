package org.tdc.config.system;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.util.Config;

/**
 * A {@link ServerConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ServerConfigImpl implements ServerConfig {
	private static final Logger log = LoggerFactory.getLogger(ServerConfigImpl.class);
	
	private static final String BOOKS_WORKING_DIR = "Books";
	private static final int DEFAULT_BOOK_CACHE_MAX_SIZE = 10;

	private final Path workingRoot;
	private final Path booksWorkingRoot;
	private final int serverPort;
	private final int bookCacheMaxSize;
	
	private ServerConfigImpl(Builder builder) {
		this.workingRoot = builder.workingRoot;
		this.booksWorkingRoot = builder.booksWorkingRoot;
		this.serverPort = builder.serverPort;
		this.bookCacheMaxSize = builder.bookCacheMaxSize;
	}
	
	@Override
	public Path getWorkingRoot() {
		return workingRoot;
	}
	
	@Override
	public Path getBooksWorkingRoot() {
		return booksWorkingRoot;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	@Override 
	public int getBookCacheMaxSize() {
		return bookCacheMaxSize;
	}

	public static class Builder {
		private static final String CONFIG_PREFIX = "Server";
		
		private final Config config;
		private final Path systemConfigRoot;
		
		private Path workingRoot;
		private Path booksWorkingRoot;
		private int serverPort;
		private int bookCacheMaxSize;
		
		public Builder(Config config, Path systemConfigRoot) {
			this.config = config;
			this.systemConfigRoot = systemConfigRoot;
		}

		public ServerConfig build() {
			ServerConfig serverConfig = null;
			if (config.hasNode(CONFIG_PREFIX)) {
				workingRoot = systemConfigRoot.resolve(config.getString(
						CONFIG_PREFIX + ".WorkingRoot", null, true));
				if (!Files.isDirectory(workingRoot)) {
					throw new IllegalStateException(
							"WorkingRoot dir does not exist: " + workingRoot.toString());
				}
				booksWorkingRoot = workingRoot.resolve(BOOKS_WORKING_DIR); // will be created as needed
				serverPort = config.getInt(CONFIG_PREFIX + ".ServerPort", 0, true);
				bookCacheMaxSize = config.getInt(
						CONFIG_PREFIX + ".BookCacheMaxSize", DEFAULT_BOOK_CACHE_MAX_SIZE, false);
				serverConfig = new ServerConfigImpl(this);
			}
			return serverConfig;
		}
	}
}
