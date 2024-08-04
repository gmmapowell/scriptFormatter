package com.gmmapowell.script.modules.emailquoter;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.listeners.LineListener;

public class EmailAt implements Comparable<EmailAt>{
	private final Region mr;

	public EmailAt(Region mr) {
		this.mr = mr;
	}

	@Override
	public int compareTo(EmailAt o) {
		return mr.name().compareTo(o.mr.name());
	}

	public String name() {
		return mr.name().substring(0, 16);
	}

	public void text(LineListener lsnr) {
		if (!mr.hasPlace("text")) {
			System.out.println("only html");
			lsnr.line("only html");
			lsnr.complete();
		} else {
			Place p = mr.place("text");
			p.lines(lsnr);
		}
	}

	public EmailMeta meta() {
		EmailMeta ret = new EmailMeta();
		mr.place("meta").lines(l -> {
			int idx = l.indexOf(": ");
			String[] ls = new String[2];
			ls[0] = l.substring(0, idx);
			ls[1] = l.substring(idx+2);
			switch (ls[0]) {
			case "Date": ret.setDate(ls[1]); break;
			case "From": ret.setFrom(ls[1]); break;
			case "To": ret.setTo(ls[1]); break;
			case "Message-ID": ret.setId(ls[1]); break;
			case "Subject": ret.setSubject(ls[1]); break;
			case "In-Reply-To": ret.setIRT(ls[1]); break;
			default: System.out.println(l); break;
			}
		});
		return ret;
	}
}
