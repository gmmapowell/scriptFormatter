package com.gmmapowell.geofs.git;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Universe;

public class GitRoot {
	private final GitWorld world;
	private final String repo;
	private final String tag;

	public GitRoot(GitWorld world, String repo, String tag) {
		this.world = world;
		this.repo = repo;
		this.tag = tag;
	}

	public Universe getUniverse() {
		return world.getUniverse();
	}
	
	public GitType findPath(File path) {
		RunProcess proc = new RunProcess("git");
		proc.showArgs(true);
		proc.captureStdout();
		proc.arg("-C");
		proc.arg(repo);
		proc.arg("cat-file");
		proc.arg("-t");
		proc.arg(tag + ":" + path);
		proc.execute();
		
		String out = proc.getStdout();
		System.out.println("GIT: " + out);
		switch (out.trim()) {
		case "":
			return GitType.NONEXIST;
		case "tree":
			return GitType.TREE;
		case "blob":
			return GitType.BLOB;
		default:
			throw new CantHappenException("what type is this in git: " + out.trim() + "?");
		}
	}
}
