package com.gmmapowell.script.config;

import java.util.Map;
import java.util.TreeMap;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.utils.Utils;

public class ConfigParser implements NumberedLineListener {
	private ScriptConfig config;
	Map<String, String> vars = null;
	boolean debug = false;
	Place index = null;
	Region workdir = null;
	String sshid = null;
	String what = null, type = null;
	int wline = 0;
	private Region root;
	private Exception capture;

	public ConfigParser(Region root) {
		this.root = root;
		config = new ScriptConfig(root);
	}
	
	@Override
	public void line(int lno, String s) {
		if (config == null)
			return;
		if (s.length() == 0 || s.startsWith("#"))
			return;
		boolean nested = Character.isWhitespace(s.charAt(0));
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
		if (!nested) {
			// if a new block is starting, flush (any) previous block
			if (workdir == null)
				workdir = root.subregion("downloads");
			if (!handleCreation(config, vars, debug, index, sshid, workdir, what, type, wline)) {
				config = null;
				return;
			}
			vars = new TreeMap<>();
			what = null;
			switch (key) {
			case "debug": {
				debug = Boolean.parseBoolean(value);
				break;
			}
			case "index": {
				index = root.placePath(value);
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
		} else
			vars.put(key, value);
	}
	
	@Override
	public void complete() {
		if (workdir == null)
			workdir = GeoFSUtils.ensureRegionPath(root, "downloads");
		if (!handleCreation(config, vars, debug, index, sshid, workdir, what, type, wline)) {
			config = null;
		}
	}
	
	private boolean handleCreation(ScriptConfig ret, Map<String, String> vars, boolean debug, Place index, String sshid, Region workdir, String what, String type, int wline) {
		try {
			if (what == null || ret == null)
				return true;
			ret.setIndex(index);
			ret.setWorkdir(workdir);
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
				ret.handleOutput(vars, type, debug, sshid);
				break;
			}
			case "webedit": {
				ret.handleWebedit(vars, type, debug, sshid);
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
