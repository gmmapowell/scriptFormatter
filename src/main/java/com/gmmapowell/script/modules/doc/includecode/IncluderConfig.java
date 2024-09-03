package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.geofs.Region;

public class IncluderConfig {
	private Region samples;

	public void setSamples(Region samples) {
		System.out.println("samples at " + samples);
		this.samples = samples;
	}

	public Region samples() {
		return samples;
	}
}
