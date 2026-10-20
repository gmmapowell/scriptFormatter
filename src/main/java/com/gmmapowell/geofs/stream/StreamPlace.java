package com.gmmapowell.geofs.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Date;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public class StreamPlace implements Place {
	private final OutputStream os;

	public StreamPlace(OutputStream os) {
		this.os = os;
	}

	@Override
	public String read() {
		throw new NotImplementedException();
	}

	@Override
	public void lines(LineListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public void lines(NumberedLineListener lsnr) {
		throw new NotImplementedException();
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
	public InputStream input() {
		throw new NotImplementedException();
	}

	@Override
	public OutputStream stream() {
		return os;
	}

	@Override
	public Reader reader() {
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
		throw new NotImplementedException();
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
	public void copyBinary(Place to) {
		throw new NotImplementedException();
	}

	@Override
	public URI uri() {
		throw new NotImplementedException();
	}

	@Override
	public Date lastModified() {
		throw new NotImplementedException();
	}

	@Override
	public long size() {
		throw new NotImplementedException();
	}

	@Override
	public void writeTo(Writer writer) {
		throw new NotImplementedException();
	}
}
