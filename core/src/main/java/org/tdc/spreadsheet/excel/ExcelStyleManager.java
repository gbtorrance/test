package org.tdc.spreadsheet.excel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdc.spreadsheet.CellAlignment;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;

/**
 * Helper class for keeping track of {@link CellStyle} objects
 * used by this tool and {@link XSSFCellStyle} objects
 * used when interacting directly with Apache POI objects.
 * 
 * <p>Also handles conversion between different types of styles.
 */
public class ExcelStyleManager {
	private final XSSFWorkbook workbook;
	private final Map<CellStyle, XSSFCellStyle> styleToXSSFStyleMap;
	private final Map<XSSFCellStyle, CellStyle> xssfStyleToStyleMap;
	
	public ExcelStyleManager(XSSFWorkbook workbook) {
		this.workbook = workbook;
		styleToXSSFStyleMap = new HashMap<>();
		xssfStyleToStyleMap = new HashMap<>();
	}

	public void setDefaultStyle(CellStyle style) {
		XSSFCellStyle xssfDefaultStyle = getDefaultXSSFStyle();
		selectiveOverrideXSSFStyle(xssfDefaultStyle, style);
	}
	
	public void setDefaultColumnStyle(XSSFSheet xssfSheet, int colNum, CellStyle style) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		XSSFCellStyle xssfStyle = lookupOrCreateXSSFStyle(style);
		xssfSheet.setDefaultColumnStyle(colNum - 1, xssfStyle);
	}
	
	public CellStyle getCellStyle(XSSFCell cell) {
		CellStyle style = null;
		if (cell != null) {
			XSSFCellStyle xssfStyle = cell.getCellStyle();
			style = lookupOrCreateStyle(xssfStyle);
		}
		return style;
	}
	
	public void setCellStyle(XSSFCell cell, CellStyle style) {
		XSSFCellStyle xssfStyle;
		if (style == null) {
			xssfStyle = (XSSFCellStyle)cell.getSheet().getColumnStyle(cell.getColumnIndex()); 
		}
		else {
			xssfStyle = lookupOrCreateXSSFStyle(style); 
		}
		cell.setCellStyle(xssfStyle);
	}
	
	private XSSFCellStyle getDefaultXSSFStyle() {
		return workbook.getCellStyleAt(0);
	}
	
	private XSSFCellStyle lookupOrCreateXSSFStyle(CellStyle style) {
		XSSFCellStyle xssfStyle = lookupXSSFStyle(style);
		if (xssfStyle == null) {
			xssfStyle = createXSSFStyle(style);
		}
		return xssfStyle;
	}

	private CellStyle lookupOrCreateStyle(XSSFCellStyle xssfStyle) {
		CellStyle style = lookupStyle(xssfStyle);
		if (style == null) {
			style = createStyle(xssfStyle);
		}
		return style;
	}

	private XSSFCellStyle lookupXSSFStyle(CellStyle style) {
		return styleToXSSFStyleMap.get(style);
	}

	private CellStyle lookupStyle(XSSFCellStyle xssfStyle) {
		return xssfStyleToStyleMap.get(xssfStyle);
	}

	private XSSFCellStyle createXSSFStyle(CellStyle style) {
		XSSFCellStyle xssfStyle = null;
		if (style != null) {
			xssfStyle = workbook.createCellStyle();
			selectiveOverrideXSSFStyle(xssfStyle, style);
			styleToXSSFStyleMap.put(style, xssfStyle);
		}
		return xssfStyle;
	}

	private CellStyle createStyle(XSSFCellStyle xssfStyle) {
		CellStyle style = null;
		if (xssfStyle != null) {
			style = createCellStyleFromXSSFStyle(xssfStyle);
			xssfStyleToStyleMap.put(xssfStyle, style);
		}
		return style;
	}

	private void selectiveOverrideXSSFStyle(XSSFCellStyle xssfStyle, CellStyle style) {
		XSSFFont xssfFont = workbook.createFont();
		if (style.getFontName() != null) {
			xssfFont.setFontName(style.getFontName());
		}
		if (style.getFontHeight() != null) {
			xssfFont.setFontHeight(style.getFontHeight());
		}
		if (style.getColor() != null) {
			XSSFColor newColor = new XSSFColor(style.getColor());
			xssfFont.setColor(newColor);
		}
		if (style.getItalic() != null) {
			xssfFont.setItalic(style.getItalic());
		}
		if (style.getBold() != null) {
			xssfFont.setBold(style.getBold());
		}
		xssfStyle.setFont(xssfFont);
		if (style.getShrinkToFit() != null) {
			xssfStyle.setShrinkToFit(style.getShrinkToFit());
		}
		if (style.getFillColor() != null) {
			XSSFColor newColor = new XSSFColor(style.getFillColor());
			xssfStyle.setFillForegroundColor(newColor);
			xssfStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		}
		if (style.getAlignment() != null) {
			xssfStyle.setAlignment(alignmentToXSSFAlignment(style.getAlignment()));
		}
		if (style.getFormat() != null) {
			XSSFDataFormat dataFormat = workbook.createDataFormat();
			xssfStyle.setDataFormat(dataFormat.getFormat(style.getFormat()));
		}
	}
	
	private CellStyle createCellStyleFromXSSFStyle(XSSFCellStyle xssfStyle) {
		String fontName = xssfStyle.getFont().getFontName();
		// convert font height from 1/20ths of a point to points
		double fontHeight = (1.0 * xssfStyle.getFont().getFontHeight()) / 20;
		Color color = xssfColorToAwtColor(xssfStyle.getFont().getXSSFColor());
		Color fillColor = xssfColorToAwtColor(xssfStyle.getFillForegroundColorColor());
		boolean italic = xssfStyle.getFont().getItalic();
		boolean bold = xssfStyle.getFont().getBold();
		boolean shrinkToFit = xssfStyle.getShrinkToFit();
		CellAlignment alignment = xssfAlignmentToAlignment(xssfStyle.getAlignmentEnum());
		String format = xssfStyle.getDataFormatString();
		CellStyle newStyle = new CellStyleImpl.Builder()
				.setFontName(fontName)
				.setFontHeight(fontHeight)
				.setColor(color)
				.setFillColor(fillColor)
				.setItalic(italic)
				.setBold(bold)
				.setShrinkToFit(shrinkToFit)
				.setAlignment(alignment)
				.setFormat(format)
				.build();
		return newStyle;
	}
	
	private HorizontalAlignment alignmentToXSSFAlignment(CellAlignment alignment) {
		HorizontalAlignment xssfAlignment = null;
		if (alignment != null) {
			switch(alignment) {
			case LEFT: xssfAlignment = HorizontalAlignment.LEFT; break;
			case CENTER: xssfAlignment = HorizontalAlignment.CENTER; break;
			case RIGHT: xssfAlignment = HorizontalAlignment.RIGHT; break;
			default: xssfAlignment = HorizontalAlignment.GENERAL;
			}
		}
		return xssfAlignment;
	}

	private CellAlignment xssfAlignmentToAlignment(HorizontalAlignment xssfAlignment) {
		CellAlignment alignment = null;
		if (xssfAlignment != null) {
			switch (xssfAlignment) {
			case LEFT: alignment = CellAlignment.LEFT; break;
			case CENTER: alignment = CellAlignment.CENTER; break;
			case RIGHT: alignment = CellAlignment.RIGHT; break;
			default: alignment = CellAlignment.GENERAL;
			}
		}
		return alignment;
	}

	private Color xssfColorToAwtColor(XSSFColor xssfColor) {
		Color color = null;
		if (xssfColor != null) {
			byte[] rgb = xssfColor.getTint() != 0.0 ? 
					xssfColor.getRGBWithTint() :  xssfColor.getRGB();
			color = new Color(
					Byte.toUnsignedInt(rgb[0]),
					Byte.toUnsignedInt(rgb[1]),
					Byte.toUnsignedInt(rgb[2]));
		}
		return color;
	}
}
