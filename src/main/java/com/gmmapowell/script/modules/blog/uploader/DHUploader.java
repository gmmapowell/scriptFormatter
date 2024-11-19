package com.gmmapowell.script.modules.blog.uploader;

import java.io.File;

import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.Finisher;
import com.gmmapowell.script.modules.processors.blog.ImgUploader;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class DHUploader implements ImgUploader, Finisher {
	private final Universe u;
	private String sshid = System.getenv("SSHID");
	private ImgCache imgcache;

	public DHUploader(GlobalState state) {
		this.u = state.getUniverse();
		Place cache = state.getRoot().ensurePlace("imgcache");
		this.imgcache = new ImgCache(cache);
		state.addFinisher(this);
	}
	
	@Override
	public String upload(File tmpFile, String encoded) throws JSchException, SftpException {
		String hash = hashFile(tmpFile);
		if (!imgcache.hasAlready(encoded, hash)) {
			System.out.println("have " + hash + " for " + tmpFile + " associated with " + encoded);
			imgcache.associate(encoded, hash);
			// TODO: put this in a background thead executor queue
			copyToDH(tmpFile, "sftp:gmmapowell@gmmapowell.com/gmmapowell.com/blog-images/" + encoded);
		}
		return "https://gmmapowell.com/blog-images/" + encoded;
	}

	private void copyToDH(File tmpFile, String upload) throws JSchException, SftpException {
		new Upload(u.getWorld("lfs").placePath(tmpFile.toString()), upload, sshid, true).send();
	}

	private String hashFile(File tmpFile) {
		RunProcess gitcmd = new RunProcess("git");
		gitcmd.arg("hash-object");
		gitcmd.arg(tmpFile.toString());
		gitcmd.redirectStderr(System.err);
		gitcmd.captureStdout();
		gitcmd.execute();
		return gitcmd.getStdout().trim();
	}

	@Override
	public void finish() {
		System.out.println("Writing cache");
		this.imgcache.write();
	}
}
