package com.gmmapowell.script.styles.simple;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimpleStyle implements Style, StyleBuilder {
	private final StyleCatalog catalog;
	private Boolean beginNewPage;
	private Boolean showAtBottom;
	private Boolean isPreformatted;
	private Float requireAfter;
	private Float afterBlock;
	private Float beforeBlock;
	private Float first = null;
	private Justification just = null;
	private Float left = null;
	private Float lineSpacing = null;
	private Float right = null;
	private String font = null;
	private Float fontSize = null;
	private Float adjustFontSize = null;
	private Float baselineAdjust = null;
	private Boolean bold = null;
	private Boolean italic = null;
	private Boolean underline = null;
	private Float width = null;
	private Float overflowNewLine = null;

	public SimpleStyle(StyleCatalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public Style apply(List<String> styles) {
		return CompoundStyle.combine(catalog, this, styles);
	}

	@Override
	public Boolean beginNewPage() {
		return beginNewPage;
	}
	
	@Override
	public Boolean showAtBottom() {
		return showAtBottom;
	}

	@Override
	public Boolean isPreformatted() {
		return isPreformatted;
	}

	@Override
	public Float getRequireAfter() {
		return requireAfter;
	}

	@Override
	public Float getAfterBlock() {
		return afterBlock;
	}
	
	@Override
	public Float getBeforeBlock() {
		return beforeBlock;
	}

	@Override
	public Float getFirstMargin() {
		return first;
	}
	
	@Override
	public PDFont getFont() {
		if (font == null)
			throw new RuntimeException("No font selected");
		return catalog.getFont(font, getItalic(), bold);
	}

	@Override
	public String getFontName() {
		return font;
	}
	
	@Override
	public Float getFontSize() {
		return fontSize;
	}

	@Override
	public Float getAdjustFontSize() {
		return adjustFontSize;
	}
	
	@Override
	public Float getBaselineAdjust() {
		return baselineAdjust;
	}
	
	@Override
	public Boolean getBold() {
		return this.bold;
	}
	
	@Override
	public Boolean getItalic() {
		return this.italic;
	}
	
	@Override
	public Justification getJustification() {
		return just;
	}
	
	@Override
	public Float getLeftMargin() {
		return left;
	}

	@Override
	public Float getLineSpacing() {
		return lineSpacing;
	}

	@Override
	public Float getRightMargin() {
		return right;
	}

	@Override
	public Boolean getUnderline() {
		return underline;
	}

	@Override
	public Float getWidth() {
		return width;
	}

	@Override
	public Float getOverflowNewLine() {
		return overflowNewLine;
	}
	
	@Override
	public StyleBuilder beginNewPage(boolean b) {
		this.beginNewPage = b;
		return this;
	}

	@Override
	public StyleBuilder setPreformatted(boolean b) {
		this.isPreformatted = b;
		return this;
	}

	@Override
	public StyleBuilder showAtBottom(boolean b) {
		this.showAtBottom = b;
		return this;
	}

	@Override
	public StyleBuilder setRequireAfter(float f) {
		this.requireAfter = f;
		return this;
	}

	@Override
	public StyleBuilder setAfterBlock(float f) {
		this.afterBlock = f;
		return this;
	}

	@Override
	public StyleBuilder setBeforeBlock(float f) {
		this.beforeBlock = f;
		return this;
	}

	@Override
	public StyleBuilder setFirstMargin(float f) {
		this.first = f;
		return this;
	}

	@Override
	public StyleBuilder setJustification(Justification just) {
		this.just = just;
		return this;
	}

	@Override
	public StyleBuilder setLeftMargin(float f) {
		this.left = f;
		return this;
	}

	@Override
	public StyleBuilder setLineSpacing(float f) {
		lineSpacing = f;
		return this;
	}

	@Override
	public StyleBuilder setRightMargin(float f) {
		this.right = f;
		return this;
	}

	@Override
	public StyleBuilder setFont(String font) {
		this.font = font;
		return this;
	}

	@Override
	public StyleBuilder setFontSize(Float f) {
		this.fontSize = f;
		return this;
	}

	@Override
	public StyleBuilder setAdjustFontSize(Float f) {
		this.adjustFontSize = f;
		return this;
	}

	@Override
	public StyleBuilder setBold(boolean b) {
		this.bold = b;
		return this;
	}

	@Override
	public StyleBuilder setItalic(boolean b) {
		this.italic = b;
		return this;
	}

	@Override
	public StyleBuilder setUnderline(boolean b) {
		this.underline = b;
		return this;
	}

	@Override
	public StyleBuilder setWidth(float f) {
		this.width = f;
		return this;
	}

	@Override
	public StyleBuilder setOverflowNewLine(float f) {
		this.overflowNewLine  = f;
		return this;
	}

	@Override
	public StyleBuilder setBaselineAdjust(float f) {
		this.baselineAdjust = f;
		return this;
	}

	@Override
	public Style build() {
		return this;
	}
}
