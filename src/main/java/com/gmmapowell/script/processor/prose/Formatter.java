package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;

public interface Formatter {

	Block format(ElementFactory ef, String text, int exdent);

	boolean isBlockIndent(int firstline, int thisline);

}
