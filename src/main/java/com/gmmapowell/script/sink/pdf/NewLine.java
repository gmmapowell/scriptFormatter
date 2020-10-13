package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class NewLine {
	private final StyleCatalog styles;
	private final float width;
	private float xpos;
	private List<Item> contents = new ArrayList<>();
	private float minht = 0;

	public NewLine(StyleCatalog styles, float width) {
		this.styles = styles;
		this.width = width;
	}

	public boolean accepts(StyledToken token) throws IOException {
		SpanItem si = token.it;
		if (si instanceof ParaBreak)
			throw new CantHappenException("we should not see this");
		if (xpos == 0 && si instanceof BreakingSpace) // ignore leading spaces
			return true;
		String bsname = token.styles.get(0);
		Style baseStyle = styles.getOptional(bsname);
		if (baseStyle == null)
			throw new RuntimeException("no style found for " + bsname);
		Style style = baseStyle.apply(token.styles);
		
		PDFont font = style.getFont();
		Float sz = style.getFontSize();
		minht = Math.max(minht, style.getLineSpacing());
		BoundingBox bbox = si.bbox(font, sz);
		if (xpos + bbox.getWidth() > width)
			return false;
		
		if (si instanceof TextSpanItem)
			contents.add(new Item(xpos, bbox, font, sz, ((TextSpanItem)si).text));
		xpos += bbox.getWidth();
		
		return true;
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

	public void shove(PDPageContentStream page, float x, float y) throws IOException {
		for (Item i : contents) {
			i.shove(page, x, y);
		}
	}

}
