package com.gmmapowell.script.modules.emailquoter;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

public class EmailConfig {
	public final Region threads;
	public final Place snaps;

	public EmailConfig(Region threadRegion, Place snapsPlace) {
		this.threads = threadRegion;
		this.snaps = snapsPlace;
	}
}
