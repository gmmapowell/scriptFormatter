package com.gmmapowell.script.styles.simple;

import java.util.List;

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

	public static Style combine(StyleCatalog catalog, Style base, List<String> style) {
		if (style == null)
			return base;
		for (String s : style) {
			base = new CompoundStyle(catalog, catalog.get(s), base);
		}
		return base;
	}
	
	@Override
	public Style apply(List<String> style) {
		return combine(catalog, this, style);
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
		return getFontInternal(this);
	}

	@Override
	public PDFont getFontInternal(Style style) {
		PDFont maybe = override.getFontInternal(this);
		if (maybe != null)
			return maybe;
		return parent.getFontInternal(this);
	}

	@Override
	public Float getFontSize() {
		Float maybe = override.getFontSize();
		if (maybe != null)
			return maybe;
		return parent.getFontSize();
	}

	@Override
	public Boolean getItalic() {
		Boolean italic = override.getItalic();
		Boolean pi = parent.getItalic();
		if (italic == null) {
			return pi;
		}
		return italic ^ pi; // XOR to toggle them
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
