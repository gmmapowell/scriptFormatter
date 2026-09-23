package com.gmmapowell.script.modules.sink.github;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

public interface NumberEncoder {
	public String render(int n);
}

class ArabicNumberEncoder implements NumberEncoder {
	private ArabicNumberEncoder() {
	}
	
	@Override
	public String render(int n) {
		return Integer.toString(n);
	}
	
	public static final ArabicNumberEncoder item = new ArabicNumberEncoder();
}

class RomanNumberEncoder implements NumberEncoder {
	private static char CAPITAL = 0;
	private static char LOWER = 32;
	private char mode;
	
	private RomanNumberEncoder(char mode) {
		this.mode = mode;
	}
	
	@Override
	public String render(int n) {
		StringBuilder sb = new StringBuilder();
		if (n < 1 || n >= 3000) {
			throw new CantHappenException("invalid number for roman formatting: " + n);
		}
		while (n > 1000) {
			sb.append('M' + mode);
			n -= 1000;
		}
		throw new NotImplementedException();
//		return sb.toString();
	}
	
	public static final RomanNumberEncoder capital = new RomanNumberEncoder(CAPITAL);
	public static final RomanNumberEncoder lower = new RomanNumberEncoder(LOWER);
}

class AlphaNumberEncoder implements NumberEncoder {
	private static char CAPITAL = 'A'-1;
	private char mode;
	
	private AlphaNumberEncoder(char mode) {
		this.mode = mode;
	}
	
	@Override
	public String render(int n) {
		return new String(new char[] { (char) (mode + n) });
	}
	
	public static final AlphaNumberEncoder capital = new AlphaNumberEncoder(CAPITAL);
}