package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class Assembling {
	private final StyleCatalog styles;
	private PageStyle pageStyle;
	private final float lx;
	private final float width;
	private final List<NewLine> lines = new ArrayList<NewLine>();
	private NewLine curr;
	private float before = 0;
	private float after;
	private Style style;
	private AcceptToken prev = null;
	private float requireAfter;

	public Assembling(StyleCatalog styles, PageStyle pageStyle, float before, float lx, float rx) {
		this.styles = styles;
		this.pageStyle = pageStyle;
		this.before = before;
		this.lx = lx;
		this.width = rx - lx;
	}

	public void token(StyledToken token) throws IOException {
		Float lm = null;
		if (prev == null || !prev.forcedNewLine()) {
			if (curr == null) { // first token in a new para
				String bsname = token.styles.get(0);
				Style baseStyle = styles.getOptional(bsname);
				if (baseStyle == null)
					throw new RuntimeException("no style found for " + bsname);
				style = baseStyle.apply(token.styles);
				this.before = Math.max(style.getBeforeBlock(), this.before);
				this.after = style.getAfterBlock();
				this.requireAfter = style.getRequireAfter();
				lm = style.getFirstMargin();
			} else if ((prev = curr.accepts(token)) != null)
				return;
		} else {
			lm = prev.getOverflow();
			this.prev = null;
		}
		if (lm == null)
			lm = style.getLeftMargin();
		this.curr = new NewLine(styles, pageStyle, lm, width);
		this.lines.add(curr);
		if (curr.accepts(token) == null)
			throw new CantHappenException("new line refused to accept token");
	}

	public float require() {
		float ret = this.before + this.requireAfter;
		for (NewLine l : lines)
			ret += l.require();
		return ret;
	}

	public float height() {
		float ret = this.before;
		for (NewLine l : lines)
			ret += l.height();
		return ret;
	}

	public void shove(PDPage meta, PDPageContentStream page, float ytop) throws IOException {
		for (NewLine l : lines) {
			l.shove(meta, page, lx, ytop - before - l.baseline());
			ytop -= l.height();
		}
	}

	public float after() {
		return after;
	}

	public boolean wantYield() {
		return false;
	}
}
