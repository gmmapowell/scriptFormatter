package com.gmmapowell.script.config;

import java.io.IOException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;

public class ReadConfig {

	private World world;

	public ReadConfig(World world) {
		this.world = world;
	}

	public Config read(String file) {
		Place place = world.placePath(file);
		// This should be a catch exception
//		if (!file.exists()) {
//			System.out.println("There is no file " + file);
//			return null;
//		}
		Region root = place.region();
		ConfigParser parser = new ConfigParser(world.getUniverse(), root);
		place.lines(parser);
		try {
			return parser.config();
		} catch (IOException ex) {
			System.out.println("Could not read configuration " + file);
			return null;
		} catch (ConfigException ex) {
			System.out.println(ex.getMessage());
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
