package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

public class LFSRegion implements Region {
	private final File file;

	public LFSRegion(File file) {
		if (!file.isDirectory())
			throw new CantHappenException("there is no directory " + file);
		this.file = file;
	}

	@Override
	public Region subregion(String name) {
		File f = new File(file, name);
		return new LFSRegion(f);
	}

	@Override
	public Place place(String name) {
		File f = new File(file, name);
		return new LFSPlace(f);
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
