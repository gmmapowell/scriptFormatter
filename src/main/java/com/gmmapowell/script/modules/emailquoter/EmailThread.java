package com.gmmapowell.script.modules.emailquoter;

import java.util.Set;
import java.util.TreeSet;

import com.gmmapowell.geofs.Region;

public class EmailThread implements Comparable<EmailThread> {
	private final Region region;
	public final Set<EmailAt> emails = new TreeSet<>();
	
	public EmailThread(Region r) {
		this.region = r;
	}
	
	public String name() {
		return region.name();
	}
	
	public void add(EmailAt emailAt) {
		emails.add(emailAt);
	}

	@Override
	public int compareTo(EmailThread o) {
		return region.name().compareTo(o.region.name());
	}
}
