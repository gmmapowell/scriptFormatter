package com.gmmapowell.script.sink.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;

public class TokenStack {
	public class Closer {
		public Closer(String style, String closer) {
			super();
			this.style = style;
			this.closer = closer;
		}

		String style;
		String closer;
	}

	private PrintWriter writer;
	private boolean needParaStart;
	private List<String> nextStyles;
	private List<String> currStyles;
	private List<Closer> closers;

	public TokenStack(StringWriter sw) {
		writer = new PrintWriter(sw);
		needParaStart = true;
	}

	public void write(Place from) {
		if (from == null) {
			return;
		}
		from.writeTo(writer);
	}

	public void styles(List<String> styles) {
		this.nextStyles = styles;
	}

	public void print(String text) {
		List<String> moveTo = new ArrayList<>(nextStyles);
		if (needParaStart) {
			beginPara(moveTo.remove(0));
		}
		if (currStyles != null) {
			List<String> andRemove = new ArrayList<>(currStyles);
			moveTo.removeAll(currStyles);
			andRemove.removeAll(nextStyles);
			while (!andRemove.isEmpty()) {
				String remove = andRemove.remove(andRemove.size()-1);
				Closer closer = closers.remove(closers.size()-1);
				if (!closer.style.equals(remove)) {
					throw new CantHappenException("we need to remove style " + remove + " but we have " + closer.style + " at the top of the stack");
				}
				writer.print(closer.closer);
			}
		}
		if (!moveTo.isEmpty() && moveTo.get(0).equals(nextStyles.get(0))) {
			throw new CantHappenException("we have a para start token at front of moveto");
		}
		while (!moveTo.isEmpty()) {
			String needAlso = moveTo.remove(0);
			switch (needAlso) {
			case "locate-place":
			case "locate-date":
			case "footnote-number":
				writer.print("<span class='" + needAlso + "'>");
				closers.add(new Closer(needAlso, "</span>"));
				break;
			case "italic":
				writer.print("<i>");
				closers.add(new Closer(needAlso, "</i>"));
				break;
			case "bold":
				writer.print("<b>");
				closers.add(new Closer(needAlso, "</b>"));
				break;
			default:
				throw new NotImplementedException("need " + needAlso);
//				break;
			}
		}
		writer.print(text);
		this.currStyles = nextStyles;
	}

	private void beginPara(String ofType) {
		this.closers = new ArrayList<>();
		switch (ofType) {
		case "chapter-title": {
			writer.print("<h1>");
			closers.add(new Closer(ofType, "</h1>"));
			break;
		}
		case "footnote": {
			writer.write("<aside class='" + ofType + "'>");
			closers.add(new Closer(ofType, "</aside>"));
			break;
		}
		default: {
			writer.write("<p class='" + ofType + "'>");
			closers.add(new Closer(ofType, "</p>"));
			break;
		}
		}
		needParaStart = false;
	}

	public void closePara() {
		if (needParaStart) {
			return;
		}
		while (!closers.isEmpty()) {
			Closer removeStyle = closers.remove(closers.size() - 1);
			writer.print(removeStyle.closer);
		}
		writer.println();
		needParaStart = true;
		this.currStyles = null;
		this.closers = null;
	}

	public void popAll() {
		if (closers != null && !closers.isEmpty()) {
			throw new CantHappenException("closers were not all gone");
		}
	}

	public void flush() {
		writer.flush();
	}

}
