package org.tdc.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Index for associating Model nodes with their MPath addresses.
 * 
 * <p>MPath is a method designed for addressing nodes for both types of Models 
 * ({@link org.tdc.modeldef.ModelDef ModelDef} and {@link org.tdc.modelinst.ModelInst ModelInst}). 
 * 
 * <p>Though it has similarities to XPath, it is not the same. 
 * 
 * <p>XPath addresses nodes at the XML level. For XPath, the only thing that really matters is the structure of the XML itself,
 * regardless of what it took to get to that point.
 * 
 * <p>MPath addresses nodes at the Model level. For MPath, the structure of the nodes within the Model is significant.
 * 
 * <p>MPath includes special tags to represent "sequences" ("!S") and "choices" ("!C"), 
 * as well as tags to represent individual "occurrences" of particular nodes 
 * (with "}2" representing the second occurrence, "}3" representing the third, and so on).
 *   
 * <p>With this approach, nodes in the Models can be addressed, whether or not they contain any data values.
 * Another huge benefit is that, when Models change (possibly due to Schema changes), nodes will generally
 * retain their same MPath, allowing for efficient and accurate upgrading of Models.
 * 
 * <p>Sample MPath:
 * <br><tt>/TestRoot</tt> ..... this is the root node
 * <br><tt>/TestRoot/@AttribA</tt> ..... it has an associated attribute
 * <br><tt>/TestRoot/!S</tt> ..... and it contains a "sequence"
 * <br><tt>/TestRoot/!S/!S</tt> ..... and there is a "sequence" within the earlier "sequence"
 * <br><tt>/TestRoot/!S/!S/M</tt> ..... and an element node called "M"
 * <br><tt>/TestRoot/!S/!S/N</tt> ..... and an element node called "N" 
 * <br><tt>/TestRoot/!S/!S}2</tt> ..... and a second instance of the contained (second) "sequence"
 * <br><tt>/TestRoot/!S/!S}2/M</tt> ..... and an "M" node in that "sequence"
 * <br><tt>/TestRoot/!S/!S}2/N</tt> ..... and an "N" node in that sequence"
 * <br><tt>/TestRoot/!S/B</tt> ..... Here were up one level to the parent "sequence", and it has an element called "B"
 * <br><tt>/TestRoot/!S/B/!S/BA</tt> ... "B" contains its own sequence with an element node "BA"
 * <br><tt>/TestRoot/!S/B/!S/BB</tt> .... and a node "BB"
 * <br><tt>/TestRoot/!S/!S}3/M</tt> ... Here there is another child "sequence" in the parent "sequence", which is effectively a third instance
 * <br><tt>/TestRoot/!S/!S}3/N</tt>
 * <br><tt>/TestRoot/!S/!S}4/T</tt>
 * <br><tt>/TestRoot/!S/!S}4/T/!S/!S/BA</tt>
 * <br><tt>/TestRoot/!S/!S}4/T/!S/!S/BB</tt>
 * <br><tt>/TestRoot/!S/!S}4/T/!S/!C</tt> ..... Notice the "choice"
 * <br><tt>/TestRoot/!S/!S}4/T/!S/!C/TBC</tt> ..... which contains element node "TDC"
 * <br><tt>/TestRoot/!S/!S}4/T/!S/!C/TBD</tt> ..... and element node "TDB"
 * <br><tt>/TestRoot/!S/!S}4/U</tt>
 */
public class MPathIndex<T extends AbstractTDCNode> {
	
	private final Map<String, T> map = new HashMap<>();
	
	public void addMPath(String mpath, T node) {
		map.put(mpath,  node);
	}
	
	public T getNode(String mpath) {
		return map.get(mpath);
	}
	
	public boolean containsKey(String mpath) {
		return map.containsKey(mpath);
	}
}
