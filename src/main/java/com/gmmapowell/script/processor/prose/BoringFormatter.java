package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;

public class BoringFormatter implements Formatter {

	@Override
	public Block format(ElementFactory ef, String text, int exdent) {
		SpanBlock ret = ef.block("preformatted");
		ret.addSpan(ef.span(null, text.replace("\t", "    ")));
		return ret;
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
