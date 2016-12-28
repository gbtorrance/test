package org.tdc.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link LRUCache}.
 */
public class LRUCacheTest {
	@Test
	public void testEntriesAboveMaxSizeRemoved() {
		LRUCache<String,String> cache = new LRUCache<>(3);
		
		cache.put("Key 1", "Value 1");
		cache.put("Key 2", "Value 2");
		cache.put("Key 3", "Value 3");
		cache.put("Key 4", "Value 4");
		
		assertThat(cache.size()).isEqualTo(3);
		assertThat(cache.get("Key 1")).isNull(); // least recently used; removed
		assertThat(cache.get("Key 2")).isEqualTo("Value 2");
		assertThat(cache.get("Key 3")).isEqualTo("Value 3");
		assertThat(cache.get("Key 4")).isEqualTo("Value 4");
	}
	
	@Test
	public void testEntriesAboveMaxSizeRemovedWithAccessOrderAffectingRemoval() {
		LRUCache<String,String> cache = new LRUCache<>(3);
		
		cache.put("Key 1", "Value 1");
		cache.put("Key 2", "Value 2");
		cache.put("Key 3", "Value 3");
		cache.get("Key 1");
		cache.get("Key 2");
		cache.put("Key 4", "Value 4");
		
		assertThat(cache.size()).isEqualTo(3);
		assertThat(cache.get("Key 1")).isEqualTo("Value 1");
		assertThat(cache.get("Key 2")).isEqualTo("Value 2");
		assertThat(cache.get("Key 3")).isNull(); // least recently used; removed
		assertThat(cache.get("Key 4")).isEqualTo("Value 4");
	}
}
