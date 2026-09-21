package com.gmmapowell.script.modules.sink.github;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class IndexPageCreator implements DocumentOutline, Creator<IndexPageCreator, ScannerAtState>, LifecycleObserver {
	private final List<String> titles = new ArrayList<>();
	private final PrintWriter idx;
	private Place idxpostp;
	
	public IndexPageCreator(Place plc, Place idxprep, Place idxpostp) {
		this.idxpostp = idxpostp;
		Writer w = plc.writer();
		idx = new PrintWriter(w);
		idxprep.writeTo(idx);
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
		String title = titles.remove(0);
		System.out.println("pulling title " + title);
		idx.println("<li><a href='html/" + name + "'>" + title + "</a>");
	}

	public void processToken(StyledToken tok) {
//		System.out.println("ipg tok " + tok.it.getClass());
	}

	@Override
	public void allDone(GlobalState state) {
		idxpostp.writeTo(idx);
		idx.close();
	}
	
	

}
