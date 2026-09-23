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
	public class Title {
		private String sno;
		private String title;

		public Title(String sno, String title) {
			this.sno = sno;
			this.title = title;
		}
		
		@Override
		public String toString() {
			return "["+sno+"]:" + title;
		}
	}

	private final List<Title> titles = new ArrayList<>();
	private final PrintWriter idx;
	private Place idxpostp;
	
	public IndexPageCreator(Place plc, Place idxprep, Place idxpostp) {
		this.idxpostp = idxpostp;
		Writer w = plc.writer();
		idx = new PrintWriter(w);
		idxprep.writeTo(idx);
	}

	@Override
	public void entry(int level, String tocFormat, String title, String style, String anchor) {
		System.out.println("entry " + title + " format = " + tocFormat);
		titles.add(new Title(tocFormat, title));
	}

	@Override
	public IndexPageCreator create(ScannerAtState quelle) {
		return this;
	}

	public void haveFile(String name) {
//		System.out.println("ipg file " + name);
		Title title = titles.remove(0);
//		System.out.println("pulling title " + title);
		if (title.title != null) {
			idx.println("<li><a href='html/" + name + "'>" + (title.sno.length() > 0 ? title.sno + " " : "") + title.title + "</a>");
		}
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
