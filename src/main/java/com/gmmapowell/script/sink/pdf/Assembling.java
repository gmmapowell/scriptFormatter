package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.StyleCatalog;

public class Assembling {
	private final StyleCatalog styles;
	private final int lx;
	private final int width;
	private final List<NewLine> lines = new ArrayList<NewLine>();
	private NewLine curr;

	public Assembling(StyleCatalog styles, int lx, int rx) {
		this.styles = styles;
		this.lx = lx;
		this.width = rx - lx;
		this.curr = new NewLine(styles, width);
		this.lines.add(curr);
	}

	public void token(StyledToken token) throws IOException {
		if (curr.accepts(token))
			return;
		this.curr = new NewLine(styles, width);
		this.lines.add(curr);
		if (!curr.accepts(token))
			throw new CantHappenException("new line refused to accept token");
	}

	// TODO: needs to deal with space above and below
	// TODO: needs to know about prev and next paras (if any)
	// (actually, I think needs to know about prev and leave memo for next)
	public float height() {
		float ret = 0;
		for (NewLine l : lines)
			ret += l.height();
		return ret;
	}

	public void shove(PDPageContentStream page, float ytop) throws IOException {
		for (NewLine l : lines) {
			l.shove(page, lx, ytop - l.height()); // should be baseline from top
			ytop -= l.height();
		}
	}

}
