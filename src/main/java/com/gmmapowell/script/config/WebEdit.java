package com.gmmapowell.script.config;

import java.io.File;

import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class WebEdit {
	public final File file;
	public final String upload;
	private final String sshid;
	public final String title;

	public WebEdit(File file, String upload, String sshid, String title) {
		this.file = file;
		this.upload = upload;
		this.sshid = sshid;
		this.title = title;
	}

	public void upload() throws SftpException, JSchException {
		new Upload(file, upload, sshid, true).send();;
	}
}
