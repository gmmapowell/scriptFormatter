package com.gmmapowell.script.modules.processors.blog;

import java.io.File;

import com.gmmapowell.script.config.ExtensionPoint;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface ImgUploader extends ExtensionPoint {

	String upload(File tmpFile, String encoded) throws JSchException, SftpException;

}
