package com.gmmapowell.geofs.git;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class GitPlace implements Place {
	private final GitRoot root;
	private final GitRegion inRegion;
	private final String name;

	public GitPlace(GitRoot root, GitRegion gitRegion, String name) {
		this.root = root;
		this.inRegion = gitRegion;
		this.name = name;
	}

	@Override
	public String read() {
		throw new NotImplementedException();
	}

	@Override
	public void lines(LineListener lsnr) {
		try {
			GeoFSUtils.linesTo(reader(), lsnr, null);
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public void lines(NumberedLineListener lsnr) {
		try {
			GeoFSUtils.linesTo(reader(), null, lsnr);
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public void binary(BinaryBlockListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public void chars(CharBlockListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public Reader reader() {
		return root.reader(inRegion.placeFile(name));
	}

	@Override
	public InputStream input() {
		throw new NotImplementedException();
	}

	@Override
	public OutputStream stream() {
		throw new NotImplementedException();
	}

	@Override
	public Writer writer() {
		throw new NotImplementedException();
	}

	@Override
	public Writer appender() {
		throw new NotImplementedException();
	}

	@Override
	public void store(String contents) {
		throw new NotImplementedException();
	}

	@Override
	public Region region() {
		throw new NotImplementedException();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public boolean exists() {
		throw new NotImplementedException();
	}

	@Override
	public void copyTo(Place to) {
		throw new NotImplementedException();
	}
	
	@Override
	public String toString() {
		return inRegion + "/" + name;
	}
}
