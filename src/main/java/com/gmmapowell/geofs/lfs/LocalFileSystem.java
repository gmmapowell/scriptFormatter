package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;
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
		File f = new File(path);
		if (!f.isAbsolute())
			throw new CantHappenException("relative paths must be used from a region");
		if (!f.isDirectory())
			throw new CantHappenException("there is no region " + f);
		return new LFSRegion(f);
	}

	@Override
	public Place placePath(String path) {
		File f = new File(path);
		if (!f.isAbsolute())
			throw new CantHappenException("relative paths must be used from a region");
		if (!f.isFile())
			throw new CantHappenException("there is no place " + f);
		return new LFSPlace(f);
	}
}
