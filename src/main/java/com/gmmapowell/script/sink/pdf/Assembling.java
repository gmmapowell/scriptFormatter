package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class Assembling {
	private final StyleCatalog styles;
	private PageStyle pageStyle;
	private final float lx;
	private final float width;
	private final List<Line> lines = new ArrayList<Line>();
	private Line currLine;
	private float before = 0;
	private float after;
	private Style style;
	private Outcome prevResult = null;
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
		List<StyledToken> prev = null;
		if (prevResult == null || !prevResult.forcedNewLine()) {
			if (currLine == null) { // first token in a new para
				String bsname = token.styles.get(0);
				Style baseStyle = styles.getOptional(bsname);
				if (baseStyle == null)
					throw new RuntimeException("no style found for " + bsname);
				style = baseStyle.apply(token.styles);
				if (style.getBeforeBlock() != null)
					this.before = Math.max(style.getBeforeBlock(), this.before);
				if (style.getAfterBlock() == null)
					this.after = 0;
				else
					this.after = style.getAfterBlock();
				if (style.getRequireAfter() == null)
					this.requireAfter = 0;
				else
					this.requireAfter = style.getRequireAfter();
				lm = style.getFirstMargin();
			} else if ((prevResult = currLine.accepts(token)) instanceof AcceptToken || prevResult instanceof PendingToken) {
				if (prevResult.replay() == null)
					return;
				prev = prevResult.replay();
//				System.out.println("need to replay " + prev);
			} else
				throw new CantHappenException("did not understand response: " + prevResult);
		} else {
			lm = ((AcceptToken)prevResult).getOverflow();
		}
		if (lm == null)
			lm = style.getLeftMargin();
		if (lm == null)
			lm = 0f;
		this.currLine = new Line(styles, pageStyle, lm, width);
		this.lines.add(currLine);
		if (prev != null) {
			for (StyledToken p : prev)
				prevResult = currLine.accepts(p);
		} else
			prevResult = currLine.accepts(token);
	}

	public float require() {
		float ret = this.before + this.requireAfter;
		for (Line l : lines)
			ret += l.require();
		return ret;
	}

	public float height() {
		float ret = this.before;
		for (Line l : lines)
			ret += l.height();
		return ret;
	}

	public void shove(PDPage meta, PDPageContentStream page, float ytop) throws IOException {
		for (Line l : lines) {
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
