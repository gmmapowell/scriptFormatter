package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.styles.StyleCatalog;

public abstract class CommonReam implements Ream {
	protected PDDocument doc;
	protected StyleCatalog styles;
	protected int pageNo;

	@Override
	public void newDocument(StyleCatalog styles) throws IOException {
		this.styles = styles;
		doc = new PDDocument();
		styles.loadFonts(doc);
	}

	@Override
	public String currentPageNo() {
		return Integer.toString(pageNo);
	}

	@Override
	public void close(Place output) throws IOException {
		closeAllStreams();
		doc.save(GeoFSUtils.file(output));
		doc.close();
		this.doc = null;
	}

	protected abstract void closeAllStreams() throws IOException;
}
