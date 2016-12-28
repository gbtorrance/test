package org.tdc.schemaparse.extractor;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.util.Config;
import org.tdc.modeldef.NodeDef;

/**
 * Implementation of {@link SchemaDataTypeExtractor}.
 * 
 * <p>This implementation supports the reading of basic data type information from schemas
 * and the subsequent setting of these data type values to variables associated with the 
 * corresponding {@link NodeDef}s.
 */
public class DefaultSchemaDataTypeExtractor implements SchemaDataTypeExtractor {
	private static final Logger log = LoggerFactory.getLogger(DefaultSchemaDataTypeExtractor.class);
	
	private final String variableName;
	private final boolean verbose;
	
	public DefaultSchemaDataTypeExtractor(String variableName, boolean verbose) {
		this.variableName = variableName;
		this.verbose = verbose;
	}

	@Override
	public void extractDataType(XSObject xsElementDeclOrAttribUse, NodeDef elementOrAttribDef) {
		String typeInfo = getDataType(xsElementDeclOrAttribUse);
		elementOrAttribDef.setVariable(variableName, typeInfo);
	}
	
	private String getDataType(XSObject xsElementDeclOrAttribUse) {
		String result;
		XSTypeDefinition type = getType(xsElementDeclOrAttribUse);
		if (verbose) {
			String typeInfo = getDataTypeVerbose(type);
			String valueConstraint = getValueConstraint(xsElementDeclOrAttribUse);
			result = formatResult(typeInfo, valueConstraint);
		}
		else {
			result = getDataTypeSimple(type); 
		}
		return result;
	}

	private String getDataTypeVerbose(XSTypeDefinition type) {
		String content;
		if (type instanceof XSSimpleTypeDefinition) {
			XSSimpleTypeDefinition simpleType = (XSSimpleTypeDefinition)type;			
			content = getSimpleContentVerbose(simpleType);
		}
		else {
			XSComplexTypeDefinition complexType = (XSComplexTypeDefinition)type;
			content = getComplexContentVerbose(complexType);
		}
		String typeName = getTypeNamesCascadeBases(type, 2);
		return formatResult(typeName, content);
	}
	
	private String getComplexContentVerbose(XSComplexTypeDefinition complexType) {
		String extension = complexType.getDerivationMethod() == 
				XSConstants.DERIVATION_EXTENSION ? "extension" : "";
		String content = complexType.getSimpleType() == null ? 
				"complexContent" :
				getSimpleContentVerbose((XSSimpleTypeDefinition)complexType.getSimpleType());
		return formatResult(extension, content);
	}
	
	private String getSimpleContentVerbose(XSSimpleTypeDefinition simpleType) {
		String union = getUnionCascade(simpleType);
		String list = getListCascade(simpleType);
		String length = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_LENGTH);
		String minLength = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MINLENGTH);
		String maxLength = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MAXLENGTH);
		String totalDigits = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_TOTALDIGITS);
		String fractionDigits = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_FRACTIONDIGITS);
		String minInclusive = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MININCLUSIVE);
		String minExclusive = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MINEXCLUSIVE);
		String maxInclusive = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MAXINCLUSIVE);
		String maxExclusive = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);
		String whitespace = getFacetPlusLabelCascade(simpleType, XSSimpleTypeDefinition.FACET_WHITESPACE);
		String pattern = getPatternPlusLabelCascade(simpleType);
		String enumeration = getEnumerationPlusLabelCascade(simpleType);
		return formatResult(
				union, list, 
				length, minLength, maxLength, 
				totalDigits, fractionDigits, 
				minInclusive, minExclusive, maxInclusive, maxExclusive, 
				whitespace, 
				pattern, 
				enumeration);
	}

	private String getDataTypeSimple(XSTypeDefinition type) {
		String result = "";
		String typeName = getTypeNamesCascadeBases(type, 1);
		if (type instanceof XSSimpleTypeDefinition) {
			XSSimpleTypeDefinition simpleType = (XSSimpleTypeDefinition)type;
			XSFacet len = getFacetCascade(simpleType, XSSimpleTypeDefinition.FACET_LENGTH);
			XSFacet minLen = getFacetCascade(simpleType, XSSimpleTypeDefinition.FACET_MINLENGTH);
			XSFacet maxLen = getFacetCascade(simpleType, XSSimpleTypeDefinition.FACET_MAXLENGTH);
			String size = "";
			if (len != null) {
				size = " (" + len.getIntFacetValue() + ")";
			}
			else if (minLen != null || maxLen != null) {
				size = " (" + 
						(minLen != null ? minLen.getIntFacetValue() : 0) + 
						"-" + 
						(maxLen != null ? maxLen.getIntFacetValue() : "n") + 
						")";
			}
			result = typeName + size;
		}
		else {
			result = typeName;
		}
		return result;
	}
	
	private XSTypeDefinition getType(XSObject xsElementDeclOrAttribUse) {
		XSTypeDefinition type;
		if (xsElementDeclOrAttribUse instanceof XSElementDeclaration) {
			XSElementDeclaration xsElementDecl = 
					(XSElementDeclaration)xsElementDeclOrAttribUse;
			type = xsElementDecl.getTypeDefinition();
		}
		else if (xsElementDeclOrAttribUse instanceof XSAttributeUse) {
			XSAttributeUse xsAttributeUse = 
					(XSAttributeUse)xsElementDeclOrAttribUse;
			type = xsAttributeUse.getAttrDeclaration().getTypeDefinition();
		}
		else {
			throw new IllegalStateException("XSObjects of type " + 
					xsElementDeclOrAttribUse.getClass().getName() + 
					" not supported; must be XSElementDeclaration or XSAttributeUse");
		}
		return type;
	}

	private String getTypeNamesCascadeBases(XSTypeDefinition type, int maxNames) {
		String result = "";
		int count = 0;
		XSTypeDefinition currentType = type;
		while (count < maxNames && currentType != null) {
			String typeName = getTypeName(currentType);
			if (typeName.length() != 0) {
				count++;
				if (result.length() != 0) {
					result += " / ";
				}
				result += typeName;
			}
			XSTypeDefinition baseType = currentType.getBaseType();
			currentType = ignoreBase(currentType, baseType) ? null : baseType;
		}
		return result;
	}
	
	private String getTypeName(XSTypeDefinition type) {
		return type.getName() == null ? "" : type.getName();
	}

	private boolean ignoreBase(XSTypeDefinition type, XSTypeDefinition baseType) {
		return baseType == null ||
				ignoreBaseTypeIfTypeOneOf(type, baseType) ||
				ignoreBaseTypeIfBaseTypeOneOf(type, baseType);
	}

	private boolean ignoreBaseTypeIfTypeOneOf(XSTypeDefinition type, XSTypeDefinition baseType) {
		// if the current type is a certain primitive type, then it is sufficiently
		// descriptive that it is not necessary to report the base type;
		// https://www.w3.org/TR/xmlschema-2/#built-in-datatypes
		
		// consider that this method will be executed along with ignoreBaseTypeIfBaseTypeOneOf()
		// to determine if a particular baseType should be ignored; it is, therefore, not necessary
		// to include types such as 'string' and 'decimal', as those are immediate children of
		// 'anySimpleType', which will be handled by the other method.
		
		String name = type.getName();
		String namespace = type.getNamespace();
		boolean ignore = false;
		if (name != null && namespace != null && namespace.equals("http://www.w3.org/2001/XMLSchema")) {
			ignore = name.equals("byte") ||
					name.equals("short") ||
					name.equals("int") ||
					name.equals("long") ||
					name.equals("integer") ||
					name.equals("negativeInteger") ||
					name.equals("nonPositiveInteger") ||
					name.equals("unsignedByte") ||
					name.equals("unsignedShort") ||
					name.equals("unsignedInt") ||
					name.equals("unsignedLong") ||
					name.equals("positiveInteger") ||
					name.equals("nonNegativeInteger");
		}
		return ignore;
	}
	
	private boolean ignoreBaseTypeIfBaseTypeOneOf(XSTypeDefinition type, XSTypeDefinition baseType) {
		// certain primitive base types can be safely ignored;
		// https://www.w3.org/TR/xmlschema-2/#built-in-datatypes
		
		String name = baseType.getName();
		String namespace = baseType.getNamespace();
		boolean ignore = false;
		if (name != null && namespace != null && namespace.equals("http://www.w3.org/2001/XMLSchema")) {
			ignore = name.equals("anySimpleType") ||
					name.equals("anyType"); 
		}
		return ignore;
	}

	private String getValueConstraint(XSObject xsElementDeclOrAttribUse) {
		String result;
		if (xsElementDeclOrAttribUse instanceof XSElementDeclaration) {
			XSElementDeclaration xsElementDecl = 
					(XSElementDeclaration)xsElementDeclOrAttribUse;
			result = getValueConstraint(
					xsElementDecl.getConstraintType(),
					xsElementDecl.getValueConstraintValue());
		}
		else if (xsElementDeclOrAttribUse instanceof XSAttributeUse) {
			XSAttributeUse xsAttribUse = 
					(XSAttributeUse)xsElementDeclOrAttribUse;
			result = getValueConstraint(
					xsAttribUse.getConstraintType(), 
					xsAttribUse.getValueConstraintValue());
			if (result.length() == 0) {
				result = getValueConstraint(
						xsAttribUse.getAttrDeclaration().getConstraintType(), 
						xsAttribUse.getAttrDeclaration().getValueConstraintValue());
			}
		}
		else {
			throw new IllegalStateException("XSObjects of type " + 
					xsElementDeclOrAttribUse.getClass().getName() + 
					" not supported; must be XSElementDeclaration or XSAttributeUse");
		}
		return result;
	}
	
	private String getValueConstraint(short vcType, XSValue vcValue) {
		String result = "";
		if (vcType != XSConstants.VC_NONE) {
			String vcTypeStr = valueConstraintToString(vcType);
			String value = vcValue.getNormalizedValue();
			result = vcTypeStr + ": '" + value + "'";
		}
		return result;
	}

	private String valueConstraintToString(short vcType) {
		switch (vcType) {
		case XSConstants.VC_NONE: return "none";
		case XSConstants.VC_FIXED: return "fixed";
		case XSConstants.VC_DEFAULT: return "default";
		default: throw new RuntimeException("Value Constraint " + vcType + " not supported");
		}
	}
	
	private String getUnionCascade(XSSimpleTypeDefinition simpleType) {
		String result = "";
		XSSimpleTypeDefinition currentSimpleType = simpleType;
		while (currentSimpleType != null) {
			if (currentSimpleType.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
				result = "union: ";
				for (int i = 0; i < currentSimpleType.getMemberTypes().getLength(); i++) {
					XSSimpleTypeDefinition member = (XSSimpleTypeDefinition)currentSimpleType.getMemberTypes().get(i);
					result += (i == 0 ? "{" : " + {") + getDataTypeVerbose(member) + "}";
				}
				break;
			}
			currentSimpleType = (XSSimpleTypeDefinition)currentSimpleType.getBaseType(); 
		}
		return result;
	}

	private String getListCascade(XSSimpleTypeDefinition simpleType) {
		String result = "";
		XSSimpleTypeDefinition currentSimpleType = simpleType;
		while (currentSimpleType != null) {
			if (currentSimpleType.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
				XSSimpleTypeDefinition item = (XSSimpleTypeDefinition)currentSimpleType.getItemType();
				result = "list: {" + getDataTypeVerbose(item) + "}";
				break;
			}
			currentSimpleType = (XSSimpleTypeDefinition)currentSimpleType.getBaseType(); 
		}
		return result;
	}
	
	private String getFacetPlusLabelCascade(XSSimpleTypeDefinition simpleType, short facetKind) {
		String result = "";
		XSFacet facet = getFacetCascade(simpleType, facetKind);
		if (facet != null) {
			result = facetKindToString(facetKind) + ": " + facet.getLexicalFacetValue();
		}
		return result;
	}
	
	private XSFacet getFacetCascade(XSSimpleTypeDefinition simpleType, short facetKind) {
		XSFacet facet = null;
		if (facetKind == XSSimpleTypeDefinition.FACET_ENUMERATION || facetKind == XSSimpleTypeDefinition.FACET_PATTERN) {
			throw new RuntimeException("Facet Kind " + facetKindToString(facetKind) + " not supported by this method");
		}
		XSSimpleTypeDefinition currentSimpleType = simpleType;
		while (currentSimpleType != null) {
			if (currentSimpleType.isDefinedFacet(facetKind)) {
				facet = (XSFacet)currentSimpleType.getFacet(facetKind);
				break;
			}
			currentSimpleType = (XSSimpleTypeDefinition)currentSimpleType.getBaseType();
		}
		return facet;
	}
	
	private String getEnumerationPlusLabelCascade(XSSimpleTypeDefinition simpleType) {
		String result = "";
		XSSimpleTypeDefinition currentSimpleType = simpleType;
		while (currentSimpleType != null) {
			if (currentSimpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_ENUMERATION)) {
				result = buildEnumerationString(currentSimpleType);
				break;
			}
			currentSimpleType = (XSSimpleTypeDefinition)currentSimpleType.getBaseType();
		}
		return result;
	}

	private String buildEnumerationString(XSSimpleTypeDefinition simpleType) {
		StringList sl = simpleType.getLexicalEnumeration();
		StringBuilder enumSB = new StringBuilder();
		for (int i = 0; i < sl.getLength(); i++) {
			if (i != 0) {
				enumSB.append(", ");
			}
			enumSB.append(sl.item(i));
		}
		if (enumSB.length() > 0) {
			enumSB.insert(0, ": (");
			enumSB.insert(0, facetKindToString(XSSimpleTypeDefinition.FACET_ENUMERATION));
			enumSB.append(")");
		}
		return enumSB.toString();
	}

	private String getPatternPlusLabelCascade(XSSimpleTypeDefinition simpleType) {
		String result = "";
		LinkedHashMap<String, String> map = null;
		XSSimpleTypeDefinition currentSimpleType = simpleType;
		while (currentSimpleType != null) {
			if (currentSimpleType.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				if (map == null) {
					map = new LinkedHashMap<>();
				}
				StringList patternList = simpleType.getLexicalPattern();
				for (int i = 0; i < patternList.getLength(); i++) {
					String pattern = patternList.item(i);
					map.putIfAbsent(pattern, pattern);
				}
			}
			currentSimpleType = (XSSimpleTypeDefinition)currentSimpleType.getBaseType();
		}
		if (map != null) {
			result = buildPatternString(map.values());
		}
		return result;
	}
	
	private String buildPatternString(Collection<String> patterns) {
		StringBuilder patternSB = new StringBuilder();
		for (String pattern : patterns) {
			if (patternSB.length() != 0) {
				patternSB.append(", ");
			}
			patternSB.append(pattern);
		}
		if (patternSB.length() > 0) {
			patternSB.insert(0, ": (");
			patternSB.insert(0, facetKindToString(XSSimpleTypeDefinition.FACET_PATTERN));
			patternSB.append(")");
		}
		return patternSB.toString();
	}

	private String facetKindToString(short facetKind) {
		switch (facetKind) {
		case XSSimpleTypeDefinition.FACET_NONE: return "none";
		case XSSimpleTypeDefinition.FACET_LENGTH: return "len";
		case XSSimpleTypeDefinition.FACET_MINLENGTH: return "minLen";
		case XSSimpleTypeDefinition.FACET_MAXLENGTH: return "maxLen";
		case XSSimpleTypeDefinition.FACET_TOTALDIGITS: return "totalDigits";
		case XSSimpleTypeDefinition.FACET_FRACTIONDIGITS: return "fractionDigits";
		case XSSimpleTypeDefinition.FACET_MININCLUSIVE: return "minIncl";
		case XSSimpleTypeDefinition.FACET_MINEXCLUSIVE: return "minExcl";
		case XSSimpleTypeDefinition.FACET_MAXINCLUSIVE: return "maxIncl";
		case XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE: return "maxExcl";
		case XSSimpleTypeDefinition.FACET_WHITESPACE: return "space";
		case XSSimpleTypeDefinition.FACET_ENUMERATION: return "enum";
		case XSSimpleTypeDefinition.FACET_PATTERN: return "pattern";
		default: throw new RuntimeException("Facet Kind " + facetKind + " not supported");
		}
	}

	private String formatResult(String...strings) {
		StringBuilder result = new StringBuilder();
		for (String string : strings) {
			if (string.length() != 0) {
				if (result.length() != 0) {
					result.append(", ");
				}
				result.append(string);
			}
		}
		return result.toString();
	}
	
	/**
	 * Static builder method to create an instance of this class based on XML configuration settings.
	 * 
	 * @param config Config class.
	 * @param key Configuration key pointing to location of information to read.
	 * @return Instance of this class.
	 */
	public static DefaultSchemaDataTypeExtractor build(Config config, String key) {
		String variableName = config.getString(key + ".VariableName", null, true);
		boolean verbose = config.getBoolean(key + ".Verbose", false, false);
		return new DefaultSchemaDataTypeExtractor(variableName, verbose);
	}
}
