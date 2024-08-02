package com.gmmapowell.geofs.lfs;

import java.io.File;
import java.io.IOException;

import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.exceptions.GeoFSPlaceExistsException;

public class LFSPendingPlace extends LFSPlace {

	public LFSPendingPlace(LocalFileSystem world, File file) {
		super(world, file, true);
	}

	@Override
	protected void createFile() {
		if (file.exists()) {
			throw new GeoFSPlaceExistsException(file.getPath());
		}
		if (!file.getParentFile().exists())
			throw new GeoFSNoRegionException(file.getParent());
		try {
			file.createNewFile();
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}
}
