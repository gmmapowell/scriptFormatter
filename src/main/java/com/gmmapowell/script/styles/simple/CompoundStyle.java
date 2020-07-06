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
			Style nested = catalog.getOptional(s);
			if (nested != null)
				base = new CompoundStyle(catalog, nested, base);
		}
		return base;
	}
	
	@Override
	public Style apply(List<String> style) {
		return combine(catalog, this, style);
	}

	@Override
	public Boolean beginNewPage() {
		Boolean maybe = override.beginNewPage();
		if (maybe != null)
			return maybe;
		return parent.beginNewPage();
	}

	@Override
	public Boolean isPreformatted() {
		Boolean maybe = override.isPreformatted();
		if (maybe != null)
			return maybe;
		return parent.isPreformatted();
	}

	@Override
	public Boolean showAtBottom() {
		Boolean maybe = override.showAtBottom();
		if (maybe != null)
			return maybe;
		return parent.showAtBottom();
	}

	@Override
	public Float getRequireAfter() {
		Float maybe = override.getRequireAfter();
		if (maybe != null)
			return maybe;
		return parent.getRequireAfter();
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
	public Float getWidth() {
		Float maybe = override.getWidth();
		if (maybe != null)
			return maybe;
		return parent.getWidth();
	}

	@Override
	public PDFont getFont() {
		return catalog.getFont(getFontName(), getItalic(), getBold());
	}

	@Override
	public String getFontName() {
		String maybe = override.getFontName();
		if (maybe != null)
			return maybe;
		return parent.getFontName();
	}

	@Override
	public Float getFontSize() {
		Float maybe = override.getFontSize();
		if (maybe != null)
			return maybe;
		
		Float p = parent.getFontSize();
		Float adj = override.getAdjustFontSize();
		if (adj != null)
			return p+adj;
		else
			return p;
	}

	@Override
	public Float getAdjustFontSize() {
		Float maybe = override.getAdjustFontSize();
		Float adj = override.getAdjustFontSize();
		if (maybe == null && adj == null)
			return null;
		else if (maybe != null)
			return maybe;
		else
			return adj;
	}

	@Override
	public Float getBaselineAdjust() {
		Float maybe = override.getBaselineAdjust();
		if (maybe != null)
			return maybe;
		return parent.getBaselineAdjust();
	}

	@Override
	public Boolean getBold() {
		Boolean bold = override.getBold();
		Boolean pb = parent.getBold();
		if (bold == null) {
			return pb;
		}
		return bold ^ pb; // XOR to toggle them
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
