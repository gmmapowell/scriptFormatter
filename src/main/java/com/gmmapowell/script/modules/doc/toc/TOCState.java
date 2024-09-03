package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.processor.prose.TableOfContents;

public class TOCState {
	private TableOfContents toc;
	public String chapterStyle;
	public int chapter = 1;
	public int section;
	public boolean commentary;
	public boolean wantSectionNumbering;

	// TODO: reset should probably be called through some kind of EP mechanism
	public void reset() {
		section = 0;
		commentary = false;
	}

	public void resetNumbering() {
		chapter = 1;
		section = 0;
		commentary = false;
	}

	public void configureOnCreate() {
		if (toc == null) {
			toc = new TableOfContents(null, null);
		}
	}
	
	public TableOfContents toc() {
		return toc;
	}
}
