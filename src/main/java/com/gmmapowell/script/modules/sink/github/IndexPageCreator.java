package com.gmmapowell.script.modules.sink.github;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class IndexPageCreator implements DocumentOutline, Creator<IndexPageCreator, ScannerAtState> {

	public IndexPageCreator(Place plc) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void entry(int level, String title, String style, String anchor) {
		System.out.println("entry " + title);
	}

	@Override
	public IndexPageCreator create(ScannerAtState quelle) {
		return this;
	}

	public void haveFile(String name) {
		System.out.println("ipg " + name);
	}

}
