package com.gmmapowell.script.modules.sink.github;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class IndexPageCreator implements DocumentOutline, Creator<IndexPageCreator, ScannerAtState>, LifecycleObserver {
	public class TitleElement {
		private static final char SET = 's';
		private static final char INC = 'i';
		private NumberEncoder encoder;
		private char op;
		private int init, currNo;

		public TitleElement(NumberEncoder encoder, char op, int currNo) {
			this.encoder = encoder;
			this.op = op;
			this.init = currNo;
			this.currNo = currNo;
		}
	}

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

	private final List<TitleElement> elts = new ArrayList<>();
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
		parseFormats(tocFormat);
		titles.add(new Title(format(), title));
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

	private void update() {
		if (elts.isEmpty()) {
			return;
		}
		TitleElement last = elts.get(elts.size()-1);
		if (last.op == TitleElement.INC) {
			last.currNo++;
		} else if (last.op == TitleElement.SET) {
			last.currNo = last.init;
		} else
			throw new CantHappenException("subsequent op may not be " + last.op);
	}
	
	private void parseFormats(String tocFormat) {
		if (tocFormat == null) {
			update();
			return;
		}
		
		if (tocFormat.length() == 0) {
			elts.clear();
			return;
		}
		
		List<TitleElement> es = new ArrayList<>();
		String[] ses = tocFormat.split("\\.");
		for (String s: ses) {
			System.out.println("have " + s);
			es.add(parseOneFormat(s));
		}
		
		if (es.size() > elts.size()+1) {
			throw new CantHappenException("cannot extend by more than one");
		}
		
		while (elts.size() > es.size()) {
			elts.remove(es.size());
		}

		for (int i=0;i<elts.size()-1;i++) {
			// need to check it's all +
		}
		
		if (elts.size() > 0 && es.size() == elts.size()) {
			TitleElement last = es.get(es.size()-1);
			if (last.op == TitleElement.INC) {
				elts.get(elts.size()-1).op = last.op;
				elts.get(elts.size()-1).currNo++;
			} else if (last.op == TitleElement.SET) {
				elts.remove(elts.size()-1);
				elts.add(last);
			} else
				throw new CantHappenException("op may not be " + last.op);
		}

		if (es.size() > elts.size()) {
			TitleElement last = es.get(elts.size());
			if (last.currNo != 1) {
				throw new CantHappenException("initial value must be 1");
			}
			elts.add(last);
		}
	}

	private TitleElement parseOneFormat(String s) {
		switch (s) {
		case "1": {
			return new TitleElement(ArabicNumberEncoder.item, TitleElement.SET, 1);
		}
		case "+": {
			return new TitleElement(null, TitleElement.INC, 1);
		}
		case "A": {
			return new TitleElement(AlphaNumberEncoder.capital, TitleElement.SET, 1);
		}
		default: {
			throw new NotImplementedException();
		}
		}
	}

	private String format() {
		StringBuilder sb = new StringBuilder();
		for (TitleElement e : elts) {
			sb.append(e.encoder.render(e.currNo));
			sb.append(".");
		}
		return sb.toString();
	}

}
