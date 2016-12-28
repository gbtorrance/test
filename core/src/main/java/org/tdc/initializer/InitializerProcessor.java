package org.tdc.initializer;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.system.InitializerConfig;
import org.tdc.config.system.SystemConfig;

/**
 * Responsible for processing, in sequence, a series of {@link Initializer}s.
 */
public class InitializerProcessor {
	private final List<Initializer> initializers;
	
	private InitializerProcessor(Builder builder) {
		this.initializers = builder.initializers;
	}
	
	public void processInitializers() {
		for (Initializer initializer : initializers) {
			processInitializer(initializer);
		}
	}
	
	private void processInitializer(Initializer initializer) {
		initializer.process();
	}

	public static class Builder {
		private final InitializerFactory initializerFactory;
		private final SystemConfig config;
		
		private List<Initializer> initializers;
		
		public Builder(InitializerFactory initializerFactory, SystemConfig config) {
			this.initializerFactory = initializerFactory;
			this.config = config;
		}
		
		public InitializerProcessor build() {
			this.initializers = createInitializers();
			return new InitializerProcessor(this);
		}
		
		private List<Initializer> createInitializers() {
			List<Initializer> list = new ArrayList<>();
			List<InitializerConfig> initializerConfigs = config.getInitializerConfigs();
			for (InitializerConfig initializerConfig : initializerConfigs) {
				Initializer initializer = initializerFactory.createInitializer(initializerConfig);
				list.add(initializer);
			}
			return list;
		}
	}
}
