package com.gmmapowell.geofs.lfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSNoPlaceException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class LFSPlace implements Place {
	private final LocalFileSystem world;
	private final LFSRegion region;
	protected final File file;

	public LFSPlace(LocalFileSystem world, LFSRegion region, File file) {
		this.world = world;
		this.region = region;
		if (!file.isFile())
			throw new CantHappenException("there is no file " + file);
		this.file = file;
	}

	protected LFSPlace(LocalFileSystem world, LFSRegion region, File file, boolean notExists) {
		this.world = world;
		this.region = region;
		if (!notExists)
			throw new CantHappenException("this constructor must be called with notExists = true");
		if (file.exists())
			throw new CantHappenException("the file exists (may be directory): " + file);
		this.file = file;
	}

	@Override
	public Region region() {
		if (region != null)
			return region;
		return new LFSRegion(world, file.getParentFile());
	}
	
	@Override
	public String name() {
		return file.getName();
	}
	
	@Override
	public String read() {
		if (!file.exists())
			throw new GeoFSNoPlaceException(file.getPath());
		return FileUtils.readFile(file);
	}
	
	@Override
	public void lines(LineListener lsnr) {
		streamLines(lsnr, null);
	}

	@Override
	public void lines(NumberedLineListener lsnr) {
		streamLines(null, lsnr);
	}
	
	private void streamLines(LineListener lsnr, NumberedLineListener nlsnr) {
		try (FileReader reader = new FileReader(file)) {
			GeoFSUtils.linesTo(reader, lsnr, nlsnr);
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public Reader reader() {
		try {
			return new FileReader(file);
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}
	
	@Override
	public InputStream input() {
		try {
			return new FileInputStream(file);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public OutputStream stream() {
		if (!file.exists()) {
			createFile();
		}
		try {
			return new FileOutputStream(file);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Writer writer() {
		if (!file.exists()) {
			createFile();
		}
		try {
			return new FileWriter(file);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Writer appender() {
		if (!file.exists()) {
			createFile();
		}
		try {
			return new FileWriter(file, true);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public void store(String contents) {
		FileUtils.writeFile(file, contents);
	}
	
	@Override
	public void copyTo(Place to) {
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

	public File getFile() {
		return file;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	protected void createFile() {
		throw new GeoFSNoPlaceException(file.getPath());
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
