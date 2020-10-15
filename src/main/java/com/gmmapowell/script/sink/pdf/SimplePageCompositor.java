package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimplePageCompositor implements PageCompositor {
	private final Ream ream;
	private final StyleCatalog styles;
	private final PDPageContentStream currentPage;
	private final PDRectangle location;
	private final PageStyle pageStyle;
	private final Map<String, Outlet> outlets = new TreeMap<>();

	public SimplePageCompositor(Ream ream, StyleCatalog styles, PDPageContentStream page, PDRectangle location, PageStyle style) {
		this.ream = ream;
		if (page == null)
			throw new CantHappenException("page must be non-null");
		this.styles = styles;
		this.currentPage = page;
		this.location = location;
		this.pageStyle = style;
	}

	@Override
	public void begin() throws IOException {
		// TODO: pageStyle needs to become more complicated to handle all this properly...
		PDRectangle header = new PDRectangle(
			location.getLowerLeftX() + pageStyle.getLeftMargin(),							location.getUpperRightY() - 52f,
			location.getWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(),	20f
		);
		if (pageStyle.wantHeader()) {
			outlets.put("header", new Outlet(styles, pageStyle, currentPage, header).bindCallback(new HFCallback() {
				public void populate(Region r) throws IOException {
					r.place(new StyledToken("header", 0, 0, new ArrayList<>(), Arrays.asList("text"), new TextSpanItem("Page " + ream.currentPageNo())));
					r.place(new StyledToken("header", 0, 0, new ArrayList<>(), Arrays.asList("text"), new ParaBreak()));
				}
			}));
		}
		
		PDRectangle inside = new PDRectangle(
			location.getLowerLeftX() + pageStyle.getLeftMargin(),							location.getLowerLeftY() + pageStyle.getBottomMargin(),
			location.getWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(),	location.getHeight() - pageStyle.getBottomMargin() - pageStyle.getTopMargin()
		);
		outlets.put("main", new Outlet(styles, pageStyle, currentPage, inside));
		outlets.put("footnotes", outlets.get("main").borrowFrom());
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
	public boolean nextRegions() throws IOException {
		boolean ret = true;
		for (Outlet o : outlets.values()) {
			ret &= o.nextRegion();
		}
		return ret;
	}
}
