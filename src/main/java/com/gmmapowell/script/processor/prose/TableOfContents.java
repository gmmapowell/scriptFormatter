package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TableOfContents {
	// TODO: this is just to get me started.
	// Ultimately, I think we need to capture page numbers somehow and store all this as JSON
	// and then read the JSON and format it at the beginning of the document
	// Making sure we add links
	private final List<String> headings = new ArrayList<>();
	private final File tocfile;
	
	public TableOfContents(File tocfile) {
		this.tocfile = tocfile;
	}

	public void chapter(String title) {
		headings.add(title);
	}

	public void section(String title) {
		headings.add("  " + title);
	}

	public void subsection(String title) {
		headings.add("    " + title);
	}
	
	public void subsubsection(String title) {
		headings.add("      " + title);
	}
	
	public void write() throws FileNotFoundException {
		if (tocfile == null)
			return;
		try (PrintWriter pw = new PrintWriter(tocfile)) {
			for (String h : headings)
				pw.println(h);
		}
	}
}
