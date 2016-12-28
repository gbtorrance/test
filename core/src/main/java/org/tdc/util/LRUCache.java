package org.tdc.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Cache} implementation that allows the cache to reach a maximum size,
 * at which point the least recently used object will be automatically removed from the cache.
 * 
 * <p>The removal of LRU objects occurs when put() is executed 
 * (if maximum size has been exceeded, of course).
 */
public class LRUCache<K,V> implements Cache<K,V> {
	
	private static final Logger log = LoggerFactory.getLogger(LRUCache.class);
	
	private final int maxSize;
	private final Map<K,V> sizeLimitedMap = new SizeLimitedMap<>();
	
	public LRUCache(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	public V get(K key) {
		return sizeLimitedMap.get(key);
	}
	
	@Override
	public void put(K key, V value) {
		sizeLimitedMap.put(key, value);
	}
	
	@Override
	public int size() {
		return sizeLimitedMap.size();
	}

	@SuppressWarnings("serial")
	private class SizeLimitedMap<K,V> extends LinkedHashMap<K,V> {
		public SizeLimitedMap() {
			// true for 3rd param indicates use of 'access' order rather than 'insert' order
			super(16, 0.75f, true); 
		}
		
		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxSize;
		}
	}
}
