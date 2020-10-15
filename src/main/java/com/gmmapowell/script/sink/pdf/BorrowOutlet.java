package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

public class BorrowOutlet extends Outlet {

	public BorrowOutlet(Outlet outlet) throws IOException {
		for (Region r : outlet.regions)
			this.regions.add(r.borrowFrom());
	}

	@Override
	public boolean nextRegion() throws IOException {
		if (currentRegion < this.regions.size())
			((BorrowRegion)this.regions.get(currentRegion)).flush();
		return super.nextRegion();
	}
}
