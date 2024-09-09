package com.gmmapowell.geofs.git;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;

public class GitExaminer implements Region {
	private final GitWorld world;

	public GitExaminer(GitWorld world, String repo, String tag) {
		this.world = world;
	}

	@Override
	public Universe getUniverse() {
		return world.getUniverse();
	}

	@Override
	public String name() {
		return "/";
	}

	@Override
	public Region subregion(String name) {
		throw new NotImplementedException();
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
		throw new NotImplementedException();
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
		throw new NotImplementedException();
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
	public Region parent() {
		throw new NotImplementedException();
	}

	@Override
	public Region ensureSubregion(String name) {
		throw new NotImplementedException("GitWorld is read only");
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
