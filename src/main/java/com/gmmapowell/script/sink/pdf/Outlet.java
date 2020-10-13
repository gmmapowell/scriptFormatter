package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.gmmapowell.script.styles.StyleCatalog;

public class Outlet {
	private final List<Region> regions = new ArrayList<>();
	private int currentRegion = 0;
	
	public Outlet(StyleCatalog styles, PDPageContentStream page, PDRectangle location) throws IOException {
		// stop hacking this
		// numbers are in pts
		regions.add(new Region(styles, page, location.getLowerLeftX(), location.getLowerLeftY(), location.getUpperRightX(), location.getUpperRightY()));
//		regions.add(new Region(styles, page, 355, 35, 635, 540));
	}
	
	public Acceptance place(StyledToken token) throws IOException {
		Acceptance ret = null;
		while (currentRegion < regions.size()) {
			ret = regions.get(currentRegion).place(token);
			if (ret.status == Acceptability.NOROOM) {
				if (nextRegion())
					return new Acceptance(Acceptability.BACKUP, ret.lastAccepted);
			}
			return ret;
		}
		return ret;
	}

	public boolean nextRegion() {
		return ++currentRegion < regions.size();
	}
}
