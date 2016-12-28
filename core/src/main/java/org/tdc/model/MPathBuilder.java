package org.tdc.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.NodeDef;

/**
 * MPath builder used during the parsing/building process for Models 
 * ({@link org.tdc.modeldef.ModelDef ModelDef} and {@link org.tdc.modelinst.ModelInst ModelInst}) 
 * to track state as nodes are created, and return MPath addresses to be stored
 * in an {@link MPathIndex} objects.  
 *  
 * @see MPathIndex
 */
public class MPathBuilder {
	
	// TODO why depend on NodeDef objects??? likely due to flattened/missing objects in inst model; consider moving to modeldef package
	
	private final Deque<MPathState> state = new ArrayDeque<>();
	
	public MPathBuilder() {
		state.addLast(new MPathState(null));
	}
	
	public String buildMPath(NodeDef nodeDef) {
		return buildMPath(nodeDef, true);
	}
	
	public String buildMPath(NodeDef nodeDef, boolean isFirstOccurrence) {
		return state.peekLast().buildMPath(getName(nodeDef), isFirstOccurrence);
	}

	public String getMPath() {
		return state.peekLast().getMPath();
	}
	
	public String getLastValidMPath() {
		return state.peekLast().getLastValidMPath();
	}
	
	public void zoomIn() {
		MPathState currentAddrState = state.peekLast();
		if (!currentAddrState.hasMPath()) {
			throw new IllegalStateException("Cannot 'zoom in' since no mpaths have been built for level");
		}
		MPathState newMPathState = new MPathState(currentAddrState);
		state.addLast(newMPathState);
	}
	
	public void zoomOut() {
		if (state.size() == 1) {
			throw new IllegalStateException("Unable to 'zoom out'. Already at top level");
		}
		state.removeLast();
	}
	
	private static String getName(NodeDef nodeDef) {
		String name;
		if (nodeDef instanceof ElementNodeDef) {
			name = ((ElementNodeDef)nodeDef).getName();
		}
		else if (nodeDef instanceof AttribNodeDef) {
			name = "@" + ((AttribNodeDef)nodeDef).getName();
		}
		else if (nodeDef instanceof CompositorNodeDef) {
			CompositorType compositorType = ((CompositorNodeDef)nodeDef).getCompositorType();
			switch (compositorType) {
			case SEQUENCE:
				name = "!S";
				break;
			case CHOICE:
				name = "!C";
				break;
			case ALL:
				name = "!A";
				break;
			default:
				throw new IllegalStateException("Invalid Compositor Type: " + compositorType);
			}
		}
		else {
			throw new IllegalStateException("Invalid Node Def type: " + nodeDef.getClass().getName());
		}
		return name;
	}
	
	private class MPathState {
		MPathState parent;
		private Map<String, Integer> differentiators = new HashMap<>();
		private String currentName;
		private int currentOccurrence;
		private int currentDifferentiator;
		private String mpath;
		
		public MPathState(MPathState parent) {
			this.parent = parent;
		}
		
		public String buildMPath(String name, boolean isFirstOccurrence) {
			if (isFirstOccurrence) {
				currentName = name;
				currentOccurrence = 1;
				Integer diff = differentiators.get(name);
				currentDifferentiator = (diff == null ? 1 : diff.intValue() + 1); 
				differentiators.put(name, Integer.valueOf(currentDifferentiator));
			}
			else {
				if (!currentName.equals(name)) {
					String msg = "Current name '" + currentName + "' is different from supplied name '" + name + 
							"'; Cannot be a 'next occurrence'";
					throw new IllegalStateException(msg);
				}
				if (currentOccurrence == 0) {
					String msg = "Current occurrence (" + currentOccurrence + 
							") must be initialized to use 'next occurrence'";
					throw new IllegalStateException(msg);
				}
				currentOccurrence++;
			}
			mpath = (parent == null ? "" : parent.getMPath()) + "/" + name;
			if (currentDifferentiator > 1) {
				mpath += "}" + currentDifferentiator;
			}
			if (currentOccurrence > 1) {
				mpath += "#" + currentOccurrence;
			}
			return mpath;
		}
		
		public String getMPath() {
			if (mpath == null) {
				throw new IllegalStateException("No mpaths have been built for this level");
			}
			return mpath;
		}
		
		public boolean hasMPath() {
			return mpath != null;
		}
		
		public String getLastValidMPath() {
			return mpath != null ? mpath : (parent != null ? parent.getMPath() : "");
		}
	}
}
