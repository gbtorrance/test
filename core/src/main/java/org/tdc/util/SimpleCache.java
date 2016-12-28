package org.tdc.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple {@link Cache} implementation. 
 */
public class SimpleCache<K,V> implements Cache<K,V> {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleCache.class);
	
	private final Map<K,V> map = new HashMap<>();

	@Override
	public V get(K key) {
		return map.get(key);
	}
	
	@Override
	public void put(K key, V value) {
		map.put(key, value);
	}
	
	@Override
	public int size() {
		return map.size();
	}
}
