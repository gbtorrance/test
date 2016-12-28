package org.tdc.schemaparse;

import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.model.CompositorType;
import org.tdc.model.MPathBuilder;
import org.tdc.model.MPathIndex;
import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.CompositorNodeDef;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefSharedState;
import org.tdc.modeldef.NodeDef;
import org.tdc.modeldef.NonAttribNodeDef;
import org.tdc.schemaparse.extractor.SchemaAnnotationExtractor;
import org.tdc.schemaparse.extractor.SchemaDataTypeExtractor;
import org.tdc.schemaparse.extractor.SchemaExtractor;
import org.tdc.schemaparse.filter.SchemaFilter;

/**
 * Takes an Apache Xerces {@link XSModel} representation of a Schema 
 * (built using an {@link ModelDefXSModelBuilder}), and uses this to 
 * construct a {@link ModelDef} object tree, returning the root {@link ElementNodeDef}.
 */
public class ModelDefSchemaParser {

	private static final Logger log = LoggerFactory.getLogger(ModelDefSchemaParser.class);

	private final XSModel xsModel;
	private final MPathIndex<NodeDef> mpathIndex; 
	private final MPathBuilder mpathBuilder = new MPathBuilder();
	private final ModelDefSharedState sharedState;
	private final StringBuilder currentElementPath = new StringBuilder();
	private final SchemaFilter schemaFilter;
	private final List<SchemaExtractor> schemaExtractors;
	
	private int rowOffset;

	private ModelDefSchemaParser(Builder builder) {
		this.xsModel = builder.xsModel;
		this.mpathIndex = builder.mpathIndex;
		this.sharedState = builder.sharedState;
		this.schemaFilter = builder.schemaFilter;
		this.schemaExtractors = builder.schemaExtractors;
	}
	
	public ElementNodeDef buildModelDefTreeFromSchema(
			String rootElementName, 
			String rootElementNamespace) {

		rowOffset = 0;
		XSElementDeclaration ed = xsModel.getElementDeclaration(rootElementName, rootElementNamespace);

		// create root element with no parent
		ElementNodeDef rootElementNodeDef = new ElementNodeDef(null, sharedState);
		rootElementNodeDef.setMinOccurs(1); // root element must always occur once and only once
		rootElementNodeDef.setMaxOccurs(1); // root element must always occur once and only once

		// build a model tree, beginning with the root
		processElementDeclaration(ed, rootElementNodeDef, 0);

		return rootElementNodeDef;
	}
	
	private void processElementDeclaration(
			XSElementDeclaration xsElementDecl, 
			ElementNodeDef elementNodeDef, 
			int colOffset) {
		
		// set the name
		elementNodeDef.setName(xsElementDecl.getName());
		elementNodeDef.setMPath(buildMPath(elementNodeDef));
		elementNodeDef.setColOffset(colOffset);
		elementNodeDef.setRowOffset(rowOffset++);
		mpathBuilder.zoomIn();
		addElementToElementPath(elementNodeDef.getName());
		colOffset++;
		
		// process data type
		processDataType(xsElementDecl, elementNodeDef);

		// elements can be of a simple type or complex type
		XSTypeDefinition xsTypeDef = xsElementDecl.getTypeDefinition();
		if (xsTypeDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {

			// process simple type definition
			processSimpleTypeDefinition( (XSSimpleTypeDefinition)xsTypeDef, elementNodeDef);
		}
		else if (xsTypeDef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {

			// process complex type definition
			processComplexTypeDefinition( (XSComplexTypeDefinition)xsTypeDef, elementNodeDef, colOffset);
		}
		else {
			// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model
			throw new UnsupportedOperationException("Unknown element type definition: " + xsTypeDef.getName());
		}
		processElementAnnotation(xsElementDecl, elementNodeDef);

		colOffset--;
		removeElementFromElementPath();
		mpathBuilder.zoomOut();
	}
	
	private void processSimpleTypeDefinition(
			XSSimpleTypeDefinition xsSimpleTypeDef, 
			ElementNodeDef elementNodeDef) {
		
		// TODO Anything to do for restriction/list/union ... or restriction/extension? 
	}
	
	private void processComplexTypeDefinition(
			XSComplexTypeDefinition xsComplexTypeDef, 
			ElementNodeDef elementNodeDef, 
			int colOffset) {
		
		// process attributes
		processAttributes(xsComplexTypeDef, elementNodeDef, colOffset);
		
		// process based on type of content
		if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE) {

			// text only content (no other elements)
			processSimpleTypeDefinition(xsComplexTypeDef.getSimpleType(), elementNodeDef);
		}
		else if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT ||
				 xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED ||
				 xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY) {

			// TODO Anything to do for restriction/extension?

			// CONTENTTYPE_ELEMENT:
			// element content (this is where model groups, wildcards, and other elements come into play)
			// [getParticle() only applies to ELEMENT and MIXED content types]

			// CONTENTTYPE_MIXED:
			// set with the "mixed" attribute!
			// refers to a "letter" type of content (with mixed text and embedded elements (order defined by sequence, choice, etc.) 
			// [confirmed that getParticle() only applies to ELEMENT and MIXED content types]
			// Note: we're allowing MIXED types, but there is no is no practical way in TDC to represent the 
			//       "letter" part, so that will be ignored

			// CONTENTTYPE_EMPTY
			// e.g. <element/> or <element abc="123"/>

			// set type
			// applicable to ELEMENT, MIXED, and EMPTY

			// process particle 
			// applicable to ELEMENT and MIXED only;
			// however, since this tool has no true way to represent MIXED content in the model tree,
			// other than simply embedding it in a test value, we will *not* go the particle
			// route for MIXED content; ELEMENT content only
			if (xsComplexTypeDef.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT) {
				processParticle(xsComplexTypeDef.getParticle(), elementNodeDef, colOffset);
			}
		}
		else {
			// should *never* occur; throw unchecked exception to indicate failure to properly understand XSD model 
			throw new UnsupportedOperationException("Unknown content type: " + xsComplexTypeDef.getContentType());
		}
	}
	
	private void processParticle(
			XSParticle xsParticle, 
			NonAttribNodeDef nonAttribNodeDef, 
			int colOffset) {
		
		// get term
		XSTerm xsTerm = (XSTerm)xsParticle.getTerm();
		
		// process based on type of term
		if (xsTerm instanceof XSElementDeclaration) {
			// element declaration
			XSElementDeclaration xsElement = (XSElementDeclaration)xsTerm;

			// I believe this can only occur if the particle is part of a model group (nested particle), 
			// but not when it's part of a complex type model (typedef particle)
			
			// only proceed down this path if filtering does not exclude element
			if (schemaFilter.includeElementInModel(currentElementPath.toString(), xsElement)) {
				
				// create new element (with current element as parent)
				ElementNodeDef newElementNodeDef = new ElementNodeDef(nonAttribNodeDef, sharedState);
				newElementNodeDef.setMinOccurs(xsParticle.getMinOccurs());
				newElementNodeDef.setMaxOccurs(xsParticle.getMaxOccursUnbounded() ? 
						NonAttribNodeDef.MAX_UNBOUNDED : xsParticle.getMaxOccurs());
				
				// add new element as child of current element
				nonAttribNodeDef.addChild(newElementNodeDef);
				
				// process element declaration (for new element node)
				processElementDeclaration(xsElement, newElementNodeDef, colOffset);
			}
		}
		else if (xsTerm instanceof XSModelGroup) {
			// model groups types are "sequence", "choice", "all"
			XSModelGroup xsModelGroup = (XSModelGroup)xsTerm;

			// create compositor node (as child of current element
			CompositorNodeDef compositorNodeDef = createCompositorNode(nonAttribNodeDef, xsModelGroup);
			compositorNodeDef.setMPath(buildMPath(compositorNodeDef));
			compositorNodeDef.setColOffset(colOffset);
			compositorNodeDef.setRowOffset(rowOffset++);
			compositorNodeDef.setMinOccurs(xsParticle.getMinOccurs());
			compositorNodeDef.setMaxOccurs(xsParticle.getMaxOccursUnbounded() ? 
					NonAttribNodeDef.MAX_UNBOUNDED : xsParticle.getMaxOccurs());
			
			// add new compositor node as child of current element
			nonAttribNodeDef.addChild(compositorNodeDef);
			
			// process model group (for new compositor node)
			processModelGroup(xsModelGroup, compositorNodeDef, colOffset);
			
			// filtering may have resulted in an empty compositor; if so, remove
			if (!compositorNodeDef.hasChild()) {
				nonAttribNodeDef.removeChild(compositorNodeDef);
			}
		} 
		else if (xsTerm instanceof XSWildcard) {
			// "any"
			
			// TODO How to handle Wildcards??
			// this may never be called, since we no longer call processParticle()
			// from processComplexTypeDefinition() when ContentType == CONTENT_TYPE_MIXED;
			throw new UnsupportedOperationException(
					"Does not support 'wildcards'; mpath: " + getLastValidMPath());
		}
		else {
			// should *never* occur; throw unchecked exception to 
			// indicate failure to properly understand XSD model 
			throw new UnsupportedOperationException(
					"Unknown term: " + xsTerm.getName() + "; mpath: " + getLastValidMPath());
		}
	}
	
	private void processModelGroup(
			XSModelGroup xsModelGroup, 
			CompositorNodeDef compositorNodeDef, 
			int colOffset) {
		
		// note: model groups do not have names; leave as null
		
		// iterate through particle children of model group
		XSObjectList xsObjectList = (XSObjectList)xsModelGroup.getParticles();
		ListIterator<?> listIterator = xsObjectList.listIterator();
		mpathBuilder.zoomIn();
		colOffset++;
		while (listIterator.hasNext()) {
			
			// process child particle
			XSParticle xsParticle = (XSParticle)listIterator.next();
			processParticle(xsParticle, compositorNodeDef, colOffset);
		}
		colOffset--;
		mpathBuilder.zoomOut();
	}
	
	private void processAttributes(
			XSComplexTypeDefinition xsComplexTypeDef, 
			ElementNodeDef elementNodeDef, 
			int colOffset) {
		
		XSObjectList xsObjectList = xsComplexTypeDef.getAttributeUses();
		if (xsObjectList != null) {
			for (int i = 0; i < xsObjectList.getLength(); i++) {
				// loop through attributes associated with this complex type
				XSAttributeUse attribUse = (XSAttributeUse)xsObjectList.item(i);
				XSAttributeDeclaration attribDecl = attribUse.getAttrDeclaration();
				// create an attrib node def for each and add to model def collection
				AttribNodeDef attribNodeDef = new AttribNodeDef(elementNodeDef, sharedState);
				attribNodeDef.setName(attribDecl.getName());
				attribNodeDef.setMPath(buildMPath(attribNodeDef));
				attribNodeDef.setColOffset(colOffset);
				attribNodeDef.setRowOffset(rowOffset++);
				attribNodeDef.setRequired(attribUse.getRequired());
				processDataType(attribUse, attribNodeDef);
				processAttribAnnotation(attribDecl, attribNodeDef);
				elementNodeDef.addAttribute(attribNodeDef);
			}
		}
	}
	
	private void processElementAnnotation(
			XSElementDeclaration xsElementDecl, 
			ElementNodeDef elementNodeDef) {
		
		processAnnotation(xsElementDecl.getAnnotation(), elementNodeDef);
	}

	private void processAttribAnnotation(
			XSAttributeDeclaration xsAttribDecl, 
			AttribNodeDef attribNodeDef) {
		
		processAnnotation(xsAttribDecl.getAnnotation(), attribNodeDef);
	}

	private void processAnnotation(
			XSAnnotation xsAnnotation, 
			NodeDef nodeDef) {
		
		for (SchemaExtractor extractor : schemaExtractors) {
			if (extractor instanceof SchemaAnnotationExtractor) {
				SchemaAnnotationExtractor e = (SchemaAnnotationExtractor)extractor;
				e.extractAnnotation(xsAnnotation, nodeDef);
			}
		}
	}
	
	private void processDataType(
			XSObject xsElementDeclOrAttribUse, 
			NodeDef elementOrAttribDef) {
		
		for (SchemaExtractor extractor : schemaExtractors) {
			if (extractor instanceof SchemaDataTypeExtractor) {
				SchemaDataTypeExtractor e = (SchemaDataTypeExtractor)extractor;
				e.extractDataType(xsElementDeclOrAttribUse, elementOrAttribDef);
			}
		}
	}
	
	private CompositorNodeDef createCompositorNode(
			NonAttribNodeDef parent, 
			XSModelGroup mg) {
		
		switch(mg.getCompositor()) {
			case XSModelGroup.COMPOSITOR_SEQUENCE: 
				return new CompositorNodeDef(parent, sharedState, CompositorType.SEQUENCE);
			case XSModelGroup.COMPOSITOR_CHOICE: 
				return new CompositorNodeDef(parent, sharedState, CompositorType.CHOICE);
			case XSModelGroup.COMPOSITOR_ALL: 
				return new CompositorNodeDef(parent, sharedState, CompositorType.ALL);
			// should *never* occur; throw unchecked exception to indicate 
			// failure to properly understand XSD model 
			default: throw new UnsupportedOperationException(
					"Invalid compositor: " + mg.getCompositor());
		}
	}
	
	private String buildMPath(NodeDef nodeDef) {
		String mpath = mpathBuilder.buildMPath(nodeDef);
		mpathIndex.addMPath(mpath, nodeDef);
		return mpath;
	}
	
	private String getLastValidMPath() {
		return mpathBuilder.getLastValidMPath();
	}
	
	private void addElementToElementPath(String name) {
		currentElementPath.append("/" + name);
	}

	private void removeElementFromElementPath() {
		currentElementPath.setLength(currentElementPath.lastIndexOf("/"));
	}

	public static class Builder {
		private final Path rootSchemaFile;
		private final MPathIndex<NodeDef> mpathIndex; 
		private final ModelDefSharedState sharedState;
		private final SchemaFilter schemaFilter;
		private final List<SchemaExtractor> schemaExtractors;

		private boolean failOnParserWarning;
		private boolean failOnParserNonFatalError;
		private MPathBuilder mpathBuilder;
		private XSModel xsModel;

		public Builder(
				Path rootSchemaFile, MPathIndex<NodeDef> mpathIndex, ModelDefSharedState sharedState, 
				SchemaFilter schemaFilter, List<SchemaExtractor> schemaExtractors) {
			
			this.rootSchemaFile = rootSchemaFile;
			this.mpathIndex = mpathIndex;
			this.sharedState = sharedState;
			this.schemaFilter = schemaFilter;
			this.schemaExtractors = schemaExtractors;
		}
		
		public Builder setFailOnParserWarning(boolean failOnParserWarning) {
			this.failOnParserWarning = failOnParserWarning;
			return this;
		}
		
		public Builder setFailOnParserNonFatalError(boolean failOnParserNonFatalError) {
			this.failOnParserNonFatalError = failOnParserNonFatalError;
			return this;
		}
		
		public ModelDefSchemaParser build() {
			mpathBuilder = new MPathBuilder();
			ModelDefXSModelBuilder xsModelBuilder = new ModelDefXSModelBuilder(
					failOnParserWarning, failOnParserNonFatalError);
			xsModel = xsModelBuilder.buildXSModelFromSchemas(rootSchemaFile);
			return new ModelDefSchemaParser(this);
		}
	}
}
