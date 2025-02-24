package com.gmmapowell.geofs.doubled;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;

public class RegionDouble implements Region, RegionPlace {
	public Map<String, RegionPlace> entries = new TreeMap<>();

	@Override
	public Universe getUniverse() {
		throw new NotImplementedException();
	}

	@Override
	public Region subregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Region newSubregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Region parent() {
		throw new NotImplementedException();
	}

	@Override
	public boolean hasPlace(String string) {
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
	public Place newPlace(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Place ensurePlace(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Place ensureRegionAndPlace(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Region regionPath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Place ensurePlacePath(String path) {
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

	public void addPlace(String name, byte[] contents) {
		if (entries.containsKey(name))
			throw new CantHappenException("don't provide multiple entries for " + name);
		entries.put(name, new PlaceByteArray(contents));
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
	
	// TODO: place from file
	// TODO: regions
}
