package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class PaperStock implements Stock {
	private final Ream ream;

	public PaperStock(Ream ream, PageStyle firstLeft, PageStyle firstRight, PageStyle left, PageStyle right) {
		this.ream = ream;
	}

	@Override
	public void newDocument(StyleCatalog styles) throws IOException {
		ream.newDocument(styles);
	}
	
	@Override
	public PageCompositor getPage(Map<String, String> current) throws IOException {
		// TODO: figure out which style to apply
		// and feed unwanted pages
		return ream.newPage();
	}
	
	@Override
	public void close(File output) throws IOException {
		ream.close(output);
	}
}
