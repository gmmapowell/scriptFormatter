package com.gmmapowell.geofs;

public interface Universe {

	World getWorld(String world);

	void register(String name, World world);

}
