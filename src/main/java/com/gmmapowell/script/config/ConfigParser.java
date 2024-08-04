package com.gmmapowell.script.config;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.utils.Utils;

public class ConfigParser implements NumberedLineListener {
	private ScriptConfig config;
	VarMap vars = null;
	boolean debug = false;
	Place index = null;
	Region workdir = null;
	String sshid = null;
	String what = null, type = null;
	int wline = 0;
	private Region root;
	private Exception capture;

	public ConfigParser(Universe universe, Region root) {
		this.root = root;
		config = new ScriptConfig(universe, root);
	}
	
	@Override
	public void line(int lno, String s) {
		if (config == null)
			return;
		if (s.length() == 0 || s.startsWith("#"))
			return;
		int nesting = 0;
		while (nesting < s.length() && Character.isWhitespace(s.charAt(nesting)))
			nesting++;
		s = s.trim();
		if (s.length() == 0 || s.startsWith("#"))
			return;
		int idx = s.indexOf(' ');
		if (idx == -1) {
			System.out.println("Syntax error on line " + lno);
			System.out.println("  " + s);
			config = null;
			return;
		}
		String key = s.substring(0, idx);
		String value = s.substring(idx+1).trim();
		if (nesting == 0) {
			// if a new block is starting, flush (any) previous block
			if (workdir == null)
				workdir = root.ensureSubregion("downloads");
			if (!handleCreation()) {
				config = null;
				return;
			}
			vars = new VarMap();
			what = null;
			switch (key) {
			case "debug": {
				debug = Boolean.parseBoolean(value);
				break;
			}
			case "index": {
				index = root.ensurePlace(value);
//				index = new File(value);
//				if (!index.isAbsolute())
//					index = new File(root, value);
				break;
			}
			case "sshid": {
				sshid = Utils.subenvs(value);
				break;
			}
			case "workdir": {
				workdir = root.regionPath(value);
//				workdir = new File(value);
//				if (!workdir.isAbsolute())
//					workdir = new File(root, value);
				break;
			}
			default: {
				what = key;
				type = value;
				wline = lno;
				break;
			}
			}
		} else if (what == null) {
			System.out.println(lno + ": must have outer block to nest inside: " + s);
		} else {
			vars.put(nesting, key, value);
		}
	}
	
	@Override
	public void complete() {
		if (workdir == null)
			workdir = GeoFSUtils.ensureRegionPath(root, "downloads");
		if (!handleCreation()) {
			config = null;
		}
	}
	
	private boolean handleCreation() {
		try {
			if (what == null || config == null)
				return true;
			config.setIndex(index);
			config.setWorkdir(workdir);
			switch (what) {
			case "loader": {
				if (index == null) {
					System.out.println(wline + ": must specify index before loader");
					return false;
				}
				config.handleLoader(vars, type, index, workdir, debug);
				break;
			}
			case "processor": {
				config.handleProcessor(vars, type, debug);
				break;
			}
			case "output": {
				config.handleOutput(vars, type, debug, sshid);
				break;
			}
			case "webedit": {
				config.handleWebedit(vars, type, debug, sshid);
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
		} catch (Exception ex) {
			this.capture = ex;
			return false;
		}
	}

	public Config config() throws Exception {
		if (this.capture != null)
			throw this.capture;
		return config;
	}
}
