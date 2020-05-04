package com.gmmapowell.script.styles.simple;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class CompoundStyle implements Style {
	private final StyleCatalog catalog;
	private final Style override;
	private final Style parent;

	public CompoundStyle(StyleCatalog catalog, Style override, Style parent) {
		this.catalog = catalog;
		this.override = override;
		this.parent = parent;
	}

	@Override
	public Style apply(String style) {
		if (style == null)
			return this;
		return new CompoundStyle(catalog, catalog.get(style), this);
	}

	@Override
	public Float getAfterBlock() {
		Float maybe = override.getAfterBlock();
		if (maybe != null)
			return maybe;
		return parent.getAfterBlock();
	}

	@Override
	public Float getBeforeBlock() {
		Float maybe = override.getBeforeBlock();
		if (maybe != null)
			return maybe;
		return parent.getBeforeBlock();
	}

	@Override
	public Float getFirstMargin() {
		Float maybe = override.getFirstMargin();
		if (maybe != null)
			return maybe;
		return parent.getFirstMargin();
	}

	@Override
	public PDFont getFont() {
		PDFont maybe = override.getFont();
		if (maybe != null)
			return maybe;
		return parent.getFont();
	}

	@Override
	public Float getFontSize() {
		Float maybe = override.getFontSize();
		if (maybe != null)
			return maybe;
		return parent.getFontSize();
	}

	@Override
	public Float getLineSpacing() {
		Float maybe = override.getLineSpacing();
		if (maybe != null)
			return maybe;
		return parent.getLineSpacing();
	}

	@Override
	public Justification getJustification() {
		Justification maybe = override.getJustification();
		if (maybe != null)
			return maybe;
		return parent.getJustification();
	}

	@Override
	public Boolean getUnderline() {
		Boolean maybe = override.getUnderline();
		if (maybe != null)
			return maybe;
		return parent.getUnderline();
	}

	@Override
	public Float getLeftMargin() {
		Float maybe = override.getLeftMargin();
		if (maybe != null)
			return maybe;
		return parent.getLeftMargin();
	}

	@Override
	public Float getRightMargin() {
		Float maybe = override.getRightMargin();
		if (maybe != null)
			return maybe;
		return parent.getRightMargin();
	}
}
