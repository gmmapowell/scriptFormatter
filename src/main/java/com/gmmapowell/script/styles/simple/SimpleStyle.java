package com.gmmapowell.script.styles.simple;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimpleStyle implements Style, StyleBuilder {
	private final StyleCatalog catalog;
	private Float afterBlock;
	private Float beforeBlock;
	private Float first = null;
	private Justification just = null;
	private Float left = null;
	private Float lineSpacing = null;
	private Float right = null;
	private Boolean underline = null;
	private Boolean italic = null;
	private String font;

	public SimpleStyle(StyleCatalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public Style apply(List<String> styles) {
		return CompoundStyle.combine(catalog, this, styles);
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
		return catalog.getFont(font, getItalic());
	}

	@Override
	public String getFontName() {
		return font;
	}
	
	@Override
	public Float getFontSize() {
		return 12.0f;
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
	public Style build() {
		return this;
	}
}
