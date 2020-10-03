package com.gmmapowell.script.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class WebEdit {
	public final File file;
	public final String upload;

	public WebEdit(File file, String upload) {
		this.file = file;
		this.upload = upload;
	}

	public void upload() throws SftpException, JSchException {
		Pattern p = Pattern.compile("sftp:([a-zA-Z0-9_]+)@([a-zA-Z0-9_.]+)(:[0-9]+)?/(.+)");
		Matcher matcher = p.matcher(upload);
		if (!matcher.matches())
			throw new RuntimeException("Could not match path " + upload);
		
		String username = matcher.group(1);
		String host = matcher.group(2);
		int port = 22;
		if (matcher.group(3) != null)
			port = Integer.parseInt(matcher.group(3).substring(1));
		String to = matcher.group(4);

		System.out.println("uploading to " + host + " as " + username + " into file " + to);
		File f = new File(to);

		File privateKeyPath = new File(System.getProperty("user.home"), ".ssh/cata_rsa");
		JSch jsch = new JSch();
		jsch.addIdentity(privateKeyPath.getPath());
		Session s = null;
		try {
			s = jsch.getSession(username, host, port);
			s.setConfig("StrictHostKeyChecking", "no");
			s.connect();
			ChannelSftp openChannel = (ChannelSftp) s.openChannel("sftp");
			openChannel.connect();
			if (f.getParent() != null) {
				try {
					openChannel.stat(f.getParent());
				} catch (SftpException ex) {
					openChannel.mkdir(f.getParent());
				}
			}
			openChannel.put(file.getPath(), to);
		} finally {
			if (s != null)
				s.disconnect();
		}
	}
}
