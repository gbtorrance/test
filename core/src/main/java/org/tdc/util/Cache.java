package org.tdc.util;

/**
 * Defines support for caching objects for reuse. 
 */
public interface Cache<K,V> {
	V get(K key);
	void put(K key, V value);
	int size();
}
