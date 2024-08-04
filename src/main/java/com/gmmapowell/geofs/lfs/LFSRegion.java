package com.gmmapowell.geofs.lfs;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class LFSRegion implements Region {
	private final LocalFileSystem world;
	protected final File file;

	public LFSRegion(LocalFileSystem world, File file) {
		this.world = world;
		if (!file.isDirectory())
			throw new CantHappenException("there is no directory " + file);
		this.file = file;
	}
	
	public LFSRegion(LocalFileSystem world, File file, boolean notExists) {
		this.world = world;
		if (!notExists)
			throw new CantHappenException("this constructor must be called with notExists = true");
		if (file.exists())
			throw new CantHappenException("the region exists (may be file): " + file);
		this.file = file;
	}

	@Override
	public Region parent() {
		if (file == null || file.getParentFile() == null)
			throw new CantHappenException("this region does not have a parent");
		return new LFSRegion(world, file.getParentFile());
	}

	@Override
	public Region subregion(String name) {
		File f = new File(file, name);
		return new LFSRegion(world, f);
	}

	@Override
	public Region newSubregion(String name) {
		LFSPendingRegion ret = new LFSPendingRegion(world, new File(file, name));
		ret.create();
		return ret;
	}

	@Override
	public Place place(String name) {
		File f = new File(file, name);
		return new LFSPlace(world, f);
	}

	@Override
	public Place newPlace(String name) {
		File f = new File(file, name);
		return new LFSPendingPlace(world, f);
	}

	@Override
	public Place ensurePlace(String name) {
		File f = new File(file, name);
		if (f.isFile())
			return new LFSPlace(world, f);
		else
			return new LFSPendingPlace(world, f);
	}

	@Override
	public Region ensureSubregion(String name) {
		File f = new File(file, name);
		if (f.isDirectory())
			return new LFSRegion(world, f);
		else if (f.isFile())
			throw new GeoFSNoRegionException(f.getPath());
		else
			return new LFSPendingRegion(world, f);
	}

	@Override
	public Region regionPath(String path) {
		return GeoFSUtils.regionPath(world, this, path);
	}

	@Override
	public Place placePath(String path) {
		return GeoFSUtils.placePath(world, this, path);
	}

	@Override
	public Place ensurePlacePath(String path) {
		return GeoFSUtils.ensurePlacePath(world, this, path);
	}

	public File getFile() {
		return file;
	}

	@Override
	public String name() {
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
	
	@Override
	public String toString() {
		return file.getPath();
	}
}
