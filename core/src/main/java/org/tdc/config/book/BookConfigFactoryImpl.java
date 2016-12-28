package org.tdc.config.book;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.ConfigFinder;
import org.tdc.util.SimpleCache;

/**
 * A {@link BookConfigFactory} implementation.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class BookConfigFactoryImpl implements BookConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(BookConfigFactoryImpl.class);

	private final Cache<Addr,BookConfig> cache = new SimpleCache<>();
	private final Path booksConfigRoot;
	private final ModelConfigFactory modelConfigFactory;
	private final FilterConfigFactory filterConfigFactory;
	private final TaskConfigFactory taskConfigFactory;
	
	public BookConfigFactoryImpl(Path booksConfigRoot, 
			ModelConfigFactory modelConfigFactory,
			FilterConfigFactory filterConfigFactory,
			TaskConfigFactory taskConfigFactory) {
		
		this.booksConfigRoot = booksConfigRoot;
		this.modelConfigFactory = modelConfigFactory;
		this.filterConfigFactory = filterConfigFactory;
		this.taskConfigFactory = taskConfigFactory;
	}

	@Override
	public synchronized BookConfig getBookConfig(Addr addr) {
		BookConfig bookConfig = cache.get(addr);
		if (bookConfig == null) {
			bookConfig = new BookConfigImpl.Builder(
					booksConfigRoot, addr, modelConfigFactory, 
					filterConfigFactory, taskConfigFactory).build();
			cache.put(addr, bookConfig);
		}
		else {
			log.debug("Found cached BookConfig for: {}", addr);
		}
		return bookConfig;
	}

	@Override
	public boolean isBookConfig(Addr addr) {
		return getAllBookConfigAddrs()
				.stream()
				.anyMatch(a -> a.equals(addr));
	}

	@Override
	public List<Addr> getAllBookConfigAddrs() {
		return ConfigFinder.findAllConfigsContainingConfigFile(
				booksConfigRoot, BookConfigImpl.CONFIG_FILE);
	}

	@Override
	public List<BookConfig> getAllBookConfigs(Map<Addr, Exception> errors) {
		List<Addr> allConfigAddrs = getAllBookConfigAddrs();
		List<BookConfig> bookConfigs = new ArrayList<>();
		for (Addr addr : allConfigAddrs) {
			processConfigWithErrorTracking(addr, bookConfigs, errors);
		}
		return bookConfigs;
	}

	private void processConfigWithErrorTracking(Addr addr, List<BookConfig> bookConfigs, Map<Addr, Exception> errors) {
		try {
			BookConfig bookConfig = getBookConfig(addr);
			bookConfigs.add(bookConfig);
		}
		catch (RuntimeException e) {
			if (errors == null) {
				throw e;
			}
			else {
				errors.put(addr, e);
				log.debug("Exception encountered while getting config for " + addr, e);
			}
		}
	}
}
