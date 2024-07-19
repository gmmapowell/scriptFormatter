package com.gmmapowell.geofs.doubled;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;

public class PlaceByteArray extends PlaceDouble implements RegionPlace, Place {
	private final byte[] contents;

	public PlaceByteArray(byte[] contents) {
		this.contents = contents;
	}

	@Override
	protected Reader textContents() {
		throw new CantHappenException("this is a binary place");
	}

	@Override
	protected InputStream binaryContents() {
		return new ByteArrayInputStream(contents);
	}
}
