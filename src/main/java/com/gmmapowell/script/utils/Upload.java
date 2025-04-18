package com.gmmapowell.script.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Upload {
	private final Place from;
	private final String scpTo;
	private final String sshid;
	private final boolean debug;

	public Upload(Place output, String scpTo, String sshid, boolean debug) {
		this.from = output;
		this.scpTo = scpTo;
		this.sshid = sshid;
		this.debug = debug;
	}

	public void send() throws JSchException, SftpException {
		if (debug)
			System.out.println("uploading to " + scpTo);
		Pattern p = Pattern.compile("sftp:([a-zA-Z0-9_]+)@([a-zA-Z0-9_.]+)(:[0-9]+)?/(.+)");
		Matcher matcher = p.matcher(scpTo);
		if (!matcher.matches())
			throw new RuntimeException("Could not match path " + scpTo);
		
		String username = matcher.group(1);
		String host = matcher.group(2);
		int port = 22;
		if (matcher.group(3) != null)
			port = Integer.parseInt(matcher.group(3).substring(1));
		String to = matcher.group(4);

		// version
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String version = sdf.format(new Date());
		to = to.replace("$YYYYMMDD$", version);

		System.out.println("uploading to " + host + " as " + username + " into file " + to);
		File f = new File(to);

		File privateKeyPath;
		if (sshid != null) {
			System.out.println("Using private key from " + sshid);
			privateKeyPath = new File(sshid);
		} else {
			System.out.println("Using private key from ~/.ssh/id_rsa");
			privateKeyPath = new File(System.getProperty("user.home"), ".ssh/id_rsa");
		}
//		JSch.setLogger(new JSCHLogger());
		JSch jsch = new JSch();
		jsch.addIdentity(privateKeyPath.getPath());
		Session s = null;
		try {
			System.out.println("username = " + username + " host = " + host + " port = " + port);
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
			openChannel.put(GeoFSUtils.file(from).getPath(), to);
		} finally {
			if (s != null)
				s.disconnect();
		}
	}
}
