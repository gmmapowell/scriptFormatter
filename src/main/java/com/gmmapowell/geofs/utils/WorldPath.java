package com.gmmapowell.geofs.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;

public class WorldPath {
	private static Pattern uriStyle = Pattern.compile("([a-z0-9]+)://(.*)");
	private static Pattern uriStyleWithRoot = Pattern.compile("([a-z0-9]+)://(.*):([^:]*)");

	World inWorld;
	String root;
	String path;
	File f;

	public static WorldPath parse(World world, String path) {
		Matcher isUri = uriStyle.matcher(path);
		World other = world;
		String root = null;
		if (isUri.matches()) {
			Matcher multiParts = uriStyleWithRoot.matcher(path);
			if (multiParts.matches()) {
				root = multiParts.group(2);
				path = multiParts.group(3);
			} else {
				path = isUri.group(2);
			}
			Universe u = world.getUniverse();
			if (u == null) {
				throw new CantHappenException("the World is not part of a Universe");
			}
			other = u.getWorld(isUri.group(1));
		}
		File f = new File(path);
		return new WorldPath(other, root, path, f);
	}

	private WorldPath(World other, String root, String path, File f) {
		this.inWorld = other;
		this.root = root;
		this.path = path;
		this.f = f;
	}

	public String getName() {
		return f.getName();
	}

	public File getParentFile() {
		return f.getParentFile();
	}

	public void assertSameWorld(World world) {
		if (inWorld != world) {
			throw new GeoFSInvalidWorldException();
		}
	}

	public World world() {
		return inWorld;
	}

}
