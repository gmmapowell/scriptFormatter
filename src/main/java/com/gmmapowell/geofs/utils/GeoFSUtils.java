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

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;
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
		throw new NotImplementedException();
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
			return findRelative(world, region, f, false);
		}
	}

	private static Region findRelative(World world, Region region, File f, boolean startAtWorld) {
		if (f.getParentFile() != null) {
			if (f.getParentFile().isAbsolute() && f.getParentFile().getParentFile() == null)
				region = world.root();
			else
				region = findRelative(world, region, f.getParentFile(), startAtWorld);
		} else {
			// absolute paths - return a root
			if (f.isAbsolute()) {
				return world.root();
			} else if (f.getName().endsWith(":")) {
				return world.root(f.getName());
			}
			// else relative paths - fall through
		}
		return region.subregion(f.getName());
	}

	public static Place placePath(World world, Region lfsRegion, String path) {
		File f = new File(path);
		if (f.isAbsolute())
			;
		throw new NotImplementedException();
	}
}
