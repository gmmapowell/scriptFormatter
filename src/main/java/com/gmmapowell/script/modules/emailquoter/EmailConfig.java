package com.gmmapowell.script.modules.emailquoter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

public class EmailConfig {
	public final Region threads;
	public final Place snaps;
	public final MailPara mailPara;

	public EmailConfig(Region threadRegion, Place snapsPlace) throws FileNotFoundException, IOException {
		this.threads = threadRegion;
		this.snaps = snapsPlace;
		this.mailPara = new MailPara(threadRegion, snapsPlace);
	}
}
