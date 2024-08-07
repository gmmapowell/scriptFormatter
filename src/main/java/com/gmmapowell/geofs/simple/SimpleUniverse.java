package com.gmmapowell.geofs.simple;

import java.util.Map;
import java.util.TreeMap;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSDuplicateWorldException;
import com.gmmapowell.geofs.exceptions.GeoFSNoWorldException;

public class SimpleUniverse implements Universe {
	private Map<String, World> worlds = new TreeMap<>();

	@Override
	public World getWorld(String world) {
		World ret = worlds .get(world);
		if (ret == null)
			throw new GeoFSNoWorldException(world);
		return ret;
	}

	public void register(String name, World world) {
		if (worlds.containsKey(name))
			throw new GeoFSDuplicateWorldException(name);
		worlds.put(name, world);
	}
}
