package com.gmmapowell.geofs;

public interface Universe {

	World getWorld(String world);

	void register(String name, World world);

	Region regionPath(String uri);

	void prepareWorlds() throws Exception;
}
