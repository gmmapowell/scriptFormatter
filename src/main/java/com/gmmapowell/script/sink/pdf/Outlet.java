package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.StyleCatalog;

public class Outlet {
	private final List<Region> regions = new ArrayList<>();
	private int currentRegion = 0;
	
	public Outlet(StyleCatalog styles, PDPageContentStream page, PDRectangle location) throws IOException {
		if (page == null)
			throw new CantHappenException("page must be non-null");
		// stop hacking this
		// numbers are in pts
		regions.add(new Region(styles, page, location.getLowerLeftX(), location.getLowerLeftY(), location.getUpperRightX(), location.getUpperRightY()));
//		regions.add(new Region(styles, page, 355, 35, 635, 540));
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
//		throw new CantHappenException("ran out of regions without returning noroom");
		// This is wrong ...
		return new Acceptance(Acceptability.NOROOM, token);
	}

	public boolean nextRegion() {
		return ++currentRegion < regions.size();
	}

	public Outlet borrowFrom() {
		return this;
	}
}
