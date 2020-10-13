package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.StyleCatalog;

public class SimplePageCompositor implements PageCompositor {
	private final StyleCatalog styles;
	private final PDRectangle location;
	private final Map<String, Outlet> outlets = new TreeMap<>();
	private PDPageContentStream currentPage;

	public SimplePageCompositor(StyleCatalog styles, PDPageContentStream page, PDRectangle location) {
		this.styles = styles;
		currentPage = page;
		this.location = location;
	}

	@Override
	public void begin() throws IOException {
		// unhack this
		outlets.put("main", new Outlet(styles, currentPage, location));
	}

	@Override
	public Acceptance token(StyledToken token) throws IOException {
		Outlet outlet = outlets.get(token.flow);
		if (outlet == null) {
			throw new CantHappenException("there should be an outlet for " + token.flow);
		}
		return outlet.place(token);
	}

	@Override
	public boolean nextRegions() {
		boolean ret = true;
		for (Outlet o : outlets.values()) {
			ret &= o.nextRegion();
		}
		return ret;
	}
}
