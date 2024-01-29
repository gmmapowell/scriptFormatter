package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class Outlet {
	protected final List<Region> regions = new ArrayList<>();
	protected int currentRegion = 0;
	private HFCallback callback;
	
	public Outlet(StyleCatalog styles, PageStyle pageStyle, PDPage meta, PDPageContentStream page, PDRectangle location) throws IOException {
		if (page == null)
			throw new CantHappenException("page must be non-null");
		regions.add(new Region(styles, pageStyle, meta, page, location.getLowerLeftX(), location.getLowerLeftY(), location.getUpperRightX(), location.getUpperRightY()));
	}
	
	protected Outlet() {
	}

	public Outlet bindCallback(HFCallback callback) {
		this.callback = callback;
		return this;
	}
	
	public Acceptance place(StyledToken token) throws IOException {
		while (currentRegion < regions.size()) {
			Acceptance ret = regions.get(currentRegion).place(token);
			if (ret.status == Acceptability.NOROOM) {
				if (nextRegion())
					return new Acceptance(Acceptability.BACKUP, ret.lastAccepted);
			}
			return ret;
		}
		throw new CantHappenException("ran out of regions without returning noroom");
	}

	public boolean nextRegion() throws IOException {
		if (callback != null)
			callback.populate(regions.get(currentRegion));
		return ++currentRegion < regions.size();
	}

	public Outlet borrowFrom() throws IOException {
		return new BorrowOutlet(this);
	}
}
