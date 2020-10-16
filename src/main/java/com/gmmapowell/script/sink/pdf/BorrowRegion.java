package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;

public class BorrowRegion extends Region {
	private final Region parent;
	private final List<Assembling> saved = new ArrayList<>();

	public BorrowRegion(Region region) throws IOException {
		super(region.styles, region.pageStyle, region.meta, region.page, region.lx, region.ly, region.rx, region.uy);
		this.parent = region;
		this.ytop = ly;
	}

	@Override
	protected boolean currFits() {
		float req = curr.require();
		if (saved.isEmpty())
			req += 10;
		return parent.ytop - req > parent.ly;
	}

	@Override
	protected void storeCurr() throws IOException {
		if (saved.isEmpty())
			parent.ly += 12;
		saved.add(curr);
		parent.ly += curr.height();
	}

	@Override
	protected Acceptance checkAcceptedSomething() {
		if (lastAccepted == null && parent.lastAccepted == null)
			throw new CantHappenException("no tokens were accepted onto the page at all");
//		System.out.println("    ---- last accepted was: " + (lastAccepted == null ? " none here" : lastAccepted.location()));
		parent.rejected = true;
		return new Acceptance(Acceptability.NOROOM, lastAccepted);
	}

	public void flush() throws IOException {
		if (saved.isEmpty())
			return;
		
		// figure out where to start
		ytop += 7;
		for (Assembling a : saved) {
			ytop += a.height();
		}
		
		page.setLineWidth(0.5f);
		page.moveTo(lx, ytop);
		page.lineTo(rx, ytop);
		page.closeAndStroke();

		ytop -= 7;
		for (Assembling a : saved) {
			a.shove(meta, page, ytop);
			ytop -= a.height();
		}
	}

}
