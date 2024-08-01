package com.gmmapowell.geofs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;
import com.gmmapowell.geofs.gdw.GDWPlace;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LFSRegion;

public class GeoFSUtils {
	private static Pattern uriStyle = Pattern.compile("([a-z0-9]+)://(.*)");

	public static Reader fileReader(Place p) throws FileNotFoundException {
		if (p instanceof LFSPlace) {
			return new FileReader(((LFSPlace)p).getFile());
		} else if (p == null) {
			throw new CantHappenException("place was null");
		} else {
			throw new CantHappenException("can't read file at " + p.getClass());
		}
	}

	public static File file(Region r) {
		if (r instanceof LFSRegion) {
			return ((LFSRegion)r).getFile();
		} else {
			throw new CantHappenException("can't return file at " + r.getClass());
		}
	}

	public static File file(Place from) {
		if (from instanceof LFSPlace) {
			return ((LFSPlace)from).getFile();
		} else
			throw new NotImplementedException("file(" + from.getClass() + ")");
	}

	public static FileWriter fileAppender(Place postsFile) {
		throw new NotImplementedException();
	}

	public static OutputStream saveStreamTo(Place dest) {
		try {
			if (dest instanceof LFSPlace)
				return new FileOutputStream(((LFSPlace)dest).getFile());
			else
				throw new NotImplementedException("cannot handle " + dest.getClass());
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	public static Region ensureRegionPath(Region root, String string) {
		throw new NotImplementedException();
	}

	public static String getGoogleID(Place local) {
		if (local instanceof GDWPlace)
			return ((GDWPlace)local).googleID();
		else
			throw new NotImplementedException();
	}

	public static Region regionPath(World world, Region region, String path) {
		Matcher isUri = uriStyle.matcher(path);
		World other = world;
		if (isUri.matches()) {
			path = isUri.group(2);
			Universe u = world.getUniverse();
			if (u == null) {
				throw new CantHappenException("the World is not part of a Universe");
			}
			other = u.getWorld(isUri.group(1));
		}
		File f = new File(path);
		if (f.isAbsolute()) {
			return findRelative(other, region, f, true);
		} else {
			if (other != world) {
				throw new GeoFSInvalidWorldException();
			}
			// start at region
			if (region == null) {
				throw new GeoFSNoRegionException(path);
			}
			return findRelative(world, region, f, false);
		}
	}

	public static Place placePath(World world, Region region, String path) {
		Matcher isUri = uriStyle.matcher(path);
		World other = world;
		if (isUri.matches()) {
			path = isUri.group(2);
			Universe u = world.getUniverse();
			if (u == null) {
				throw new CantHappenException("the World is not part of a Universe");
			}
			other = u.getWorld(isUri.group(1));
		}
		File f = new File(path);
		if (f.isAbsolute()) { 
			return findRelative(other, region, f.getParentFile(), true).place(f.getName());
		} else if (other != world) {
			throw new GeoFSInvalidWorldException();
		} else if (f.getParentFile() != null) {
			return findRelative(world, region, f.getParentFile(), false).place(f.getName());
		} else {
			if (region == null) {
				throw new GeoFSNoRegionException(path);
			}
			return region.place(f.getName());
		}
	}
	
	private static Region findRelative(World world, Region region, File f, boolean startAtWorld) {
		if (f.getParentFile() != null) {
			region = findRelative(world, region, f.getParentFile(), startAtWorld);
		} else {
			if (f.isAbsolute()) { // absolute paths - return a root
				return world.root();
			} else if (f.getName().endsWith(":")) { // windows drive letters - are absolute, but Java doesn't know it
				return world.root(f.getName());
			} else if (f.getName().startsWith("~")) { // ~ represents a home directory - like a root, and thus absolute, but Java doesn't know it
				return world.root(f.getName());
			}
			// else relative paths - fall through
		}
		return region.subregion(f.getName());
	}

	public static JSONObject readJSON(Place metafile) throws JSONException {
		throw new NotImplementedException();
	}
}
