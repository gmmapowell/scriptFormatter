package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.StyleCatalog;

public class SimplePageCompositor implements PageCompositor {
	private final StyleCatalog styles;
	private final PDDocument doc;
	private final PDRectangle paperSize;
	private final Map<String, Outlet> outlets = new TreeMap<>();
	private PDPageContentStream currentPage;

	public SimplePageCompositor(StyleCatalog styles, PDDocument doc) {
		this.styles = styles;
		this.doc = doc;
		this.paperSize = new PDRectangle(670, 575);
	}

	@Override
	public void begin() throws IOException {
		PDPage page = new PDPage(paperSize);
		doc.addPage(page);
		currentPage = new PDPageContentStream(doc, page);
		
		// unhack this
		outlets.put("main", new Outlet(styles, currentPage));
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

	@Override
	public void close() throws IOException {
		currentPage.close();
		currentPage = null;
	}
}
