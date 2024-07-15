package com.gmmapowell.geofs.doubled;

import java.io.Reader;
import java.io.StringReader;

import com.gmmapowell.geofs.Place;

public class PlaceString extends PlaceDouble implements RegionPlace, Place {
	private final String contents;

	public PlaceString(String contents) {
		this.contents = contents;
	}

	@Override
	protected Reader contents() {
		return new StringReader(contents);
	}
}
