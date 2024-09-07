package com.gmmapowell.script.modules.git;

import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class GitState {
	private Region formatterRoot;
	private Region repositoryRoot;

	public void formatterRoot(Region root) {
		this.formatterRoot = root;
	}

	public void repository(String repo) {
		this.repositoryRoot = formatterRoot.regionPath(repo);
	}
	
	public void showDelta(ConfiguredState sink, String branch, String filespec, String from, String to) {
		if (repositoryRoot == null)
			throw new RuntimeException("Cannot use &import without &git");
		System.out.println("git show " + repositoryRoot + " " + branch + " " + filespec + " " + from + " => " + to);
		RunProcess gitcmd = new RunProcess("git");
		gitcmd.arg("show");
		gitcmd.arg(branch);
		gitcmd.redirectStderr(System.err);
		gitcmd.processStdout(new GitShowProcessor(sink, branch, filespec, from, to));
		gitcmd.executeInDir(GeoFSUtils.file(repositoryRoot));
		gitcmd.execute();
	}
}
