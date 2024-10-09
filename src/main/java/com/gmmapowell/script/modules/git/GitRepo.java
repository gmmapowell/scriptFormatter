package com.gmmapowell.script.modules.git;

import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class GitRepo {
	private Region repositoryRoot;
	private String repoPath;

	public void repository(GitState global, String repo) {
		this.repoPath = repo;
		this.repositoryRoot = global.formatterRoot.regionPath(repo);
	}
	
	public String repoPath() {
		return repoPath;
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
