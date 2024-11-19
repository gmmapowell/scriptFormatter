package com.gmmapowell.script.modules.processors.blog;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Set;

import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class UploadAll {
	private Set<ImgUploader> uploaders;

	public UploadAll(GlobalState state) {
		this.uploaders = state.extensions().forPoint(ImgUploader.class, state);
	}

	public String upload(File tmpFile, String name) throws JSchException, SftpException {
		String encoded = URLEncoder.encode(name, Charset.forName("UTF-8"));
		String s = null;
		for (ImgUploader u : uploaders) {
			if (s == null)
				s = u.upload(tmpFile, encoded);
		}
		return s;
	}
}
