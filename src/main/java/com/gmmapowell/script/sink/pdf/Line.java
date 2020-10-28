package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.LinkFromRef;
import com.gmmapowell.script.flow.LinkFromTOC;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class Line {
	private final StyleCatalog styles;
	private final float width;
	private float xpos;
	private List<Item> contents = new ArrayList<>();
	private float minht = 0;
	private boolean isNew;
	private PageStyle pageStyle;
	private float requireBeyond = 0;
	private Justification just = Justification.LEFT;
	private List<StyledToken> pending = new ArrayList<>();

	public Line(StyleCatalog styles, PageStyle pageStyle, float margin, float width) {
		this.styles = styles;
		this.pageStyle = pageStyle;
		this.xpos = margin;
		this.width = width;
		this.isNew = true;
	}

	public Outcome accepts(StyledToken token) throws IOException {
		pending.add(token);
		SpanItem si = token.it;
		if (si instanceof ParaBreak)
			throw new CantHappenException("we should not see this");
		if (isNew && si instanceof BreakingSpace) // ignore leading spaces
			return new AcceptToken();
		String bsname = token.styles.get(0);
		Style baseStyle = styles.getOptional(bsname);
		if (baseStyle == null)
			throw new RuntimeException("no style found for " + bsname);
		if (isNew)
			just = baseStyle.getJustification();
		Style style = baseStyle.apply(token.styles);
		Justification cellJust = style.getJustification();
		Float cellWidth = style.getWidth();
		PDFont font = style.getFont();
		Float sz = style.getFontSize();
		if (sz == null)
			sz = 10f;
		if (style.getLineSpacing() != null)
			minht = Math.max(minht, style.getLineSpacing());
		if (si instanceof Break)
			requireBeyond = ((Break) si).require();
		BoundingBox bbox = si.bbox(font, sz);
		float wid = Math.max(bbox.getWidth(), orZero(style.getWidth()));
		if (xpos + wid > width) {
			if (isNew)
				System.out.println("line overflowed with " + token);
			else {
				System.out.println("pending=" + pending.size() + " contents = " + contents.size());
				if (pending.size() >= contents.size()) {
					System.out.println("overflowed " + width + " with no breaks");
					return new AcceptToken(style.getOverflowNewLine(), null);
				}
				for (int i=0;i<pending.size()-1;i++)
					contents.remove(contents.size()-1);
				return new AcceptToken(style.getOverflowNewLine(), pending);
			}
		}
		if (style.getBaselineAdjust() != null) {
			Float addy = style.getBaselineAdjust();
			bbox.setLowerLeftY(bbox.getLowerLeftY()+addy);
		}
		
		if (si instanceof TextSpanItem || si instanceof Break || si instanceof LinkOp || si instanceof LinkFromTOC || si instanceof LinkFromRef) {
			contents.add(new Item(pageStyle, style, adjust(xpos, cellJust, cellWidth, wid), bbox, font, sz, si));
		}
		isNew = false;
		if (cellWidth != null)
			xpos += cellWidth;
		else
			xpos += wid;
		
		if (style.getOverflowNewLine() != null && style.getWidth() != null && bbox.getWidth() > style.getWidth()) {
			pending.clear();
			return new AcceptToken(style.getOverflowNewLine(), pending);
		}
		
		if (si instanceof BreakingSpace || si instanceof ParaBreak) {
			pending.clear();
			return new AcceptToken();
		} else
			return new PendingToken();
	}

	private float adjust(float xpos, Justification cellJust, Float cellWidth, float itemWidth) {
		if (cellJust == null || cellWidth == null || itemWidth > cellWidth || cellJust == Justification.LEFT)
			return xpos;
		switch (cellJust) {
		case RIGHT:
			return xpos + cellWidth - itemWidth;
		case CENTER:
			return xpos + (cellWidth - itemWidth) / 2;
		default:
			throw new CantHappenException("invalid justification " + cellJust);
		}
	}

	private float orZero(Float x) {
		if (x == null)
			return 0;
		else
			return x;
	}

	public float baseline() {
		float ret = 0;
		for (Item i : contents)
			ret = Math.max(ret, i.height());
		return ret;
	}

	public float height() {
		return Math.max(baseline(), minht);
	}

	public float require() {
		return Math.max(height(), requireBeyond);
	}

	public void shove(PDPage meta, PDPageContentStream page, float x, float y) throws IOException {
		if (just != null) {
			switch (just) {
			case LEFT:
				break; // the default
			case RIGHT:
				x += width - xpos;
				break;
			case CENTER:
				x += (width - xpos)/2;
				break;
			default:
				throw new NotImplementedException();
			}
		}
		for (Item i : contents) {
			i.shove(meta, page, x, y);
		}
	}

}
