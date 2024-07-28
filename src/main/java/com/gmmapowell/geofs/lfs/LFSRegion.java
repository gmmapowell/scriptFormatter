package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;

public class LFSRegion implements Region {
	private final File file;

	public LFSRegion(File file) {
		if (!file.isDirectory())
			throw new CantHappenException("there is no directory " + file);
		this.file = file;
	}
	
	@Override
	public Region parent() {
		if (file == null || file.getParentFile() == null)
			throw new CantHappenException("this region does not have a parent");
		return new LFSRegion(file.getParentFile());
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
		File f = new File(path);
		if (f.isAbsolute())
			throw new CantHappenException("absolute paths must be used from the world");
		f = new File(file, path);
		if (!f.isDirectory())
			throw new CantHappenException("there is no region " + f);
		return new LFSRegion(f);
	}

	@Override
	public Place placePath(String path) {
		File f = new File(path);
		if (f.isAbsolute())
			throw new CantHappenException("absolute paths must be used from the world");
		f = new File(file, path);
		if (!f.isFile())
			throw new CantHappenException("there is no place " + f);
		return new LFSPlace(f);
	}

	public File getFile() {
		return file;
	}

	@Override
	public String name() {
		throw new NotImplementedException();
	}

	@Override
	public Region ensureSubregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void places(PlaceListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public void regions(RegionListener lsnr) {
		throw new NotImplementedException();
	}
	
	
}
