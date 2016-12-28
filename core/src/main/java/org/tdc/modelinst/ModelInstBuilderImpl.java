package org.tdc.modelinst;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorType;
import org.tdc.model.MPathBuilder;
import org.tdc.model.MPathIndex;
import org.tdc.model.NonAttribNode;
import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.modeldef.NonAttribNodeDef;
import org.tdc.util.Util;

/**
 * A {@link ModelInstBuilder} implementation.
 * 
 * Does the complex work of constructing an in-memory {@link ModelInst} object tree
 * from a {@link ModelDef}, and then injects the root {@link ElementNodeInst},
 * along with other dependencies, into a new {@link ModelInst}, which is then returned.
 */
public class ModelInstBuilderImpl implements ModelInstBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ModelInstBuilderImpl.class);

	private final ModelDef modelDef;
	private final int defaultOccursCount;
	
	private int rowOffset;
	private MPathIndex<NodeInst> mpathIndex; 
	private MPathBuilder mpathBuilder;
	private ModelInstSharedState sharedState;
	
	public ModelInstBuilderImpl(ModelDef modelDef, int defaultOccursCount) {
		this.modelDef = modelDef;
		this.defaultOccursCount = defaultOccursCount;
	}
	
	@Override
	public ModelInst build() {
		log.debug("Start building ModelInst tree");
		mpathIndex = new MPathIndex<>();
		sharedState = new ModelInstSharedState();
		ElementNodeInst rootElementInst = buildRootElementInst(modelDef.getRootElement());
		log.debug("Finish building ModelInst tree: rootElementInst: {}", rootElementInst.getName()); 
		return new ModelInstImpl(modelDef, rootElementInst, mpathIndex, sharedState);
	}
	
	private ElementNodeInst buildRootElementInst(ElementNodeDef rootElementNodeDef) {
		mpathBuilder = new MPathBuilder();
		rowOffset = 0;
		List<? extends NonAttribNodeInst> list;
		list = buildTree(null, rootElementNodeDef, 0);
		if (list.size() != 1) {
			throw new IllegalStateException("There should only be one 'root' element, not " + list.size());
		}
		mpathBuilder = null;
		return (ElementNodeInst)list.get(0);
	}
	
	// TODO consider simplifying use of generics by using List<NonAttribNodeInst> for all return types (rather than bounded wildcards)
	private List<? extends NonAttribNodeInst> buildTree(
			NonAttribNodeInst parentNonAttribNodeInst, NonAttribNodeDef nonAttribNodeDef, int colOffset) {
		
		List<? extends NonAttribNodeInst> list;
		if (nonAttribNodeDef instanceof ElementNodeDef) {
			ElementNodeDef elementNodeDef = (ElementNodeDef)nonAttribNodeDef;
			list = buildOccurrenceElements(parentNonAttribNodeInst, elementNodeDef, colOffset);
		}
		else if (nonAttribNodeDef instanceof CompositorNodeDef) {
			CompositorNodeDef compositorNodeDef = (CompositorNodeDef)nonAttribNodeDef;
			if (canFlattenCompositor(compositorNodeDef)) {
				list = flattenCompositor(parentNonAttribNodeInst, compositorNodeDef, colOffset);
			}
			else {
				list = buildOccurrenceCompositors(parentNonAttribNodeInst, compositorNodeDef, colOffset);
			}
		}
		else {
			throw new IllegalStateException("Non Attrib Node Def is of invalid type: " + nonAttribNodeDef.getClass().getTypeName());
		}
		return list;
	}
	
	private List<ElementNodeInst> buildOccurrenceElements(
			NonAttribNodeInst parentNonAttribNodeInst, ElementNodeDef elementNodeDef, int colOffset) {
		
		List<ElementNodeInst> occurrenceList = new ArrayList<>();
		ElementNodeInst elementNodeInst;
		int occurCount = getOccursCount(elementNodeDef);
		for (int i = 0; i < occurCount; i++) {
			elementNodeInst = buildElement(parentNonAttribNodeInst, elementNodeDef, colOffset, i+1, occurCount);
			rowOffset++;
			mpathBuilder.zoomIn();
			buildPreNotes(elementNodeInst);
			buildPostNotes(elementNodeInst);
			buildAttribs(elementNodeInst);
			buildChildren(elementNodeInst);
			mpathBuilder.zoomOut();
			occurrenceList.add(elementNodeInst);
		}
		return occurrenceList;
	}
	
	private ElementNodeInst buildElement(
			NonAttribNodeInst parentNonAttribNodeInst, ElementNodeDef elementNodeDef, 
			int colOffset, int occurNum, int occurCount) {
		
		ElementNodeInst elementNodeInst = new ElementNodeInst(parentNonAttribNodeInst, sharedState, elementNodeDef);
		elementNodeInst.setRowOffset(rowOffset);
		elementNodeInst.setColOffset(colOffset);
		elementNodeInst.setOccurNum(occurNum);
		elementNodeInst.setOccurCount(occurCount);
		elementNodeInst.setMPath(buildMPath(elementNodeInst, occurNum == 1));
		return elementNodeInst;
	}
	
	private List<CompositorNodeInst> buildOccurrenceCompositors(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, int colOffset) {
		
		List<CompositorNodeInst> occurrenceList = new ArrayList<>();
		CompositorNodeInst compositorNodeInst;
		int occurCount = getOccursCount(compositorNodeDef);
		for (int i = 0; i < occurCount; i++) {
			compositorNodeInst = buildCompositor(parentNonAttribNodeInst, compositorNodeDef, colOffset, i+1, occurCount);
			rowOffset++;
			mpathBuilder.zoomIn();
			buildPreNotes(compositorNodeInst);
			buildPostNotes(compositorNodeInst);
			buildChildren(compositorNodeInst);
			mpathBuilder.zoomOut();
			occurrenceList.add(compositorNodeInst);
		}
		return occurrenceList;
	}
	
	private CompositorNodeInst buildCompositor(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, 
			int colOffset, int occurNum, int occurCount) {
		
		CompositorNodeInst compositorNodeInst = new CompositorNodeInst(parentNonAttribNodeInst, sharedState, compositorNodeDef);
		compositorNodeInst.setRowOffset(rowOffset);
		compositorNodeInst.setColOffset(colOffset);
		compositorNodeInst.setOccurNum(occurNum);
		compositorNodeInst.setOccurCount(occurCount);
		compositorNodeInst.setMPath(buildMPath(compositorNodeInst, occurNum == 1));
		return compositorNodeInst;
	}
	
	private void buildAttribs(ElementNodeInst parentElementNodeInst) {
		ElementNodeDef elementNodeDef = parentElementNodeInst.getNodeDef();
		if (elementNodeDef.hasAttribute()) {
			for (AttribNodeDef attribNodeDef : elementNodeDef.getAttributes()) {
				if (getOccursCount(attribNodeDef) == 1) {
					AttribNodeInst attribNodeInst = buildAttrib(parentElementNodeInst, attribNodeDef, 
							parentElementNodeInst.getColOffset()+1);
					rowOffset++;
					buildPreNotes(attribNodeInst);
					buildPostNotes(attribNodeInst);
					parentElementNodeInst.addAttribute(attribNodeInst);
				}
			}
		}
	}
	
	private int getOccursCount(NodeDef nodeDef) {
		int override = nodeDef.getOccursCountOverride();
		int occursCount = override;
		if (override == Util.UNDEFINED) {
			int maxOccurs;
			int minOccurs;
			boolean isUnbounded = false;
			if (nodeDef instanceof AttribNode) {
				AttribNode attrib = (AttribNode)nodeDef;
				maxOccurs = 1;
				minOccurs = attrib.isRequired() ? 1 : 0;
			}
			else {
				// if it's not an AttribNode, it's a NonAttribNode
				NonAttribNode nonAttrib = (NonAttribNode)nodeDef;
				maxOccurs = nonAttrib.getMaxOccurs();
				minOccurs = nonAttrib.getMinOccurs();
				isUnbounded = nonAttrib.isUnbounded();
			}
			occursCount = defaultOccursCount;
			occursCount = isUnbounded ? occursCount : Integer.min(maxOccurs, occursCount);
			occursCount = Integer.max(minOccurs, occursCount);
		}
		return occursCount;
	}

	private AttribNodeInst buildAttrib(
			ElementNodeInst parentElementNodeInst, AttribNodeDef attribNodeDef, int colOffset) {
		
		AttribNodeInst attribNodeInst = new AttribNodeInst(parentElementNodeInst, sharedState, attribNodeDef);
		attribNodeInst.setRowOffset(rowOffset);
		attribNodeInst.setColOffset(colOffset);
		attribNodeInst.setMPath(buildMPath(attribNodeInst, true));
		return attribNodeInst;
	}
	
	private void buildChildren(NonAttribNodeInst parentNonAttribNodeInst) {
		List<? extends NonAttribNodeInst> childList;
		for (NonAttribNodeDef nonAttribNodeDefChild : parentNonAttribNodeInst.getNodeDef().getChildren()) {
			childList = buildTree(parentNonAttribNodeInst, nonAttribNodeDefChild, 
					parentNonAttribNodeInst.getColOffset()+1);
			parentNonAttribNodeInst.addChildAll(childList);
		}
	}
	
	private boolean canFlattenCompositor(CompositorNodeDef compositorNodeDef) {
		// TODO add configuration for flattening sequence and choice
		// TODO cleanup code
		
		// flatten compositor if:
		// - it is a sequence with one and only one occurrence (and)
		//   - its parent is an element (or)
		//   - its parent is a compositor which is NOT a choice
		//     (because if it's a choice, it needs to be clearly marked as such in the output, so it can't be flattened)
		boolean canFlatten = false;
		if ((compositorNodeDef.getCompositorType() == CompositorType.SEQUENCE) && //|| compositorNodeDef.getType() == CompositorType.CHOICE) &&
				compositorNodeDef.getMinOccurs() == 1 &&
				compositorNodeDef.getMaxOccurs() == 1 &&
				(compositorNodeDef.getParent() instanceof ElementNodeDef ||
						(compositorNodeDef.getParent() instanceof CompositorNodeDef &&
								((CompositorNodeDef)compositorNodeDef.getParent()).getCompositorType() != CompositorType.CHOICE
						)
				)
			) {
			canFlatten = true;
		}
		return canFlatten;
	}
	
	private List<? extends NonAttribNodeInst> flattenCompositor(
			NonAttribNodeInst parentNonAttribNodeInst, CompositorNodeDef compositorNodeDef, int colOffset) {
		
		List<NonAttribNodeInst> list = new ArrayList<>();
		List<? extends NonAttribNodeInst> childList;
		
		buildFlattenedNodeMPath(compositorNodeDef);
		
		mpathBuilder.zoomIn();
		for (NonAttribNodeDef nonAttribNodeDefChild : compositorNodeDef.getChildren()) {
			if (nonAttribNodeDefChild instanceof CompositorNodeDef && 
					canFlattenCompositor((CompositorNodeDef)nonAttribNodeDefChild)) {
				
				childList = flattenCompositor(parentNonAttribNodeInst, (CompositorNodeDef)nonAttribNodeDefChild, colOffset);
			}
			else {
				childList = buildTree(parentNonAttribNodeInst, nonAttribNodeDefChild, colOffset);
			}
			list.addAll(childList);
		}
		mpathBuilder.zoomOut();
		
		return list;
	}

	private void buildPreNotes(NodeInst nodeInst) {
		// TODO implement notes?
		//buildNotesForMultiOccurs(nodeInst);
		//buildNotesForChoices(nodeInst);
	}
	
	private void buildPostNotes(NodeInst nodeInst) {
		// TODO implement notes?
	}
	
//	private void buildNotesForMultiOccurs(NodeInst nodeInst) {
//		if (nodeInst instanceof NonAttribNodeInst) {
//			NonAttribNodeInst nonAttribNodeInst = (NonAttribNodeInst)nodeInst;
//			if (nonAttribNodeInst.getOccurCount() > 1 && nonAttribNodeInst.getOccurNum() == 1) {
//				NoteInst note = new NoteInst(nodeInst, "Multiples allowed", NoteFormat.BASIC_PRE);
//				nodeInst.addPreNote(note);
//				rowOffset++;
//			}
//		}
//	}
//	
//	private void buildNotesForChoices(NodeInst nodeInst) {
//		if (nodeInst instanceof NonAttribNodeInst) {
//			NonAttribNodeInst nonAttribNodeInst = (NonAttribNodeInst)nodeInst;
//			if (nonAttribNodeInst.isFirstChildOfChoice()) {
//				// need to get count from parent of *def* node, 
//				// as choice compositor parent may have been removed in inst model
//				int count = nonAttribNodeInst.getNodeDef().getParent().getChildren().size();
//				NoteInst note = new NoteInst(nodeInst, "Choose 1 of the next " + count + " element(s)", NoteFormat.BASIC_PRE);
//				nodeInst.addPreNote(note);
//				rowOffset++;
//			}
//		}
//	}
	
	private String buildMPath(NodeInst nodeInst, boolean isFirstOccurrence) {
		NodeDef nodeDef = nodeInst.getNodeDef();
		String mpath = mpathBuilder.buildMPath(nodeDef);
		mpathIndex.addMPath(mpath, nodeInst);
		return mpath;
	}
	
	private void buildFlattenedNodeMPath(NodeDef nodeDef) {
		// even though we don't care about the return value here, we do need
		// to do this to ensure the mpaths sync up correctly between
		// ModelDef and ModelInst models
		mpathBuilder.buildMPath(nodeDef);
	}
}
