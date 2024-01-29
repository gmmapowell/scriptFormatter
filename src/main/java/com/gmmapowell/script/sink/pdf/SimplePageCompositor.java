package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimplePageCompositor implements PageCompositor {
	private final Ream ream;
	private final StyleCatalog styles;
	private final PDPage meta;
	private final PDPageContentStream currentPage;
	private final String pageName;
	private final PDRectangle location;
	private final PageStyle pageStyle;
	private final Map<String, Outlet> outlets = new TreeMap<>();

	public SimplePageCompositor(Ream ream, StyleCatalog styles, PDPage meta, PDPageContentStream page, String pageName, PDRectangle location, PageStyle style) {
		this.ream = ream;
		this.styles = styles;
		this.meta = meta;
		if (page == null)
			throw new CantHappenException("page must be non-null");
		this.currentPage = page;
		this.pageName = pageName;
		this.location = location;
		this.pageStyle = style;
	}

	@Override
	public void begin() throws IOException {
		// TODO: pageStyle needs to become more complicated to handle all this properly...
		if (pageStyle.wantHeader()) {
			PDRectangle header = new PDRectangle(
					location.getLowerLeftX() + pageStyle.getLeftMargin(),							location.getUpperRightY() - 42f,
					location.getWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(),	20f
					);
			outlets.put("header", new Outlet(styles, pageStyle, meta, currentPage, header).bindCallback(new HFCallback() {
				public void populate(Region r) throws IOException {
					r.place(new StyledToken("header", 0, 0, new ArrayList<>(), Arrays.asList("text"), new TextSpanItem("Page " + ream.currentPageNo())));
					r.place(new StyledToken("header", 0, 0, new ArrayList<>(), Arrays.asList("text"), new ParaBreak()));
				}
			}));
		}
		if (pageStyle.wantFooter()) {
			PDRectangle footer = new PDRectangle(
					location.getLowerLeftX() + pageStyle.getLeftMargin(),							location.getLowerLeftY() + 60f,
					location.getWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(),	20f
					);
			outlets.put("footer", new Outlet(styles, pageStyle, meta, currentPage, footer).bindCallback(new HFCallback() {
				public void populate(Region r) throws IOException {
					r.place(new StyledToken("footer", 0, 0, new ArrayList<>(), Arrays.asList("pageno"), new TextSpanItem("Page " + ream.currentPageNo())));
					r.place(new StyledToken("footer", 0, 0, new ArrayList<>(), Arrays.asList("pageno"), new ParaBreak()));
				}
			}));
		}
		
		PDRectangle inside = new PDRectangle(
			location.getLowerLeftX() + pageStyle.getLeftMargin(),							location.getLowerLeftY() + pageStyle.getBottomMargin(),
			location.getWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(),	location.getHeight() - pageStyle.getBottomMargin() - pageStyle.getTopMargin()
		);
		outlets.put("main", new Outlet(styles, pageStyle, meta, currentPage, inside));
		outlets.put("footnotes", outlets.get("main").borrowFrom());
	}

	@Override
	public String currentPageName() {
		return pageName;
	}

	@Override
	public PDPage meta() {
		return meta;
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
