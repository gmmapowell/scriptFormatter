package com.gmmapowell.geofs.git;

import java.io.File;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class GitRegion implements Region {
	private final GitRoot root;
	private final Region parent;
	private final File myPath;
	private final String myName;

	public GitRegion(GitRoot root, Region parent, File path, String name) {
		this.root = root;
		this.parent = parent;
		this.myPath = path;
		this.myName = name;
	}

	@Override
	public Region parent() {
		return parent;
	}

	@Override
	public Universe getUniverse() {
		return root.getUniverse();
	}

	@Override
	public String name() {
		return myName;
	}

	@Override
	public Region subregion(String name) {
		File path = new File(myPath, name);
		GitType foo = root.findPath(path);
		if (foo == GitType.NONEXIST)
			throw new GeoFSNoRegionException(name);
		return new GitRegion(root, this, path, name);
	}

	@Override
	public Region newSubregion(String name) {
		throw new NotImplementedException("GitWorld is read only");
	}

	@Override
	public boolean hasPlace(String string) {
		throw new NotImplementedException();
	}

	@Override
	public Place place(String name) {
		return new GitPlace(this, name);
	}

	@Override
	public Place newPlace(String name) {
		throw new NotImplementedException("GitWorld is read only");
	}

	@Override
	public Place ensurePlace(String name) {
		throw new NotImplementedException("GitWorld is read only");
	}

	@Override
	public Region regionPath(String path) {
		return GeoFSUtils.regionPath(root.getWorld(), this, path);
	}

	@Override
	public Place placePath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Place ensurePlacePath(String path) {
		throw new NotImplementedException("GitWorld is read only");
	}

	@Override
	public Region ensureSubregion(String name) {
		throw new NotImplementedException("GitWorld is read only");
	}

	@Override
	public void places(PlaceListener lsnr) {
		root.listChildren(myPath, (type, name) -> {
			if (type.equals("blob"))
				lsnr.place(place(name));
		});
	}

	@Override
	public void regions(RegionListener lsnr) {
		throw new NotImplementedException();
	}
	
	@Override
	public String toString() {
		return myPath.toString();
	}
}
