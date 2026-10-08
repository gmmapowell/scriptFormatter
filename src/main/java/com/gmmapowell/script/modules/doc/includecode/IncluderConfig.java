package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.geofs.Region;

public class IncluderConfig {
	private Region samples;

	public void setSamples(Region samples) {
		this.samples = samples;
	}

	public Region samples() {
		return samples;
	}
}
