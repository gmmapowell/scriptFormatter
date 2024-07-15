package com.gmmapowell.geofs.doubled;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;

public class WorldDouble implements World {
	private Region defaultRoot;
	private Map<String, Region> roots = new TreeMap<>();
	
	// How do we want to configure this?
	// I think the constructor just takes a set of roots and then creates empty regions
	// Then the user has to go through each region they want to configure and add to that
	// No strings here (or any of them null) creates the default region

	public WorldDouble(String... roots) {
		defaultRoot = new RegionDouble();
	}
	
	@Override
	public Region root() {
		if (defaultRoot != null)
			return defaultRoot;
		throw new NotImplementedException();
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
