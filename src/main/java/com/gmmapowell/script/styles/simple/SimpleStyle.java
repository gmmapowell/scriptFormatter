package com.gmmapowell.script.styles.simple;

import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimpleStyle implements Style {
	private final StyleCatalog catalog;

	public SimpleStyle(StyleCatalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public Style apply(String style) {
		if (style == null)
			return this;
		return new CompoundStyle(catalog, catalog.get(style), this);
	}

}
