package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class PaperStock implements Stock {
	private final Ream ream;
	private PageStyle firstLeft;
	private PageStyle firstRight;
	private PageStyle left;
	private PageStyle right;

	public PaperStock(Ream ream, PageStyle firstLeft, PageStyle firstRight, PageStyle left, PageStyle right) {
		this.ream = ream;
		this.firstLeft = firstLeft;
		this.firstRight = firstRight;
		this.left = left;
		this.right = right;
	}

	@Override
	public void newDocument(StyleCatalog styles) throws IOException {
		ream.newDocument(styles);
	}
	
	@Override
	public PageCompositor getPage(Map<String, String> current, boolean beginSection) throws IOException {
		PageCompositor ret = null;
		if (beginSection)
			ret = ream.newPage(firstLeft, firstRight);
		if (ret == null)
			ret = ream.newPage(left, right);
		if (ret == null)
			throw new CantHappenException("could not allocate page");
		return ret;
	}
	
	@Override
	public void close(Place output) throws IOException {
		ream.close(output);
	}
}
