package com.gmmapowell.geofs.doubled;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

public class RegionDouble implements Region, RegionPlace {
	public Map<String, RegionPlace> entries = new TreeMap<>();

	@Override
	public Region subregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Place place(String name) {
		RegionPlace entry = entries.get(name);
		if (!(entry instanceof Place))
			throw new CantHappenException("the entry " + name + " is not a Place");
		return (Place)entry;
	}

	@Override
	public Region regionPath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Place placePath(String path) {
		throw new NotImplementedException();
	}
	
	// Configuration methods
	
	public void addPlace(String name, String contents) {
		if (entries.containsKey(name))
			throw new CantHappenException("don't provide multiple entries for " + name);
		entries.put(name, new PlaceString(contents));
	}
	
	// TODO: place from file
	// TODO: regions
}
