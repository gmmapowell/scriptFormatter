package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;

public class LocalFileSystem implements World {

	@Override
	public Region root() {
		return new LFSRegion(new File("/"));
	}

	@Override
	public Region root(String root) {
		throw new NotImplementedException();
	}

	@Override
	public Region regionPath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Place placePath(String path) {
		throw new NotImplementedException();
	}
}
