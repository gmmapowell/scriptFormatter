package com.gmmapowell.geofs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.function.BiFunction;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.xml.XML;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSNoPlaceException;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.gdw.GDWPlace;
import com.gmmapowell.geofs.git.GitPlace;
import com.gmmapowell.geofs.git.GitRegion;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LFSRegion;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public class GeoFSUtils {
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
		} else if (r instanceof GitRegion) {
			GitRegion gr = (GitRegion) r;
			return new File(gr.repo());
		} else {
			throw new CantHappenException("can't return file at " + r.getClass());
		}
	}

	public static File file(Place from) {
		if (from instanceof LFSPlace) {
			return ((LFSPlace)from).getFile();
		} else if (from instanceof GitPlace) {
			GitRegion gr = (GitRegion) from.region();
			return gr.placeFile(((GitPlace)from).name());
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
		if (rn.n == null)
			return rn.r;
		else
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
		WorldPath wp = WorldPath.parse(world, path);
		Region r = figureParentPath(wp, region);
		return new RegionName(r, wp.childSegment());
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
		WorldPath wp = WorldPath.parse(world, path);
		Region r = figureParentPath(wp, region);
		if (wp.childSegment() == null)
			throw new GeoFSNoPlaceException(path);
		return resolve.apply(r, wp.childSegment());
	}

	private static Region figureParentPath(WorldPath wp, Region region) {
		Region r;
		if (wp.isAbsolute()) {
			r = wp.root();
		} else {
			wp.assertSameWorld();
			if (region == null)
				throw new GeoFSNoRegionException(null);
			r = region;
		}
		return findRelative(r, wp.parentSegments());
	}

	private static Region findRelative(Region region, List<String> segments) {
		for (String s : segments)
			region = region.subregion(s);
		return region;
	}
	
	public static void linesTo(Reader reader, LineListener lsnr, NumberedLineListener nlsnr) throws IOException {
		LineNumberReader lnr = new LineNumberReader(reader);
		String s;
		while ((s = lnr.readLine()) != null) {
			if (s.endsWith("\r"))
				s = s.substring(0, s.length()-1);
			if (lsnr != null)
				lsnr.line(s);
			else
				nlsnr.line(lnr.getLineNumber(), s);
		}
		if (lsnr != null)
			lsnr.complete();
		else
			nlsnr.complete();
	}

	public static JSONObject readJSON(Place p) throws JSONException {
		return new JSONObject(p.read());
	}

	public static XML readXML(Place xml) {
		return XML.fromStream(xml.name(), xml.input());
	}

	public static String gitTag(Place place) {
		if (place instanceof GitPlace) {
			GitPlace gp = (GitPlace) place;
			GitRegion gr = (GitRegion) gp.region();
			return gr.tag();
		} else {
			return "";
		}
	}
}
