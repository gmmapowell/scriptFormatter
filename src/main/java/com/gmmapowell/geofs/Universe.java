package com.gmmapowell.geofs;

public interface Universe {

	World getWorld(String world);

	void register(String name, World world);

	Region regionPath(String uri);
	Place placePath(String uri);

	Place newPlacePath(String uri);
	Region newRegionPath(String uri);

	Region ensureRegionPath(String inputs);

	void prepareWorlds() throws Exception;
}
