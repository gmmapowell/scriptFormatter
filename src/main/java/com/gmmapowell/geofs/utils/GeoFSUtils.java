package com.gmmapowell.geofs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LFSRegion;

public class GeoFSUtils {

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
}
