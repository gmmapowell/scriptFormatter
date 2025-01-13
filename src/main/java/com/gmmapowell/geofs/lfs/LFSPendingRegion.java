package com.gmmapowell.geofs.lfs;

import java.io.File;

import com.gmmapowell.geofs.exceptions.GeoFSCannotCreateRegionException;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;

public class LFSPendingRegion extends LFSRegion {

	public LFSPendingRegion(LocalFileSystem world, File file) {
		super(world, file, true);
		if (!file.getParentFile().exists()) {
			throw new GeoFSNoRegionException(file.getParent());
		}
	}

	public void create() {
		if (!file.mkdir())
			throw new GeoFSCannotCreateRegionException(file.getPath());
	}

	public void ensureExists() {
		if (file.isDirectory())
			return;
		else
			this.create();
	}

}
