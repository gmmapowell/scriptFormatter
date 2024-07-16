package com.gmmapowell.geofs.doubled;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

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
