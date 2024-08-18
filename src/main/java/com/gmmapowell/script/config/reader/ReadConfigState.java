package com.gmmapowell.script.config.reader;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.utils.SBLocation;

public class ReadConfigState extends SBLocation {
	ScriptConfig config;
	VarMap vars = null;
	boolean debug = false;
	Place index = null;
	Region workdir = null;
	String sshid = null;
	String what = null, type = null;
	int wline = 0;
	Region root;
	
	
	public ReadConfigState(Region root, ScriptConfig sc) {
		this.root = root;
		this.config = sc;
	}
}
