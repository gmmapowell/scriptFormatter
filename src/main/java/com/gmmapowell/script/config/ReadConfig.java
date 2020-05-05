package com.gmmapowell.script.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;

public class ReadConfig {

	public Config read(File file) {
		if (!file.exists()) {
			System.out.println("There is no file " + file);
			return null;
		}
		ScriptConfig ret = new ScriptConfig(file.getParentFile());
		Map<String, String> vars = null;
		boolean debug = false;
		String index = null, workdir = null;
		String what = null, type = null;
		int wline = 0;
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
			String s;
			while ((s = lnr.readLine()) != null) {
				if (s.length() == 0 || s.startsWith("#"))
					continue;
				boolean nested = Character.isWhitespace(s.charAt(0));
				s = s.trim();
				if (s.length() == 0 || s.startsWith("#"))
					continue;
				int idx = s.indexOf(' ');
				if (idx == -1) {
					System.out.println("Syntax error on line " + lnr.getLineNumber());
					System.out.println("  " + s);
					return null;
				}
				String key = s.substring(0, idx);
				String value = s.substring(idx+1).trim();
				if (!nested) {
					// if a new block is starting, flush (any) previous block
					if (!handleCreation(ret, vars, debug, index, workdir, what, type, wline))
						return null;
					vars = new TreeMap<>();
					what = null;
					switch (key) {
					case "debug": {
						debug = Boolean.parseBoolean(value);
						break;
					}
					case "index": {
						index = value;
						break;
					}
					case "workdir": {
						workdir = value;
						break;
					}
					default: {
						what = key;
						type = value;
						wline = lnr.getLineNumber();
						break;
					}
					}
				} else if (what == null) {
					System.out.println(lnr.getLineNumber() + ": must have outer block to nest inside: " + s);
				} else
					vars.put(key, value);
			}
			if (!handleCreation(ret, vars, debug, index, workdir, what, type, wline))
				return null;
		} catch (IOException ex) {
			System.out.println("Could not read configuration " + file);
			return null;
		} catch (ConfigException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		return ret;
	}

	private boolean handleCreation(ScriptConfig ret, Map<String, String> vars, boolean debug, String index, String workdir,
			String what, String type, int wline) throws ConfigException {
		if (what == null)
			return true;
		switch (what) {
		case "loader": {
			if (index == null) {
				System.out.println(wline + ": must specify index before loader");
				return false;
			}
			ret.handleLoader(vars, type, index, workdir, debug);
			break;
		}
		case "processor": {
			ret.handleProcessor(vars, type, debug);
			break;
		}
		case "output": {
			ret.handleOutput(vars, type, debug);
			break;
		}
		default: {
			System.out.println(wline +": there is no block " + what);
			return false;
		}
		}
		if (!vars.isEmpty()) {
			System.out.println(wline + ": block " + what + " was given vars but did not use them:");
			for (String v : vars.keySet())
				System.out.println("  " + v);
			return false;
		}
		return true;
	}

}
