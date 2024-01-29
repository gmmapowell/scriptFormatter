package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;

import com.gmmapowell.script.flow.StyledToken;

public interface PageCompositor {
	void begin() throws IOException;
	String currentPageName();
	PDPage meta();

	Acceptance token(StyledToken next) throws IOException;
	boolean nextRegions() throws IOException;

}
