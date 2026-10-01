package com.gmmapowell.script.modules.processors.doc;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.modules.doc.toc.TOCState;

public class OutlineNumberState implements OutlineNumbering {
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

	private final List<TitleElement> elts = new ArrayList<>();

	public OutlineNumberState(TOCState state) {
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
	
	public void parseFormats(String tocFormat) {
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
				if (last.encoder == null) {
					throw new CantHappenException("encoder is null for SET");
				}
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

	public String format(int level, String text, String style, String anchor) {
		StringBuilder sb = new StringBuilder();
		for (TitleElement e : elts) {
			sb.append(e.encoder.render(e.currNo));
			sb.append(".");
		}
		return sb.toString();
	}
}
