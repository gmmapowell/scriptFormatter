package com.gmmapowell.geofs.doubled;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;

public class PlaceString extends PlaceDouble implements RegionPlace, Place {
	private final String contents;

	public PlaceString(String contents) {
		this.contents = contents;
	}

	@Override
	protected Reader textContents() {
		return new StringReader(contents);
	}

	@Override
	protected InputStream binaryContents() {
		throw new CantHappenException("this is not a binary place");
	}
}
