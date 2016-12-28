package org.tdc.spreadsheet;

import java.awt.Color;
import java.util.Objects;

import org.tdc.config.util.Config;
import org.tdc.util.Util;

/**
 * A {@link CellStyle} implementation.
 */
public class CellStyleImpl implements CellStyle {

	private static final String ITEM_STYLE_FONT_NAME = "FontName"; 
	private static final String ITEM_STYLE_FONT_HEIGHT = "FontHeight"; 
	private static final String ITEM_STYLE_COLOR_RGB = "ColorRGB";
	private static final String ITEM_STYLE_FILL_COLOR_RGB = "FillColorRGB";
	private static final String ITEM_STYLE_ITALIC = "Italic";
	private static final String ITEM_STYLE_BOLD = "Bold";
	private static final String ITEM_STYLE_SHRINK_TO_FIT = "ShrinkToFit";
	private static final String ITEM_STYLE_ALIGNMENT = "Alignment";
	private static final String ITEM_STYLE_FORMAT = "Format";
	
	private final String fontName;
	private final Double fontHeight;
	private final Color color;
	private final Color fillColor;
	private final Boolean italic;
	private final Boolean bold;
	private final Boolean shrinkToFit;
	private final CellAlignment alignment;
	private final String format;
	
	private CellStyleImpl(Builder builder) {
		this.fontName = builder.fontName;
		this.fontHeight = builder.fontHeight;
		this.color = builder.color;
		this.fillColor = builder.fillColor;
		this.italic = builder.italic;
		this.bold = builder.bold;
		this.shrinkToFit = builder.shrinkToFit;
		this.alignment = builder.alignment;
		this.format = builder.format;
	}
	
	@Override
	public String getFontName() {
		return fontName;
	}

	@Override
	public Double getFontHeight() {
		return fontHeight;
	}

	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public String getColorRGB() {
		return color == null ? null : getColorRed() + " " + getColorGreen() + " " + getColorBlue();
	}

	@Override
	public int getColorRed() {
		return color == null ? Util.UNDEFINED : color.getRed();
	}

	@Override
	public int getColorGreen() {
		return color == null ? Util.UNDEFINED : color.getGreen();
	}

	@Override
	public int getColorBlue() {
		return color == null ? Util.UNDEFINED : color.getBlue();
	}

	@Override
	public Color getFillColor() {
		return fillColor;
	}
	
	@Override
	public String getFillColorRGB() {
		return fillColor == null ? null : getFillColorRed() + " " + getFillColorGreen() + " " + getFillColorBlue();
	}

	@Override
	public int getFillColorRed() {
		return fillColor == null ? Util.UNDEFINED : fillColor.getRed();
	}

	@Override
	public int getFillColorGreen() {
		return fillColor == null ? Util.UNDEFINED : fillColor.getGreen();
	}

	@Override
	public int getFillColorBlue() {
		return fillColor == null ? Util.UNDEFINED : fillColor.getBlue();
	}

	@Override
	public Boolean getItalic() {
		return italic;
	}
	
	@Override
	public Boolean getBold() {
		return bold;
	}
	
	@Override
	public Boolean getShrinkToFit() {
		return shrinkToFit;
	}
	
	@Override
	public CellAlignment getAlignment() {
		return alignment;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(fontName).append(", ");
		sb.append(fontHeight).append(", ");
		sb.append("color: [").append(getColorRGB()).append("], ");
		sb.append("fillColor: [").append(getFillColorRGB()).append("], ");
		sb.append("italic: ").append(italic).append(", ");
		sb.append("bold: ").append(bold).append(", ");
		sb.append("shrinkToFit: ").append(shrinkToFit).append(", ");
		sb.append("alignment: ").append(alignment).append(", ");
		sb.append("format: ").append(format);
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fontName, fontHeight, color, fillColor, 
				italic, bold, shrinkToFit, alignment, format);
	}
	
	@Override 
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CellStyleImpl)) {
			return false;
		}
		CellStyleImpl csi = (CellStyleImpl)obj;
		return Objects.equals(fontName, csi.fontName) && 
				Objects.equals(fontHeight, csi.fontHeight) && 
				Objects.equals(color, csi.color) &&
				Objects.equals(fillColor, csi.fillColor) &&
				Objects.equals(italic, csi.italic) &&
				Objects.equals(bold, csi.bold) &&
				Objects.equals(shrinkToFit, csi.shrinkToFit) &&
				Objects.equals(alignment, csi.alignment) &&
				Objects.equals(format, csi.format);
	}
	
	public static class Builder {
		private String fontName;
		private Double fontHeight;
		private Color color;
		private Color fillColor;
		private Boolean italic;
		private Boolean bold;
		private Boolean shrinkToFit;
		public CellAlignment alignment;
		public String format;
		
		public Builder clear() {
			fontName = null;
			fontHeight = null;
			color = null;
			fillColor = null;
			italic = null;
			bold = null;
			shrinkToFit = null;
			alignment = null;
			format = null;
			return this;
		}
		
		public Builder setFrom(CellStyle style) {
			if (style == null) {
				clear();
			}
			else {
				setFontName(style.getFontName());
				setFontHeight(style.getFontHeight());
				setColor(style.getColor());
				setFillColor(style.getFillColor());
				setItalic(style.getItalic());
				setBold(style.getBold());
				setShrinkToFit(style.getShrinkToFit());
				setAlignment(style.getAlignment());
				setFormat(style.getFormat());
			}
			return this;
		}
		
		public Builder setFromIfProvided(CellStyle style) {
			if (style != null) {
				setFontNameIfProvided(style.getFontName());
				setFontHeightIfProvided(style.getFontHeight());
				setColorIfProvided(style.getColor());
				setFillColorIfProvided(style.getFillColor());
				setItalicIfProvided(style.getItalic());
				setBoldIfProvided(style.getBold());
				setShrinkToFitIfProvided(style.getShrinkToFit());
				setAlignmentIfProvided(style.getAlignment());
				setFormatIfProvided(style.getFormat());
			}
			return this;
		}
		
		public Builder setFromConfig(
				Config config, String key, CellStyle defaultValue, boolean required) {
			
			if (required && !config.hasNode(key)) {
				config.throwConfigItemNotFoundException(key);
			}
			// the following statement is necessary to make sure that, even if there are 
			// no child elements, that the parent key will be marked as having been
			// visited/recognized by the system
			config.hasNode(key);
			String alignmentStr = config.getString(key + "." + ITEM_STYLE_ALIGNMENT, null, false);
			CellAlignment alignment = alignmentStr == null ? 
					null : CellAlignment.getCellAlignmentByConfigType(alignmentStr);
			setFrom(defaultValue);
			setFontNameIfProvided(config.getString(key + "." + ITEM_STYLE_FONT_NAME, null, false));
			setFontHeightIfProvided(config.getDouble(key + "." + ITEM_STYLE_FONT_HEIGHT, null, false));
			setColorIfProvided(config.getString(key + "." + ITEM_STYLE_COLOR_RGB, null, false));
			setFillColorIfProvided(config.getString(key + "." + ITEM_STYLE_FILL_COLOR_RGB, null, false));
			setItalicIfProvided(config.getBoolean(key + "." + ITEM_STYLE_ITALIC, null, false));
			setBoldIfProvided(config.getBoolean(key + "." + ITEM_STYLE_BOLD, null, false));
			setShrinkToFitIfProvided(config.getBoolean(key + "." + ITEM_STYLE_SHRINK_TO_FIT, null, false));
			setAlignmentIfProvided(alignment);
			setFormatIfProvided(config.getString(key + "." + ITEM_STYLE_FORMAT, null, false));
			return this;
		}
		
		public Builder setFontName(String fontName) {
			this.fontName = fontName;
			return this;
		}

		public Builder setFontNameIfProvided(String fontName) {
			if (fontName != null) {
				setFontName(fontName);
			}
			return this;
		}

		public Builder setFontHeight(Double fontHeight) {
			this.fontHeight = fontHeight;
			return this;
		}

		public Builder setFontHeightIfProvided(Double fontHeight) {
			if (fontHeight != null) {
				setFontHeight(fontHeight);
			}
			return this;
		}

		public Builder setColor(Color color) {
			this.color = color;
			return this;
		}

		public Builder setColor(String colorRGB) {
			setColor(colorRGB == null ? null : 
				createColorFromRGBConfigString(colorRGB));
			return this;
		}

		public Builder setColorIfProvided(Color color) {
			if (color != null) {
				setColor(color);
			}
			return this;
		}

		public Builder setColorIfProvided(String colorRGB) {
			setColorIfProvided(colorRGB == null ? null : 
				createColorFromRGBConfigString(colorRGB));
			return this;
		}

		public Builder setFillColor(Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		public Builder setFillColor(String colorRGB) {
			setFillColor(colorRGB == null ? null : 
				createColorFromRGBConfigString(colorRGB));
			return this;
		}

		public Builder setFillColorIfProvided(Color fillColor) {
			if (fillColor != null) {
				setFillColor(fillColor);
			}
			return this;
		}
		
		public Builder setFillColorIfProvided(String colorRGB) {
			setFillColorIfProvided(colorRGB == null ? null : 
				createColorFromRGBConfigString(colorRGB));
			return this;
		}

		public Builder setItalic(Boolean italic) {
			this.italic = italic;
			return this;
		}

		public Builder setItalicIfProvided(Boolean italic) {
			if (italic != null) {
				setItalic(italic);
			}
			return this;
		}

		public Builder setBold(Boolean bold) {
			this.bold = bold;
			return this;
		}

		public Builder setBoldIfProvided(Boolean bold) {
			if (bold != null) {
				setBold(bold);
			}
			return this;
		}

		public Builder setShrinkToFit(Boolean shrinkToFit) {
			this.shrinkToFit = shrinkToFit;
			return this;
		}
		
		public Builder setShrinkToFitIfProvided(Boolean shrinkToFit) {
			if (shrinkToFit != null) {
				setShrinkToFit(shrinkToFit);
			}
			return this;
		}
		
		public Builder setAlignment(CellAlignment alignment) {
			this.alignment = alignment;
			return this;
		}
		
		public Builder setAlignmentIfProvided(CellAlignment alignment) {
			if (alignment != null) {
				setAlignment(alignment);
			}
			return this;
		}
		
		public Builder setFormat(String format) {
			this.format = format;
			return this;
		}
		
		public Builder setFormatIfProvided(String format) {
			if (format != null) {
				setFormat(format);
			}
			return this;
		}
		
		public CellStyle build() {
			return new CellStyleImpl(this);
		}
		
		private Color createColorFromRGBConfigString(String colorRGB) {
			Color color = null;
			String[] colorSplit = colorRGB.split("[ ]+");
			boolean validFormat = colorSplit.length == 3;
			if (validFormat) { 
				try {
					int red = Integer.parseUnsignedInt(colorSplit[0]);
					int green = Integer.parseUnsignedInt(colorSplit[1]);
					int blue = Integer.parseUnsignedInt(colorSplit[2]);
					color = new Color(red, green, blue);
				}
				catch (NumberFormatException ex) {
					validFormat = false;
				}
			}
			if (!validFormat) {
				throw new RuntimeException("Color config string must contain 3 " + 
						"non-negative integer values separated by spaces; '" + 
						colorRGB + "' is not valid");
			}
			return color;
		}
	}
}
