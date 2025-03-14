package com.gmmapowell.geofs.gdw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.geofs.utils.LineListenerOutputStream;
import com.google.api.services.drive.Drive;

public class GDWPlace implements Place {
	private final Drive service;
	private final String name;
	private final String id;
	private final GDWRegion region;

	public GDWPlace(Drive service, String id, String name, GDWRegion region) {
		this.service = service;
		this.name = name;
		this.id = id;
		this.region = region;
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String read() {
		throw new NotImplementedException();
	}
	
	@Override
	public void lines(LineListener lsnr) {
		try {
			OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
			service.files().export(id, "text/plain").executeMediaAndDownloadTo(os);
			os.close();
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public InputStream input() {
		throw new NotImplementedException();
	}

	@Override
	public Reader reader() {
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
	public Region region() {
		throw new NotImplementedException();
	}

	@Override
	public boolean exists() {
		throw new NotImplementedException();
	}
	
	@Override
	public void copyTo(Place to) {
		try {
			GeoFSUtils.ensureRegionExists(to.region());
			service.files().export(id, "text/plain").executeMediaAndDownloadTo(GeoFSUtils.saveStreamTo(to));
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	public String googleID() {
		return id;
	}
	
	@Override
	public String toString() {
		return region.toString() + "/" + name;
	}
}
