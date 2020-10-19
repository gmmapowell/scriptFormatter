package com.gmmapowell.script.processor.prose;

import org.apache.pdfbox.pdmodel.PDPage;
import org.codehaus.jettison.json.JSONObject;

public class JSONTOCEntry implements TOCEntry {
	private final TableOfContents toc;
	private final JSONObject entry;

	public JSONTOCEntry(TableOfContents toc, JSONObject entry) {
		this.toc = toc;
		this.entry = entry;
	}

	@Override
	public void recordPage(PDPage meta, String name) {
		toc.recordPage(entry, meta, name);
	}

}
