package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.SyncAfterFlow;
import com.gmmapowell.script.flow.YieldToFlow;
import com.gmmapowell.script.styles.StyleCatalog;

public class Region {
	private final StyleCatalog styles;
	private final PDPageContentStream page;
	private final float lx;
	private final float ly;
	private final float rx;
	private final float uy;

	private float ytop;
	private Assembling curr;
	private StyledToken lastAccepted;
	private boolean showBorder = false;
	private String wantYield = null;

	public Region(StyleCatalog styles, PDPageContentStream page, float lx, float ly, float rx, float uy) throws IOException {
		this.styles = styles;
		this.page = page;
		this.lx = lx;
		this.ly = ly;
		this.rx = rx;
		this.uy = uy;
		this.ytop = uy;
		this.curr = new Assembling(styles, 0, lx, rx);
		if (showBorder) {
			drawBorder();
		}
	}

	private void drawBorder() throws IOException {
		page.setLineWidth(0.3f);
		page.moveTo(lx, ly);
		page.lineTo(lx, uy);
		page.lineTo(rx, uy);
		page.lineTo(rx, ly);
		page.closeAndStroke();
	}

	public Acceptance place(StyledToken token) throws IOException {
		if (token.it instanceof ParaBreak) {
			if (ytop - curr.height() > ly) {
				curr.shove(page, ytop);
				ytop -= curr.height();
				curr = new Assembling(styles, curr.after(), lx, rx);
				if (wantYield != null) {
					System.out.println("    ---- yielded back to: " + (lastAccepted == null ? "beginning" : lastAccepted.location()));
					Acceptance ret = new Acceptance(Acceptability.SUSPEND, lastAccepted).enableFlow(wantYield);
					wantYield = null;
					return ret;
				} else {
					lastAccepted = token;
					System.out.println("    ---- accepted: " + lastAccepted.location());
					return new Acceptance(Acceptability.PROCESSED, token);
				}
			} else {
				if (lastAccepted == null)
					throw new CantHappenException("no tokens were accepted onto the page at all");
				System.out.println("    ---- last accepted was: " + lastAccepted.location());
				return new Acceptance(Acceptability.NOROOM, lastAccepted);
			}
		} else if (token.it instanceof YieldToFlow) {
			return new Acceptance(Acceptability.SUSPEND, token).enableFlow(((YieldToFlow)token.it).yieldTo());
		} else {
			if (token.it instanceof SyncAfterFlow) {
				wantYield = ((SyncAfterFlow)token.it).yieldTo();
			} else {
				curr.token(token);
			}
			return new Acceptance(Acceptability.PENDING, lastAccepted);
		}
	}

}
