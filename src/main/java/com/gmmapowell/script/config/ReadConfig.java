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
		Map<String, String> vars = new TreeMap<>();
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
			String s;
			while ((s = lnr.readLine()) != null) {
				s = s.trim();
				if (s.length() == 0 || s.startsWith("#"))
					continue;
				int idx = s.indexOf(' ');
				if (idx == -1) {
					System.out.println("Syntax error on line " + lnr.getLineNumber());
					System.out.println("  " + s);
					return null;
				}
				String before = s.substring(0, idx);
				String after = s.substring(idx+1).trim();
				vars.put(before, after);
			}
		} catch (IOException ex) {
			System.out.println("Could not read configuration " + file);
		}
		ScriptConfig ret = new ScriptConfig(file.getParentFile());
		try {
			ret.handleLoader(vars);
			ret.handleOutput(vars);
			ret.handleProcessor(vars);
		} catch (ConfigException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		if (!vars.isEmpty()) {
			System.out.println("these vars were specified but not used:");
			for (String v : vars.keySet())
				System.out.println("  " + v);
			return null;
		}
		return ret;
	}

}
