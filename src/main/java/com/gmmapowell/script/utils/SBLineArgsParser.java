package com.gmmapowell.script.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gmmapowell.script.processor.ParsingException;

public class SBLineArgsParser<T extends FileWithLocation> implements LineArgsParser {
	private final T state;
	private final StringBuilder args;

	public SBLineArgsParser(T state, String args) {
		this.state = state;
		this.args = new StringBuilder(args);
	}

	public Command readCommand() {
		if (args.length() == 0 || args.toString().startsWith("#"))
			return null;
		int nesting = 0;
		while (nesting < args.length() && Character.isWhitespace(args.charAt(0))) {
			args.delete(0, 1);
			nesting++;
		}
		if (args.length() == 0 || args.toString().startsWith("#"))
			return null;
		int firstSpace = 0;
		while (firstSpace < args.length() && !Character.isWhitespace(args.charAt(firstSpace)))
			firstSpace++;
		NestedCommand ret = new NestedCommand(nesting, args.substring(0, firstSpace), this);
		args.delete(0, firstSpace);
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0))) {
			args.delete(0, 1);
		}
		return ret;
	}
	
	@Override
	public boolean hasMore() {
		if (args == null || args.length() == 0)
			return false;
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		return args.length() > 0;
	}
	
	@Override
	public String readString() {
		if (args == null || args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		char c = args.charAt(0);
		if (c != '\'' && c != '"')
			throw new ParsingException("unquoted string at " + state.inputLocation());
		args.delete(0, 1);
		String ret = null;
		for (int i=0;i<args.length();i++) {
			if (args.charAt(i) == c) {
				if (i+1<args.length() && args.charAt(i+1) == c) {
					args.delete(i, i+1);
					continue;
				}
				ret = args.substring(0, i);
				args.delete(0, i+1);
				break;
			}
		}
		if (ret == null)
			throw new ParsingException("unterminated string at " + state.inputLocation());
		return ret;
	}
	
	@Override
	public String readArg() {
		if (args == null || args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		char c = args.charAt(0);
		if (c == '\'' || c == '"')
			return readString();
		for (int i=0;i<args.length();i++) {
			if (Character.isWhitespace(args.charAt(i))) {
				String ret = args.substring(0, i);
				args.delete(0, i);
				return ret;
			}
		}
		String ret = args.toString();
		args.delete(0, args.length());
		return ret;
	}

	@Override
	public Map<String, String> readParams(String... allowedStrings) {
		Map<String, String> ret = new TreeMap<>();
		if (args == null)
			return ret;
		List<String> allowed = Arrays.asList(allowedStrings);
		while (args.length() > 0) {
			while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
				args.delete(0, 1);
			if (args.length() == 0)
				break;
			int j=0;
			while (j < args.length() && args.charAt(j) != '=')
				j++;
			if (j == args.length())
				throw new RuntimeException("needed =");
			String var = args.substring(0, j);
			args.delete(0, j+1);
			if (ret.containsKey(var))
				throw new RuntimeException("duplicate definition of " + var);
			else if (!allowed.contains(var))
				throw new RuntimeException("unexpected definition of " + var + "; allowed = " + allowed);
			else
				ret.put(var, readArg()); 
		}
		return ret;
	}
	
	@Override
	public void argsDone() {
		if (hasMore())
			throw new RuntimeException("command had junk at end: " + args + " at " + state.inputLocation());
	}
	
	@Override
	public String asString() {
		return args.toString();
	}

	@Override
	public String toString() {
		return args.toString();
	}
}