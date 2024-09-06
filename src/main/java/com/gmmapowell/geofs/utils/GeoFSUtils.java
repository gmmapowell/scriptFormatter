package com.gmmapowell.geofs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.xml.XML;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.gdw.GDWPlace;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LFSRegion;

public class GeoFSUtils {
	private static Pattern uriStyle = Pattern.compile("([a-z0-9]+)://(.*)");
	
	static class RegionName {
		Region r;
		String n;
		
		public RegionName(Region r, String n) {
			this.r = r;
			this.n = n;
		}
	}

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

	public static Writer fileAppender(Place file) {
		if (file instanceof LFSPlace)
			return ((LFSPlace)file).appender();
		else
			throw new NotImplementedException("no appender for " + file.getClass());
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

	public static String getGoogleID(Place local) {
		if (local instanceof GDWPlace)
			return ((GDWPlace)local).googleID();
		else
			throw new NotImplementedException();
	}

	public static Region regionPath(World world, Region region, String path) {
		RegionName rn = obtainParentRegion(world, region, path);
		return rn.r.subregion(rn.n);
	}

	public static Region newRegionPath(World world, Region region, String path) {
		RegionName rn = obtainParentRegion(world, region, path);
		return rn.r.newSubregion(rn.n);
	}

	public static Region ensureRegionPath(Region root, String string) {
		throw new NotImplementedException();
	}

	private static RegionName obtainParentRegion(World world, Region region, String path) {
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
		String name = f.getName();
		f = f.getParentFile();
		Region r;
		if (f == null) {
			if (region == null) {
				throw new GeoFSNoRegionException(path);
			}
			r = region;
		} else if (f.isAbsolute()) {
			r = findRelative(other, region, f, true);
		} else {
			// 2024-09-06: I commented this out because it didn't correctly handle "~/.../" in CollectBlogs
//			if (other != world) {
//				throw new GeoFSInvalidWorldException();
//			}
//			// start at region
//			if (region == null) {
//				throw new GeoFSNoRegionException(path);
//			}
			r = findRelative(world, region, f, false);
		}
		return new RegionName(r, name);
	}

	public static Place placePath(World world, Region region, String path) {
		BiFunction<Region, String, Place> resolve = (r,n) -> r.place(n);
		return findPlace(world, region, path, resolve);
	}
	
	public static Place newPlacePath(World world, Region region, String path) {
		BiFunction<Region, String, Place> resolve = (r,n) -> r.newPlace(n);
		return findPlace(world, region, path, resolve);
	}

	public static Place ensurePlacePath(World world, Region region, String path) {
		BiFunction<Region, String, Place> resolve = (r,n) -> r.ensurePlace(n);
		return findPlace(world, region, path, resolve);
	}

	public static Place findPlace(World world, Region region, String path, BiFunction<Region, String, Place> resolve) {
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
		Region r;
		if (f.isAbsolute()) { 
			r = findRelative(other, region, f.getParentFile(), true);
		} else if (other != world) {
			throw new GeoFSInvalidWorldException();
		} else if (f.getParentFile() != null) {
			r = findRelative(world, region, f.getParentFile(), false);
		} else {
			if (region == null) {
				throw new GeoFSNoRegionException(path);
			}
			r = region;
		}
		return resolve.apply(r, f.getName());
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

	public static JSONObject readJSON(Place p) throws JSONException {
		return new JSONObject(p.read());
	}

	public static XML readXML(Place xml) {
		return XML.fromStream(xml.name(), xml.input());
	}
}
