package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

public class Token {
	private final InputPosition location;

	public Token(InputPosition loc) {
		location = loc;
	}
	
	public InputPosition location() {
		return location;
	}

	public static Token from(ErrorReporter errors, Tokenizable line) {
		line.skipWS();
		if (!line.hasMore())
			return null;
		int mark = line.at();
		InputPosition loc = line.realinfo();
		char oq = line.nextChar();
		if (oq == '"' || oq == '\'')
			return readString(errors, line, loc, oq);
		
		while (line.hasMore() && !Character.isWhitespace(line.nextChar()))
			line.advance();
		String tok = line.fromMark(mark);
		switch (tok) {
		case "aspect":
		case "format":
		case "img":
		case "remove":
		case "slide":
		case "speaker":
		case "step":
		case "title":
			return new KeywordToken(loc, tok);
		case "<-":
			return new OpToken(loc, tok);
		default:
			break;
		}
		if (Character.isDigit(tok.charAt(0))) {
			try {
				float f = Float.parseFloat(tok);
				return new NumberToken(loc, f);
			} catch (Exception ex) {
				errors.message(line.locationAtText(mark), "invalid number");
				return null;
			}
		}
		if (Character.isLowerCase(tok.charAt(0))) {
			boolean isName = true;
			for (int i=0;i<tok.length();i++) {
				char ci = tok.charAt(i);
				if (!Character.isLetterOrDigit(ci) && ci != '-') {
					isName = false;
					break;
				}
			}
			if (isName)
				return new NameToken(loc, tok);
		}
		errors.message(line.locationAtText(mark), "cannot recognize this token");
		return null;
	}

	private static Token readString(ErrorReporter errors, Tokenizable line, InputPosition loc, char oq) {
		InputPosition start = line.realinfo();
		int actualLine = line.actualLine();
		
		StringBuilder ret = new StringBuilder();
		while (true) {
			line.advance();
			int mark = line.at();
			while (line.hasMore() && line.nextChar() != oq) {
				if (line.actualLine() > actualLine) {
					errors.message(start, "unterminated string");
					return null;
				}
				line.advance();
			}
			if (!line.hasMore()) {
				errors.message(start, "unterminated string");
				return null;
			}
			if (line.at() > mark)
				ret.append(line.fromMark(mark));
			line.advance();
			if (!line.hasMore() || line.nextChar() != oq)
				return new StringToken(loc, ret.toString());
			ret.append(oq);
		}
	}

	public static void assertEnd(ErrorReporter errors, Tokenizable line) {
		line.skipWS();
		if (line.hasMore()) {
			errors.message(line, "end of line expected");
		}
	}

}
