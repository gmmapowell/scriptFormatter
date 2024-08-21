package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class LocalFileSystem implements World {
	private Universe universe;
	private Region cwd;

	public LocalFileSystem(Universe universe) {
		this.universe = universe;
		if (universe != null)
			universe.register("lfs", this);
		cwd = new LFSRegion(this, new File(System.getProperty("user.dir")));
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public Universe getUniverse() {
		return universe;
	}

	@Override
	public Region root() {
		return new LFSRegion(this, new File("/"));
	}

	@Override
	public Region root(String root) {
		if (root.equals("~")) {
			return new LFSRegion(this, new File(System.getProperty("user.home")));
		} else
			throw new NotImplementedException();
	}

	@Override
	public Region regionPath(String path) {
		return GeoFSUtils.regionPath(this, cwd, path);
	}

	@Override
	public Region newRegionPath(String path) {
		return GeoFSUtils.newRegionPath(this, cwd, path);
	}

	@Override
	public Place placePath(String path) {
		return GeoFSUtils.placePath(this, cwd, path);
	}

	@Override
	public Place newPlacePath(String path) {
		return GeoFSUtils.newPlacePath(this, cwd, path);
	}

	@Override
	public Place ensurePlacePath(String path) {
		return GeoFSUtils.ensurePlacePath(this, cwd, path);
	}
}
