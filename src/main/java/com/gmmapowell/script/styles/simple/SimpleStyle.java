package com.gmmapowell.script.styles.simple;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.simple.SimpleStyleCatalog.Builder;

public class SimpleStyle implements Style, Builder {
	private final StyleCatalog catalog;
	private Float afterBlock;
	private Float beforeBlock;
	private Justification just = null;
	private Float lineSpacing = null;
	private Boolean underline = null;

	public SimpleStyle(StyleCatalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public Style apply(String style) {
		if (style == null)
			return this;
		return new CompoundStyle(catalog, catalog.get(style), this);
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
	public PDFont getFont() {
		return PDType1Font.COURIER;
	}

	@Override
	public Float getFontSize() {
		return 12.0f;
	}

	@Override
	public Float getLineSpacing() {
		return lineSpacing;
	}

	@Override
	public Justification getJustification() {
		return just;
	}

	@Override
	public Boolean getUnderline() {
		return underline;
	}

	@Override
	public Float getLeftMargin() {
		return 72f;
	}

	@Override
	public Float getRightMargin() {
		return 72f;
	}

	@Override
	public Builder setAfterBlock(float f) {
		this.afterBlock = f;
		return this;
	}

	@Override
	public Builder setBeforeBlock(float f) {
		this.beforeBlock = f;
		return this;
	}

	@Override
	public Builder setJustification(Justification just) {
		this.just = just;
		return this;
	}

	@Override
	public Builder setLineSpacing(float f) {
		lineSpacing = f;
		return this;
	}

	@Override
	public Builder setUnderline(boolean b) {
		this.underline = b;
		return this;
	}

	@Override
	public Style build() {
		return this;
	}
}
