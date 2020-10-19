package com.gmmapowell.script.processor.prose;

import org.apache.pdfbox.pdmodel.PDPage;

public interface TOCEntry {

	void recordPage(PDPage meta, String name);

}
