package com.gmmapowell.script.sink.pdf;

import com.gmmapowell.script.flow.StyledToken;

public class Acceptance {
	public final Acceptability status;
	public final StyledToken lastAccepted;
	private String enable;
	
	public Acceptance(Acceptability status, StyledToken lastAccepted) {
		this.status = status;
		this.lastAccepted = lastAccepted;
	}

	public Acceptance enableFlow(String enable) {
		this.enable = enable;
		return this;
	}

	public String enable() {
		return enable;
	}
}
