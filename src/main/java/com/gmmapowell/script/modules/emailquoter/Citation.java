package com.gmmapowell.script.modules.emailquoter;

public class Citation {
	String file;
	int first, last;

	public static Citation parse(String file, String quote) {
		Citation c = new Citation();
		c.file = file;
		String[] lines = quote.split("-");
		c.first = Integer.parseInt(lines[0]);
		if (lines.length > 1) 
			c.last = Integer.parseInt(lines[1]);
		else
			c.last = c.first;
		return c;
	}
	
	@Override
	public String toString() {
		return file + ":" + first + (last > first ? "-" + last : "");
	}
}