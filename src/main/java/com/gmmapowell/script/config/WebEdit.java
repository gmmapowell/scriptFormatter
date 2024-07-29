package com.gmmapowell.script.config;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class WebEdit {
	public final Place file;
	public final String upload;
	private final String sshid;
	public final String title;

	public WebEdit(Place place, String upload, String sshid, String title) {
		this.file = place;
		this.upload = upload;
		this.sshid = sshid;
		this.title = title;
	}

	public void upload() throws SftpException, JSchException {
		new Upload(file, upload, sshid, true).send();;
	}
}
