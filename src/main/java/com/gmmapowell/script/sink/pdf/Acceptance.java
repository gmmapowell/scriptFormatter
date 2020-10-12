package com.gmmapowell.script.sink.pdf;

public class Acceptance {
	public final Acceptability status;
	public final StyledToken lastAccepted;
	
	public Acceptance(Acceptability status, StyledToken lastAccepted) {
		this.status = status;
		this.lastAccepted = lastAccepted;
	}
}
