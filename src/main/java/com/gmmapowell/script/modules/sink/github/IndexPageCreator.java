package com.gmmapowell.script.modules.sink.github;

import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class IndexPageCreator implements DocumentOutline, Creator<IndexPageCreator, ScannerAtState> {
	private final List<String> titles = new ArrayList<>();
	
	public IndexPageCreator(Place plc) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void entry(int level, String title, String style, String anchor) {
		System.out.println("entry " + title);
		titles.add(title);
	}

	@Override
	public IndexPageCreator create(ScannerAtState quelle) {
		System.out.println("creating for " + quelle);
		return this;
	}

	public void haveFile(String name) {
		System.out.println("ipg file " + name);
		System.out.println("pulling title " + titles.remove(0));
		if (titles.isEmpty()) {
			System.out.println("empty");
		}
	}

	public void processToken(StyledToken tok) {
//		System.out.println("ipg tok " + tok.it.getClass());
	}

}
