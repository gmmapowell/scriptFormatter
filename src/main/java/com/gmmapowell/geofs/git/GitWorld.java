package com.gmmapowell.geofs.git;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class GitWorld implements World {
	private final Universe universe;

	public GitWorld(Universe universe) {
		this.universe = universe;
		universe.register("git", this);
	}
	
	@Override
	public Universe getUniverse() {
		return universe;
	}
	
	@Override
	public void prepare() throws Exception {
	}
	
	@Override
	public Region root() {
		throw new NotImplementedException("You need to provide a repo and optionally a tag");
	}

	@Override
	public Region root(String root) {
		int idx = root.indexOf(':');
		String repo, tag;
		if (idx == -1) {
			repo = root;
			tag = "HEAD";
		} else {
			repo = root.substring(0, idx);
			tag = root.substring(idx+1);
		}
		return new GitRegion(new GitRoot(this, repo, tag), null, null, null);
	}

	@Override
	public Region regionPath(String path) {
		return GeoFSUtils.regionPath(this, null, path);
	}

	@Override
	public Region newRegionPath(String path) {
		return GeoFSUtils.newRegionPath(this, null, path);
	}

	@Override
	public Place placePath(String path) {
		return GeoFSUtils.placePath(this, null, path);
	}
	
	@Override
	public Place newPlacePath(String path) {
		return GeoFSUtils.newPlacePath(this, null, path);
	}

	@Override
	public Place ensurePlacePath(String path) {
		return GeoFSUtils.ensurePlacePath(this, null, path);
	}
}
