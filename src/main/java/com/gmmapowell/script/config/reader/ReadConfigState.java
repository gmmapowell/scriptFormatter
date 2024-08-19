package com.gmmapowell.script.config.reader;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.utils.SBLocation;

public class ReadConfigState extends SBLocation {
	public ScriptConfig config;
	public boolean debug = false;
	public Place index = null;
	public Region workdir = null;
	public String sshid = null;
	public String what = null, type = null;
	public int wline = 0;
	public Region root;
	
	public ReadConfigState(Region root, ScriptConfig sc) {
		this.root = root;
		this.config = sc;
	}

	public Universe universe() {
		return root.getUniverse();
	}
}
