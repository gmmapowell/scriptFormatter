package com.gmmapowell.script.modules.doc.emailquoter;

public class Citation {
	public static class LinePhrase {
		private int line;
		private String phrase;

		public LinePhrase(int line, String phrase) {
			this.line = line;
			this.phrase = phrase;
		}

	}

	String file;
	int first, last;
	String fromPhrase, toPhrase;

	public static Citation parse(String file, String quote) {
		Citation c = new Citation();
		c.file = file;
		int dash = quote.indexOf('-');
		if (dash != -1) {
			LinePhrase left = parseCol(quote.substring(0, dash));
			LinePhrase right = parseCol(quote.substring(dash+1));
			c.first = left.line;
			c.last = right.line;
			c.fromPhrase = left.phrase;
			c.toPhrase = right.phrase;
		} else { 
			LinePhrase both = parseCol(quote);
			c.first = c.last = both.line;
			c.fromPhrase = both.phrase;
			c.toPhrase = null;
		}
		return c;
	}
	
	private static LinePhrase parseCol(String s) {
		int col = s.indexOf(":");
		if (col == -1)
			return new LinePhrase(Integer.parseInt(s), null);
		else
			return new LinePhrase(Integer.parseInt(s.substring(0, col)), s.substring(col+1));
	}

	public String getFile() {
		return file;
	}

	public int getFirst() {
		return first;
	}

	public int getLast() {
		return last;
	}

	public String getFromPhrase() {
		return fromPhrase;
	}

	public String getToPhrase() {
		return toPhrase;
	}

	@Override
	public String toString() {
		return file + ":" + first + (last > first ? "-" + last : "");
	}
}