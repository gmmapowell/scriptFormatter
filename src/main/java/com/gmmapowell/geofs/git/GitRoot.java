package com.gmmapowell.geofs.git;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.parser.LinePatternMatch;
import org.zinutils.parser.LinePatternParser;
import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;

public class GitRoot {
	private final GitWorld world;
	private final String repo;
	private final String tag;

	public GitRoot(GitWorld world, String repo, String tag) {
		this.world = world;
		this.repo = repo;
		this.tag = tag;
	}

	public World getWorld() {
		return world;
	}

	public Universe getUniverse() {
		return world.getUniverse();
	}
	
	public GitType findPath(File path) {
		RunProcess proc = new RunProcess("git");
		proc.showArgs(false);
		proc.captureStdout();
		proc.arg("-C");
		proc.arg(repo);
		proc.arg("cat-file");
		proc.arg("-t");
		proc.arg(tag + ":" + nullPath(path));
		proc.execute();
		
		String out = proc.getStdout();
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

	public void listChildren(File path, GitEntryListener lsnr) {
		RunProcess proc = new RunProcess("git");
		proc.showArgs(false);
		proc.captureStdout();
		proc.arg("-C");
		proc.arg(repo);
		proc.arg("cat-file");
		proc.arg("-p");
		proc.arg(tag + ":" + nullPath(path));
		proc.execute();

		LinePatternParser lpp = new LinePatternParser();
		lpp.match("([0-7]*) (tree) ([0-9a-f]*)\t(.*)", "tree", "perm", "type", "id", "name");
		lpp.match("([0-7]*) (blob) ([0-9a-f]*)\t(.*)", "blob", "perm", "type", "id", "name");
		String stdout = proc.getStdout();
		for (LinePatternMatch lpm : lpp.applyTo(new StringReader(stdout))) {
			lsnr.entry(lpm.get("type"), lpm.get("name"));
		}

	}

	private String nullPath(File path) {
		if (path == null)
			return "";
		else
			return path.toString();
	}

	public Reader reader(File path) {
		return new InputStreamReader(inputStream(path));
	}

	private InputStream inputStream(File path) {
		RunProcess proc = new RunProcess("git");
		proc.showArgs(false);
		proc.captureStdout();
		proc.arg("-C");
		proc.arg(repo);
		proc.arg("show");
		proc.arg(tag + ":" + nullPath(path));
		proc.execute();

		return proc.getStdoutStream();
	}

	public String repo() {
		return repo;
	}
}
