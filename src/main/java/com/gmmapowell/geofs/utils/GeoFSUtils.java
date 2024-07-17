package com.gmmapowell.geofs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LFSRegion;

public class GeoFSUtils {

	public static Reader fileReader(Place p) throws FileNotFoundException {
		if (p instanceof LFSPlace) {
			return new FileReader(((LFSPlace)p).getFile());
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
}
