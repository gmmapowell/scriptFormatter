package com.gmmapowell.geofs.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;

public class WorldPath {
	private static Pattern uriStyle = Pattern.compile("([a-z0-9]+):(.*)");
	private static Pattern uriStyleWithRoot = Pattern.compile("([a-z0-9]+):(.*):([^:]*)");

	private World orig;
	World inWorld;
	String root;
	String path;
	File f;
	private boolean isAbsolute;
	private String childSegment;
	private List<String> parentSegments;

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
		return new WorldPath(world, other, root, path, parseSegments(path));
	}

	private static List<String> parseSegments(String path) {
		File f = new File(path);
		List<String> ret = new ArrayList<>();
		while (f != null) {
			String name = f.getName();
			if (name.equals(""))
				name = "/";
			ret.add(0, name);
			f = f.getParentFile();
		}
		return ret;
	}

	private WorldPath(World orig, World use, String root, String path, List<String> segments) {
		this.orig = orig;
		this.inWorld = use;
		this.root = root;
		this.path = path;
		this.childSegment = segments.remove(segments.size()-1);
		this.parentSegments = segments;
		boolean useChild = segments.isEmpty();
		String fst = useChild ? childSegment : segments.get(0);
		if (fst.equals("/") || fst.startsWith("~") || fst.endsWith(":")) {
			if (fst.endsWith(":"))
				this.root = fst.replace(":", "");
			else if (!"/".equals(fst))
				this.root = fst;
			this.isAbsolute = true;
			if (useChild)
				this.childSegment = null;
			else {
				segments.remove(0);
			}
		} else {
			this.isAbsolute = false;
		}
	}

	public String getName() {
		return f.getName();
	}

	public File getParentFile() {
		return f.getParentFile();
	}

	public void assertSameWorld() {
		if (inWorld != orig) {
			throw new GeoFSInvalidWorldException();
		}
	}

	public World world() {
		return inWorld;
	}

	public List<String> parentSegments() {
		return parentSegments;
	}

	public String childSegment() {
		return childSegment;
	}

	public boolean isAbsolute() {
		if (inWorld != orig || root != null) {
			if (!isAbsolute)
				throw new GeoFSInvalidWorldException();
			return true;
		} else {
			return isAbsolute;
		}
	}

	public Region root() {
		if (root == null)
			return inWorld.root();
		else
			return inWorld.root(root);
	}

}
