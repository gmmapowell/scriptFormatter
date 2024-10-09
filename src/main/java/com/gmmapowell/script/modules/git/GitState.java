package com.gmmapowell.script.modules.git;

import com.gmmapowell.geofs.Region;

public class GitState {
	public Region formatterRoot;

	public void formatterRoot(Region root) {
		this.formatterRoot = root;
	}
	
	public Region root() {
		return formatterRoot;
	}
}
