package org.tdc.config.util;

import java.util.Map;

import org.apache.commons.configuration2.interpol.Lookup;

/**
 * Apache Commons Configuration {@link Lookup} implementation
 * to support allowing variables to be "looked up" 
 * from a supplied {@link Map} using a key value. 
 */
public class TDCLookup implements Lookup {
	private final Map<String, String> lookupMap;
	
	public TDCLookup(Map<String, String> lookupMap) {
		this.lookupMap = lookupMap;
	}

	@Override
	public String lookup(String key) {
		return lookupMap == null ? null : lookupMap.get(key);
	}
}
