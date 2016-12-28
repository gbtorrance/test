package org.tdc.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Support for standardized naming and addressing of high-level domain objects such as Schema, Model, and BookDef 
 */
public class Addr implements Comparable<Addr> {
	
	private final Path addrPath;
	
	public Addr(String addr) {
		this.addrPath = standardizeAddrPath(addr);
	}
	
	public Addr(Path path) {
		this(path.toString());
	}
	
	public Addr getParentAddr() {
		int nameCount = addrPath.getNameCount();
		if (nameCount <= 1) {
			throw new IllegalStateException("Addr has no parent: " + addrPath);
		}
		Path parentAddrPath = addrPath.subpath(0,  nameCount-1);
		return new Addr(parentAddrPath.toString());
	}
	
	public Path getPath() {
		return addrPath;
	}
	
	public String getName() {
		Path name = addrPath.getFileName();
		if (name == null) {
			throw new IllegalStateException("Unable to get name from Addr: " + addrPath);
		}
		return name.toString();
	}
	
	public Addr resolve(String append) {
		Path resolvedPath = addrPath.resolve(append);
		return new Addr(resolvedPath);
	}
	
	public Addr resolve(Addr append) {
		Path resolvedPath = addrPath.resolve(append.getPath());
		return new Addr(resolvedPath);
	}
	
	@Override
	public String toString() {
		return addrPath.toString(); 
	}

	@Override
	public int hashCode() {
		return addrPath.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Addr)) {
			return false;
		}
		Addr compareAddr = (Addr)obj;
		return addrPath.equals(compareAddr.addrPath);
	}
	
	@Override
	public int compareTo(Addr o) {
		return addrPath.toString().compareTo(o.addrPath.toString());
	}
	
	private Path standardizeAddrPath(String addr) {
		// strip any initial slash or back slash, 
		// and split on any subsequent slash or backslash
		String[] addrParts = addr.split("/|[\\\\]");
		return Paths.get("", addrParts);
	}
}
